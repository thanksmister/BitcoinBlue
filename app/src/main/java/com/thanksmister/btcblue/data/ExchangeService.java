/*
 * Copyright (c) 2014. ThanksMister LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thanksmister.btcblue.data;

import android.content.SharedPreferences;

import com.thanksmister.btcblue.data.api.BitcoinAverage;
import com.thanksmister.btcblue.data.api.Bluelytics;
import com.thanksmister.btcblue.data.api.model.Bluelytic;
import com.thanksmister.btcblue.data.api.model.Exchange;
import com.thanksmister.btcblue.data.api.transforms.ResponseToExchangeList;
import com.thanksmister.btcblue.data.prefs.StringPreference;
import com.thanksmister.btcblue.utils.Doubles;

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
    public static final String PREFS_SELECTED_EXCHANGE = "selected_exchange";

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
        this.bluelyticsList = new ArrayList<Bluelytic>();
    }
    
    /*public Subscription getExchanges(final RefreshObserver<List<Exchange>> observer, final int startDelay)
    {
        Timber.d("getExchanges");
        
        if(exchangesRequest != null) { // join the request
            return exchangesRequest.subscribe(observer);
        }

        exchangesRequest = PublishSubject.create();  
        exchangesRequest.subscribe(new Observer<List<Exchange>>() {
            @Override
            public void onCompleted() {

                Timber.d("Exchanges Complete");
                //exchangesRequest = null;
                observer.onRefreshEnd();
            }

            @Override
            public void onError(Throwable throwable) {

                Timber.d("Exchanges Error");
                //exchangesRequest = null;
                observer.onRefreshEnd();
                //getExchangesFromDatabase(observer); // return exchanges now if we have them
                exchangesRequest = null;
                getExchanges(observer, 30000);
            }

            @Override 
            public void onNext(List<Exchange> exchanges) {

                Timber.d("Exchanges Returned");
                
                if(exchanges != null) {       
                    Collections.sort(exchanges, new ExchangeNameComparator());
                    String date = Dates.getLocalDateMilitaryTime(); 
                    for (Exchange exchange:exchanges) {
                        exchange.date = date; // set everything to the same time
                    }
                   
                    setExchangeExpireTime();
                }

                observer.onRefreshEnd();
            }
        });

        Timber.d("Create Scheduler");
        Subscription subscription = exchangesRequest.subscribe(observer);
        Observable.create(new Observable.OnSubscribe<List<Exchange>>()
        {
            @Override
            public void call(Subscriber<? super List<Exchange>> subscriber)
            {
                Schedulers.io().createWorker()
                        .schedulePeriodically(new Action0()
                        {
                            @Override
                            public void call()
                            {
                                Timber.d("Periodically");
                                observer.onRefreshStart();

                                getExchangesObservable().doOnNext(new Action1<List<Exchange>>()
                                {
                                    @Override
                                    public void call(List<Exchange> exchanges)
                                    {
                                        Timber.d("On Next happened");
                                        subscriber.onNext(exchanges);
                                    }
                                }).doOnError(new Action1<Throwable>()
                                {
                                    @Override
                                    public void call(Throwable throwable)
                                    {
                                        subscriber.onError(throwable);
                                    }
                                }).subscribe();

                            }
                        }, startDelay, CHECK_EXCHANGE_DATA, TimeUnit.MILLISECONDS);
            }
        })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(exchangesRequest);
            
        *//*getExchangesObservable()
        .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(exchangesRequest);*//*
        
        return subscription;
    }*/

    public void setSelectedExchange(String name)
    {
        StringPreference preference = new StringPreference(sharedPreferences, PREFS_SELECTED_EXCHANGE, "Bitstamp");
        preference.set(name);
    }

    public String getSelectedExchangeName()
    {
        StringPreference preference = new StringPreference(sharedPreferences, PREFS_SELECTED_EXCHANGE, "Bitstamp");
        Timber.d("Selected Name: " + preference.get());
        if(preference.get().equals("")) return "Bitstamp";
        return preference.get();
    }

    private Observable<List<Exchange>> getBluelyticsSubscription(final List<Exchange> exchanges)
    {
        return blueLytics.lastPrice()
                .map(new Func1<List<Bluelytic>, List<Exchange>>()
                {
                    @Override
                    public List<Exchange> call(List<Bluelytic> bluelytics)
                    {
                        bluelyticsList = bluelytics;
                        setDollarBlueExpireTime();
                        return setBlueDollarValues(bluelytics, exchanges);
                    }
                });
    }
    
    private List<Exchange> setBlueDollarValues(List<Bluelytic> bluelytics, List<Exchange> exchanges)
    {
        double official_value_sell = 0;
        double official_value_buy = 0;
        double blue_value_sell = 0;
        double blue_value_buy = 0;

        for (Bluelytic bluelytic : bluelytics) {
            if (bluelytic.source.equals("oficial")) {
                official_value_sell += Doubles.convertToDouble(bluelytic.value_sell);
                official_value_buy += Doubles.convertToDouble(bluelytic.value_buy);
            } else {
                blue_value_sell += Doubles.convertToDouble(bluelytic.value_sell);
                blue_value_buy += Doubles.convertToDouble(bluelytic.value_buy);
            }
        }

        int exchange_size = bluelytics.size() - 1;
        for (Exchange exchange : exchanges) {
            exchange.setBlue_ask(String.valueOf(blue_value_sell / exchange_size));
            exchange.setBlue_bid(String.valueOf(blue_value_buy / exchange_size));
            exchange.setOfficial_ask(String.valueOf(official_value_sell));
            exchange.setOfficial_bid(String.valueOf(official_value_buy));
        }
        
        return  exchanges;
    }

    public Observable<List<Exchange>> getExchangesObservable()
    {    
        return bitcoinAverage.exchanges(USD)
                .map(new ResponseToExchangeList())
                .flatMap(new Func1<List<Exchange>, Observable<List<Exchange>>>()
                {
                    @Override
                    public Observable<List<Exchange>> call(List<Exchange> exchanges)
                    {
                        //setExchangeExpireTime();
                        
                        if (exchanges == null) {
                            return Observable.empty();
                        }

                        if (needToRefreshDollarBlue()) {
                            return getBluelyticsSubscription(exchanges);
                        }

                        return Observable.just(setBlueDollarValues(bluelyticsList, exchanges));
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
