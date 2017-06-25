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

package com.thanksmister.btcblue.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.thanksmister.btcblue.R;
import com.thanksmister.btcblue.data.ExchangeService;
import com.thanksmister.btcblue.data.api.model.DisplayExchange;
import com.thanksmister.btcblue.data.api.model.Exchange;
import com.thanksmister.btcblue.db.DbManager;
import com.thanksmister.btcblue.ui.spinner.ExchangeAdapter;
import com.thanksmister.btcblue.utils.Calculations;
import com.thanksmister.btcblue.utils.Conversions;
import com.thanksmister.btcblue.utils.Dates;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

import static rx.android.app.AppObservable.bindActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {
    @Inject
    ExchangeService exchangeService;

    @Inject
    DbManager dbManager;

    @InjectView(R.id.dataFrom)
    TextView dataFrom;

    @InjectView(R.id.date)
    TextView date;

    @InjectView(R.id.averageUSDB)
    TextView averageUSDB;

    @InjectView(R.id.titleUSDB)
    TextView titleUSDB;

    @InjectView(R.id.askUSDB)
    TextView askUSDB;

    @InjectView(R.id.bidUSDB)
    TextView bidUSDB;

    @InjectView(R.id.officialUSDB)
    TextView officialUSDB;

    @InjectView(R.id.averageUSD)
    TextView averageUSD;

    @InjectView(R.id.titleUSD)
    TextView titleUSD;

    @InjectView(R.id.askUSD)
    TextView askUSD;

    @InjectView(R.id.bidUSD)
    TextView bidUSD;

    @InjectView(R.id.officialUSD)
    TextView officialUSD;

    @InjectView(R.id.averageARS)
    TextView averageARS;

    @InjectView(R.id.titleARS)
    TextView titleARS;

    @InjectView(R.id.askARS)
    TextView askARS;

    @InjectView(R.id.bidARS)
    TextView bidARS;

    @InjectView(R.id.officialARS)
    TextView officialARS;

    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    @InjectView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @InjectView(R.id.exchangeSpinner)
    Spinner exchangeSpinner;

    @InjectView(R.id.fab)
    FloatingActionButton fab;

    @InjectView(R.id.content)
    View content;

    Handler handler;
    ExchangeAdapter adapter;

    private Observable<Exchange> exchangeObservable;
    private Observable<Exchange> exchangeUpdateObservable;

    Subscription subscription = Subscriptions.empty();
    Subscription updateSubscription = Subscriptions.empty();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
        handler = new Handler();

        // setup adapter
        adapter = new ExchangeAdapter(this, R.layout.spinner_dropdown);
        setupSpinner();
        setupFab();
        setFromText();
    }

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
        onRefreshStart();
        subscribeData();
        updateData();
    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
        subscription.unsubscribe();
        updateSubscription.unsubscribe();
        handler.removeCallbacks(refreshRunnable);
    }

    @Override
    public void onRefresh() {
        updateData();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            swipeLayout.setEnabled(true);
        } else {
            swipeLayout.setEnabled(false);
        }
    }

    private void onRefreshStart() {
        handler.postDelayed(refreshRunnable, 1000);
    }

    private void onRefreshStop() {
        handler.removeCallbacks(refreshRunnable);
        swipeLayout.setRefreshing(false);
    }

    private void showContent(boolean show) {
        if(content != null) {
            content.setVisibility((show) ? View.VISIBLE : View.GONE);  
        }
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            swipeLayout.setRefreshing(true);
        }
    };

    private void setupFab() {
        fab.setOnClickListener(this);
    }

    private void setupSpinner() {
        List<DisplayExchange> exchanges = generateDisplayExchanges();
        adapter.setData(exchanges);
        exchangeSpinner.setAdapter(adapter);
        exchangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DisplayExchange exchange = (DisplayExchange) adapterView.getAdapter().getItem(i);
                exchangeService.setSelectedExchange(exchange.getExchangeName());
                onResume();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            Intent intent = CalculatorActivity.createStartIntent(MainActivity.this);
            startActivity(intent);
        }
    }

    public void subscribeData() {
        // database data
        exchangeObservable = bindActivity(this, dbManager.exchangeQuery(exchangeService.getSelectedExchangeName()));
        subscription = exchangeObservable.subscribe(new Action1<Exchange>() {
            @Override
            public void call(Exchange exchange) {
                if (exchange != null) {
                    updateSelectedExchange(exchange);
                } 
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                reportError(throwable);
            }
        });
    }

    public void updateData() {
        // update data
        exchangeUpdateObservable = bindActivity(this, exchangeService.getExchangeObservable(exchangeService.getSelectedExchangeName()));
        updateSubscription = exchangeUpdateObservable.subscribe(new Action1<Exchange>() {
            @Override
            public void call(Exchange exchange) {
                onRefreshStop();
                dbManager.updateExchange(exchange);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                onRefreshStop();
                handleError(throwable, getString(R.string.snack_retry), new Action0() {
                    @Override
                    public void call() {
                        updateData();
                    }
                });
            }
        });
    }

    private void updateSelectedExchange(final Exchange exchange) {
        int index = 0;
        List<DisplayExchange> exchanges = generateDisplayExchanges();
        for (DisplayExchange ex : exchanges) {
            if (ex.getExchangeName().equals(exchange.getSource())) {
                break;
            }
            index++;
        }
        exchangeSpinner.setSelection(index);
        setExchange(exchange);
    }

    private void setExchange(Exchange exchange) {
        if (exchange == null) return;
        Timber.e("Exchange date: " + exchange.getCreated_at());
        Timber.e("Exchange source: " + exchange.getSource());
        Timber.e("Exchange ask: " + exchange.getAsk());
        showContent(true);
        setMarketData(exchange, MarketViewType.ARS_USDB);
        setMarketData(exchange, MarketViewType.ARS_BTC);
        setMarketData(exchange, MarketViewType.USD_BTC);
        setDate(exchange);
    }

    private void setDate(Exchange exchange) {
        Dates.DateDistance dateDistance = Dates.getTimeInDistance(exchange.getCreated_at());
        switch (dateDistance) {
            case RECENT:
                date.setText(Html.fromHtml(getString(R.string.date_updated_text_recent, exchange.getCreated_at())));
                break;
            case NOT_SO_MUCH:
                date.setText(Html.fromHtml(getString(R.string.date_updated_text_stale, exchange.getCreated_at())));
                break;
            case LONG_GONE:
                date.setText(Html.fromHtml(getString(R.string.date_updated_text_old, exchange.getCreated_at())));
                break;
        }
    }

    private void setFromText() {
        dataFrom.setText(Html.fromHtml(getString(R.string.data_provided_by)));
        dataFrom.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private enum MarketViewType {
        USD_BTC,
        ARS_BTC,
        ARS_USDB,
        ARS_USD
    }

    public void setMarketData(Exchange exchange, MarketViewType type) {
        String ask = "";
        String bid = "";
        String avg = "";

        switch (type) {
            case ARS_USDB:
                averageUSDB.setText(Calculations.calculateAverageBidAskFormatted(exchange.getBlue_bid(), exchange.getBlue_ask()));
                titleUSDB.setText(getString(R.string.title_ars_usdb));
                askUSDB.setText(Conversions.formatCurrencyAmount(exchange.getBlue_ask()));
                bidUSDB.setText(Conversions.formatCurrencyAmount(exchange.getBlue_bid()));

                ask = Conversions.formatCurrencyAmount(exchange.getOfficial_ask());
                bid = Conversions.formatCurrencyAmount(exchange.getOfficial_bid());
                avg = Calculations.calculateAverageBidAskFormatted(bid, ask);

                officialUSDB.setText(getString(R.string.official_text_usd, avg));
                break;
            case ARS_BTC:

                ask = Calculations.calculateBlueARSFormatted(exchange.getBlue_ask(), exchange.getAsk());
                bid = Calculations.calculateBlueARSFormatted(exchange.getBlue_bid(), exchange.getBid());

                averageARS.setText(Calculations.calculateAverageBidAskFormatted(bid, ask));
                titleARS.setText(getString(R.string.title_ars_btc));

                askARS.setText(Calculations.calculateBlueARSFormatted(exchange.getBlue_ask(), exchange.getAsk()));
                bidARS.setText(Calculations.calculateBlueARSFormatted(exchange.getBlue_bid(), exchange.getBid()));

                ask = Calculations.calculateBlueARSFormatted(exchange.getOfficial_ask(), exchange.getAsk());
                bid = Calculations.calculateBlueARSFormatted(exchange.getOfficial_bid(), exchange.getBid());
                avg = Calculations.calculateAverageBidAskFormatted(bid, ask);

                officialARS.setText(getString(R.string.official_text_btc, avg));
                break;
            case USD_BTC:
                averageUSD.setText(Calculations.calculateAverageBidAskFormatted(exchange.getBid(), exchange.getAsk()));
                titleUSD.setText(getString(R.string.title_usd_btc));
                askUSD.setText(Conversions.formatCurrencyAmount(exchange.getAsk()));
                bidUSD.setText(Conversions.formatCurrencyAmount(exchange.getBid()));
                officialUSD.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * BitcoinAverage removed the ability to get exchanges with a free account.
     * This is breakable if one of the exchanges changes names or is removed.
     * @return
     */
    private List<DisplayExchange> generateDisplayExchanges() {
        List<DisplayExchange> exchanges = new ArrayList<>();
        DisplayExchange exchange = new DisplayExchange();
        
        exchange.setDisplayName("Bitstamp");
        exchange.setExchangeName("bitstamp");
        exchanges.add(exchange);

        exchange = new DisplayExchange();
        exchange.setDisplayName("Bitfinex");
        exchange.setExchangeName("bitfinex");
        exchanges.add(exchange);

        exchange = new DisplayExchange();
        exchange.setDisplayName("Bitsquare");
        exchange.setExchangeName("bitsquare");
        exchanges.add(exchange);

        exchange = new DisplayExchange();
        exchange.setDisplayName("BTC-e");
        exchange.setExchangeName("btce");
        exchanges.add(exchange);

        exchange = new DisplayExchange();
        exchange.setDisplayName("GDAX");
        exchange.setExchangeName("gdax");
        exchanges.add(exchange);

        exchange = new DisplayExchange();
        exchange.setDisplayName("Gemini");
        exchange.setExchangeName("gemini");
        exchanges.add(exchange);
        
        exchange = new DisplayExchange();
        exchange.setDisplayName("Kraken");
        exchange.setExchangeName("kraken");
        exchanges.add(exchange);
        
        return exchanges;
    }
}
