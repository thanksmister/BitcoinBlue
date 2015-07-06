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

import com.squareup.sqlbrite.SqlBrite;
import com.thanksmister.btcblue.R;
import com.thanksmister.btcblue.data.ExchangeService;
import com.thanksmister.btcblue.data.api.model.Exchange;
import com.thanksmister.btcblue.db.DbManager;
import com.thanksmister.btcblue.ui.spinner.ExchangeAdapter;
import com.thanksmister.btcblue.utils.Calculations;
import com.thanksmister.btcblue.utils.Conversions;
import com.thanksmister.btcblue.utils.Dates;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

import static rx.android.app.AppObservable.bindActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener
{
    @Inject
    ExchangeService exchangeService;

    @Inject
    DbManager dbManager;

    @Inject
    SqlBrite db;
    
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

    @InjectView(android.R.id.progress)
    View progress;

    @InjectView(R.id.content)
    View content;
    
    Handler handler;
    ExchangeAdapter adapter;

    private Observable<List<Exchange>> exchangeObservable;
    private Observable<List<Exchange>> exchangeUpdateObservable;

    Subscription subscription = Subscriptions.empty();
    Subscription updateSubscription = Subscriptions.empty();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.blue));

        handler = new Handler();

        // setup adapter
        adapter = new ExchangeAdapter(this, R.layout.spinner_dropdown);
        
        // database data
        exchangeObservable = bindActivity(this, dbManager.exchangeQuery());

        // update data
        exchangeUpdateObservable = bindActivity(this, exchangeService.getExchangesObservable());
        
        setupSpinner();
        setupFab();
        setFromText();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        appBarLayout.addOnOffsetChangedListener(this);

        onRefreshStart();

        subscribeData();

        updateData();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        appBarLayout.removeOnOffsetChangedListener(this);
        subscription.unsubscribe();
        updateSubscription.unsubscribe();
        handler.removeCallbacks(refreshRunnable);
    }

    @Override
    public void onRefresh()
    {
        updateData();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i)
    {
        if (i == 0) {
            swipeLayout.setEnabled(true);
        } else {
            swipeLayout.setEnabled(false);
        }
    }

    private void onRefreshStart()
    {
        handler.postDelayed(refreshRunnable, 1000);
    }

    private void onRefreshStop()
    {
        handler.removeCallbacks(refreshRunnable);
        
        swipeLayout.setRefreshing(false);
    }
    
    private void onProgress(boolean show)
    {
        progress.setVisibility((show)? View.VISIBLE: View.GONE);
        content.setVisibility((show) ? View.GONE : View.VISIBLE);
    }
    
    private Runnable refreshRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            swipeLayout.setRefreshing(true);
        }
    };
    
    private void setupFab()
    {
        //fab.setBackgroundTintList(getResources().getColorStateList(R.drawable.fab_background));
        fab.setOnClickListener(this);
    }

    private void setupSpinner()
    {
        exchangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Exchange exchange = (Exchange) adapterView.getAdapter().getItem(i);
                exchangeService.setSelectedExchange(exchange.getDisplay_name());
                setExchange(exchange);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
    }
    
    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.fab) {
            Intent intent = CalculatorActivity.createStartIntent(MainActivity.this);
            startActivity(intent);
        }
    }

    public void subscribeData()
    {
        subscription = exchangeObservable.subscribe(new Action1<List<Exchange>>()
        {
            @Override
            public void call(List<Exchange> exchanges)
            {
                if(!exchanges.isEmpty()) {
                    onProgress(false);
                    updateSelectedExchange(exchanges);
                } else {
                    onProgress(true);
                }
            }
        }, new Action1<Throwable>()
        {
            @Override
            public void call(Throwable throwable)
            {
                onProgress(false);
                reportError(throwable);
            }
        });
    }

    public void updateData()
    {
        updateSubscription = exchangeUpdateObservable.subscribe(new Action1<List<Exchange>>()
        {
            @Override
            public void call(List<Exchange> exchanges)
            {
                onRefreshStop();

                dbManager.updateExchanges(exchanges);
               
            }
        }, new Action1<Throwable>()
        {
            @Override
            public void call(Throwable throwable)
            {
                onRefreshStop();
                
                reportError(throwable);
            }
        });
    }
    
    private void updateSelectedExchange(final List<Exchange> exchanges)
    {
        String name = exchangeService.getSelectedExchangeName();
        
        int index = 0;
        for (Exchange ex:exchanges) {
            if(ex.getDisplay_name().equals(name)) {
                break;
            }
            index ++;
        }
        
        adapter.setData(exchanges);
        setExchangeSpinner(adapter, index);

        Exchange selectedExchange = exchanges.get(index);
        setExchange(selectedExchange);
        
    }

    public void setExchangeSpinner(ExchangeAdapter adapter, int currentSelectedPosition)
    {
        exchangeSpinner.setAdapter(adapter);
        exchangeSpinner.setSelection(currentSelectedPosition);
    }
    
    private void setExchange(Exchange exchange)
    {
        if(exchange == null) return;

        setMarketData(exchange, MarketViewType.ARS_USDB);
        setMarketData(exchange, MarketViewType.ARS_BTC);
        setMarketData(exchange, MarketViewType.USD_BTC);

        setDate(exchange);
    }
    
    private void setDate(Exchange exchange)
    {
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
    
    private void setFromText()
    {
        dataFrom.setText(Html.fromHtml(getString(R.string.data_provided_by)));
        dataFrom.setMovementMethod(LinkMovementMethod.getInstance());
    }
    
    private enum MarketViewType
    {
        USD_BTC,
        ARS_BTC,
        ARS_USDB,
        ARS_USD
    }

    public void setMarketData(Exchange exchange, MarketViewType type)
    {
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
}
