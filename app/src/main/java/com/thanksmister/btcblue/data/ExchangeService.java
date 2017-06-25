/*
 * <!--
 *   ~ Copyright (c) 2017. ThanksMister LLC
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License. 
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~ http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software distributed 
 *   ~ under the License is distributed on an "AS IS" BASIS, 
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *   ~ See the License for the specific language governing permissions and 
 *   ~ limitations under the License.
 *   -->
 */

package com.thanksmister.btcblue.data;

import android.content.SharedPreferences;

import com.thanksmister.btcblue.Constants;
import com.thanksmister.btcblue.data.api.BitcoinAverage;
import com.thanksmister.btcblue.data.api.Bluelytics;
import com.thanksmister.btcblue.data.api.model.Bluelytic;
import com.thanksmister.btcblue.data.api.model.Exchange;
import com.thanksmister.btcblue.data.api.transforms.ResponseToBluelytics;
import com.thanksmister.btcblue.data.api.transforms.ResponseToExchange;
import com.thanksmister.btcblue.data.api.transforms.ResponseToString;
import com.thanksmister.btcblue.data.prefs.StringPreference;
import com.thanksmister.btcblue.utils.Doubles;
import com.thanksmister.btcblue.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

@Singleton
public class ExchangeService
{
    public static final String PREFS_DOLLAR_BLUE_EXPIRE_TIME = "pref_dollar_blue_expire";
    public static final String PREFS_EXCHANGE_EXPIRE_TIME = "pref_exchange_expire";
    public static final String PREFS_SELECTED_EXCHANGE = "selected_exchange_source";

    public static final int CHECK_EXCHANGE_DATA = 2 * 60 * 1000;// 5 minutes
    public static final int CHECK_BLUE_DOLLAR_DATA = 60 * 60 * 1000;// 1 HOUR
    
    public static final String USD = "USD";
    
    private final BitcoinAverage bitcoinAverage;
    private final Bluelytics blueLytics;
    private final SharedPreferences sharedPreferences;
    private List<Bluelytic> bluelyticsList;
    
    @Inject
    public ExchangeService(SharedPreferences sharedPreferences, BitcoinAverage bitcoinAverage, Bluelytics blueLytics)
    {
        this.bitcoinAverage = bitcoinAverage;
        this.blueLytics = blueLytics;
        this.sharedPreferences = sharedPreferences;
        this.bluelyticsList = new ArrayList<>();
    }
   
    public void setSelectedExchange(String name)
    {
        StringPreference preference = new StringPreference(sharedPreferences, PREFS_SELECTED_EXCHANGE, "bitstamp");
        preference.set(name);
    }

    public String getSelectedExchangeName() {
        StringPreference preference = new StringPreference(sharedPreferences, PREFS_SELECTED_EXCHANGE, "bitstamp");
        Timber.d("Selected Name: " + preference.get());
        if(preference.get().equals("")) return "bitstamp";
        return preference.get().toLowerCase();
    }

    private Observable<Exchange> getBluelyticsSubscription(final Exchange exchange)
    {
        return blueLytics.latestPrice()
                .map(new ResponseToBluelytics())
                .flatMap(new Func1<List<Bluelytic>, Observable<Exchange>>()
                {
                    @Override
                    public Observable<Exchange> call(List<Bluelytic> bluelytics)
                    {
                        bluelyticsList = bluelytics;
                        setDollarBlueExpireTime();
                        return Observable.just(setBlueDollarValues(bluelytics, exchange));
                    }
                });
    }
    
    private Exchange setBlueDollarValues(List<Bluelytic> bluelytics, Exchange exchange)
    {
        double official_value_sell = 0;
        double official_value_buy = 0;
        double blue_value_sell = 0;
        double blue_value_buy = 0;

        // we only care about the oficial and the average blue rate
        for (Bluelytic bluelytic : bluelytics) {
            if (bluelytic.source.equals("oficial")) {
                official_value_sell = Doubles.convertToDouble(bluelytic.value_sell);
                official_value_buy = Doubles.convertToDouble(bluelytic.value_buy);
            } else if (bluelytic.source.equals("blue")) {
                blue_value_sell = Doubles.convertToDouble(bluelytic.value_sell);
                blue_value_buy = Doubles.convertToDouble(bluelytic.value_buy);
            }
        }

        exchange.setBlue_ask(String.valueOf(blue_value_sell));
        exchange.setBlue_bid(String.valueOf(blue_value_buy));
        exchange.setOfficial_ask(String.valueOf(official_value_sell));
        exchange.setOfficial_bid(String.valueOf(official_value_buy));
        
        return  exchange;
    }

    public Observable<String> getServerTime() {
        return bitcoinAverage.serverTime()
                .map(new ResponseToString())
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        Timber.d("Server time: " + s);
                        return Observable.just(s);
                    }
                });
    }

    public Observable<Exchange> getExchangeObservable(final String exchange) {
        return bitcoinAverage.serverTime()
                .map(new ResponseToString())
                .flatMap(new Func1<String, Observable<Exchange>>() {
                    @Override
                    public Observable<Exchange> call(String serverTime) {
                        String nonce = NetworkUtils.generateNonce();
                        Timber.d("nonce: " + nonce);
                        try {
                            JSONObject jsonObject = new JSONObject(serverTime);
                            serverTime = jsonObject.getString("epoch");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Timber.d("Server time: " + serverTime);
                        String signature = NetworkUtils.createSignature(serverTime, Constants.PUBLIC_KEY, Constants.SECRET);
                        return bitcoinAverage.perExchangeData(signature, exchange)
                                .map(new ResponseToExchange())
                                .flatMap(new Func1<Exchange, Observable<Exchange>>() {
                                    @Override
                                    public Observable<Exchange> call(Exchange exchange) {
                                        //setExchangeExpireTime();
                                        if (exchange == null) {
                                            return Observable.empty();
                                        }

                                        if (needToRefreshDollarBlue()) {
                                            return getBluelyticsSubscription(exchange);
                                        }

                                        return Observable.just(setBlueDollarValues(bluelyticsList, exchange));
                                    }
                                });
                    }
                });
    }

    private class ExchangeNameComparator implements Comparator<Exchange>
    {
        @Override
        public int compare(Exchange o1, Exchange o2) {
            return o1.getDisplay_name().toLowerCase().compareTo(o2.getDisplay_name().toLowerCase());
        }
    }

    private void setDollarBlueExpireTime()
    {
        synchronized (this) {
            long expire = System.currentTimeMillis() + CHECK_BLUE_DOLLAR_DATA; // 1 hours
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(PREFS_DOLLAR_BLUE_EXPIRE_TIME, expire);
            editor.apply();
        }
    }

    private void setExchangeExpireTime()
    {
        synchronized (this) {
            long expire = System.currentTimeMillis() + CHECK_EXCHANGE_DATA; // 1 hours
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(PREFS_EXCHANGE_EXPIRE_TIME, expire);
            editor.apply();
        }
    }
    
    private boolean needToRefreshDollarBlue()
    {
        if(bluelyticsList == null || bluelyticsList.isEmpty()) 
            return true;
        
        synchronized (this) {
            long expiresAt = sharedPreferences.getLong(PREFS_DOLLAR_BLUE_EXPIRE_TIME, -1);
            return System.currentTimeMillis() >= expiresAt;
        }
    }

    private boolean needToRefreshExchanges()
    {
        synchronized (this) {
            long expiresAt = sharedPreferences.getLong(PREFS_EXCHANGE_EXPIRE_TIME, -1);
            return System.currentTimeMillis() >= expiresAt;
        }
    }
}
