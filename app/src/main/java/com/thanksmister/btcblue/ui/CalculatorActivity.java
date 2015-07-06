package com.thanksmister.btcblue.ui;

import android.support.v7.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.widget.ContentFrameLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thanksmister.btcblue.R;
import com.thanksmister.btcblue.data.ExchangeService;
import com.thanksmister.btcblue.data.WriterService;
import com.thanksmister.btcblue.data.api.model.Exchange;
import com.thanksmister.btcblue.db.DbManager;
import com.thanksmister.btcblue.utils.Calculations;
import com.thanksmister.btcblue.utils.Conversions;
import com.thanksmister.btcblue.utils.Doubles;
import com.thanksmister.btcblue.utils.Strings;

import java.io.File;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

import static rx.android.app.AppObservable.bindActivity;

public class CalculatorActivity extends BaseActivity implements View.OnFocusChangeListener, TextWatcher
{
    private final static double DEFAULT_BTC_VALUE = 1;
    
    private final static int EDIT_ARS = 1;
    private final static int EDIT_USD = 2;
    private final static int EDIT_BTC = 3;
    private final static int EDIT_SALE_ARS = 4;
    private final static int EDIT_SALE_USD = 5;
    private final static int EDIT_FEE = 6;
    private final static int EDIT_COMMISSION = 7;

    @Inject
    DbManager dbManager;

    @Inject
    ExchangeService exchangeService;
    
    @InjectView(R.id.totalARS) TextView totalARS;
    @InjectView(R.id.totalUSD) TextView totalUSD;
    @InjectView(R.id.totalBTC) TextView totalBTC;
    @InjectView(R.id.editARS) EditText editARS;
    @InjectView(R.id.editBTC) EditText editBTC;
    @InjectView(R.id.editUSD) EditText editUSD;
    @InjectView(R.id.editFee) EditText editFee;
    @InjectView(R.id.editSaleUSD) EditText editSaleUSD;
    @InjectView(R.id.editSaleARS) EditText editSaleARS;
    @InjectView(R.id.editCommission) EditText editCommission;
    @InjectView(R.id.copyARS) ImageButton copyARS;
    @InjectView(R.id.copyUSD) ImageButton copyUSD;
    @InjectView(R.id.copyBTC) ImageButton copyBTC;
    @InjectView(R.id.copyTotalARS) ImageButton copyTotalARS;
    @InjectView(R.id.copyTotalUSD) ImageButton copyTotalUSD;
    @InjectView(R.id.copyTotalBTC) ImageButton copyTotalBTC;
    @InjectView(R.id.copySaleARS) ImageButton copySaleARScopySaleARS;
    @InjectView(R.id.copySaleUSD) ImageButton copySaleUSD;
    @InjectView(R.id.copyFee) ImageButton copyFee;
    @InjectView(R.id.copyCommission) ImageButton copyCommission;

    @OnClick(R.id.clearFees)
    public void clearFeesClicked()
    {
        resetFeesAndCommission();
    }

    @OnClick(R.id.clearExchange)
    public void clearExchangeClicked()
    {
        reset();
    }

    @OnClick(R.id.copyARS)
    public void copyARSClicked()
    {
        CharSequence copy = editARS.getText();
        if(!Strings.isBlank(copy)) {
            setTextOnClipboard("ARS", copy);
        }
    }

    @OnClick(R.id.copyUSD)
    public void copyUSDClicked()
    {
        CharSequence copy = editUSD.getText();
        if(!Strings.isBlank(copy)) {
            setTextOnClipboard("USD", copy);
        }
    }

    @OnClick(R.id.copyBTC)
    public void copyBTCClicked()
    {
        CharSequence copy = editBTC.getText();
        if(!Strings.isBlank(copy)) {
            setTextOnClipboard("BTC", copy);
        }
    }

    @OnClick(R.id.copySaleARS)
    public void copySaleArsClicked()
    {
        CharSequence copy = editBTC.getText();
        if(!Strings.isBlank(copy)) {
            setTextOnClipboard("Sale ARS", copy);
        }
    }

    @OnClick(R.id.copySaleUSD)
    public void copySaleUsdClicked()
    {
        CharSequence copy = editBTC.getText();
        if(!Strings.isBlank(copy)) {
            setTextOnClipboard("Sale USD", copy);
        }
    }

    @OnClick(R.id.copyFee)
    public void copyFeeClicked()
    {
        CharSequence copy = editFee.getText();
        if(!Strings.isBlank(copy)) {
            setTextOnClipboard("Fee", copy);
        }
    }

    @OnClick(R.id.copyCommission)
    public void copyCommissionClicked()
    {
        CharSequence copy = editCommission.getText();
        if(!Strings.isBlank(copy)) {
            setTextOnClipboard("Commission", copy);
        }
    }

    @OnClick(R.id.copyTotalARS)
    public void copyTotalArsClicked()
    {
        CharSequence copy = totalARS.getText();
        if(!Strings.isBlank(copy)) {
            setTextOnClipboard("Total ARS", copy);
        }
    }

    @OnClick(R.id.copyTotalUSD)
    public void copyTotalUsdClicked()
    {
        CharSequence copy = totalUSD.getText();
        if(!Strings.isBlank(copy)) {
            setTextOnClipboard("Total USD", copy);
        }
    }

    @OnClick(R.id.copyTotalBTC)
    public void copyTotalBtcClicked()
    {
        CharSequence copy = totalBTC.getText();
        if(!Strings.isBlank(copy)) {
            setTextOnClipboard("Total BTC", copy);
        }
    }

    @InjectView(android.R.id.progress)
    View progress;

    @InjectView(R.id.content)
    View content;

    private Observable<Exchange> exchangeObservable;
    Subscription subscription = Subscriptions.empty();
    
    double rateUSD = 0;
    double rateARS = 0;
    double bitcoinValue = DEFAULT_BTC_VALUE;
    double arsValue = 0;
    double usdValue = 0;
    
    int whoHasFocus = 0;
    Exchange exchange;
    AlertDialog dialog;
    Toolbar toolbar;
    
    public static Intent createStartIntent(Context context)
    {
        return new Intent(context, CalculatorActivity.class);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_calculator);

        ButterKnife.inject(this);
        
        // database data
        String exchangeName = exchangeService.getSelectedExchangeName();
        exchangeObservable = bindActivity(this, dbManager.exchangeQuery(exchangeName));

        setupToolbar();
        setupEditText();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        
        subscribeData();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        subscription.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calculator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if(id == R.id.action_print) {
            onReceipt();
        }

        return super.onOptionsItemSelected(item);
    }

    private void onProgress(boolean show)
    {
        progress.setVisibility((show)? View.VISIBLE: View.GONE);
        content.setVisibility((show)? View.GONE: View.VISIBLE);
    }

    private void setupEditText()
    {
        editBTC.setOnFocusChangeListener(this);
        editBTC.addTextChangedListener(this);
        editBTC.setFilters(new InputFilter[]{new Calculations.DecimalPlacesInputFilter(8)});

        editUSD.setOnFocusChangeListener(this);
        editUSD.addTextChangedListener(this);
        editUSD.setFilters(new InputFilter[]{new Calculations.DecimalPlacesInputFilter(2)});
        
        editARS.setOnFocusChangeListener(this);
        editARS.addTextChangedListener(this);
        editARS.setFilters(new InputFilter[]{new Calculations.DecimalPlacesInputFilter(2)});
        
        editSaleARS.setOnFocusChangeListener(this);
        editSaleARS.addTextChangedListener(this);
        editSaleARS.setFilters(new InputFilter[]{new Calculations.DecimalPlacesInputFilter(2)});
        
        editSaleUSD.setOnFocusChangeListener(this);
        editSaleUSD.addTextChangedListener(this);
        editSaleUSD.setFilters(new InputFilter[]{new Calculations.DecimalPlacesInputFilter(2)});
        
        editFee.setOnFocusChangeListener(this);
        editFee.addTextChangedListener(this);
        editFee.setFilters(new InputFilter[]{new Calculations.DecimalPlacesInputFilter(8)});
        
        editCommission.setOnFocusChangeListener(this);
        editCommission.addTextChangedListener(this);
        editCommission.setFilters(new InputFilter[]{new Calculations.DecimalPlacesInputFilter(2)});
    }
  
    private void setupToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.title_calculator);
        //ab.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void subscribeData()
    {
        // we alreayd set the value don't reset on rotate or resume
        if(editBTC != null && !Strings.isBlank(editBTC.getText().toString())) {
            onProgress(false);
            return;
        }
        
        subscription = exchangeObservable.subscribe(new Action1<Exchange>()
        {
            @Override
            public void call(Exchange exchange)
            {
                setExchange(exchange);
                onProgress(false);
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

    private void setExchange(Exchange exchange)
    {
        this.exchange = exchange;
        
        rateUSD = exchange.getUSDValue();
        rateARS = exchange.getARSValue();

        reset();
    }
    
    private void reset() 
    {
        bitcoinValue = DEFAULT_BTC_VALUE;
        
        arsValue = Calculations.calculateARSValue(rateARS, bitcoinValue);
        usdValue = Calculations.calculateUSDValue(rateUSD, bitcoinValue);

        editBTC.setText(Conversions.formatBitcoinAmount(bitcoinValue));
        editARS.setText(Conversions.formatCurrencyAmount(rateARS));
        editUSD.setText(Conversions.formatCurrencyAmount(rateUSD));

        calculateTotal();
    }

    private void resetFeesAndCommission()
    {
        editSaleARS.setText("");
        editSaleUSD.setText("");
        editFee.setText("");
        editCommission.setText("");
        
        calculateTotal();
    }

    private void setTextOnClipboard(String title, CharSequence copy)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(title, copy);
        clipboard.setPrimaryClip(clip);
        toast(R.string.text_saved_to_clipboard);
    }
    
    private void calculateTotal()
    {
        double saleValueARS = Doubles.convertToDouble(editSaleARS.getText().toString());
        double saleValueUSD = Doubles.convertToDouble(editSaleUSD.getText().toString());
        double feeValue = Doubles.convertToDouble(editFee.getText().toString());
        double commissionValue = Doubles.convertToDouble(editCommission.getText().toString());

        if((saleValueUSD > 0 && saleValueUSD != usdValue) || (saleValueARS > 0 && saleValueARS != arsValue) || commissionValue > 0 || feeValue > 0) {
            
            Calculations.CalculatedValue totalValues = Calculations.calculateTotalSales(saleValueARS, commissionValue, feeValue, bitcoinValue, arsValue, usdValue);
            int negativeOrPositive = (saleValueUSD < usdValue)? R.string.sales_negative:R.string.sales_positive;

            totalBTC.setText(Html.fromHtml(getString(negativeOrPositive, Conversions.formatBitcoinAmount(totalValues.btcSale), Conversions.formatBitcoinAmount(totalValues.diffBTC))));
            totalARS.setText(Html.fromHtml(getString(negativeOrPositive, Conversions.formatCurrencyAmount(totalValues.arsSale), Conversions.formatCurrencyAmount(totalValues.diffARS))));
            totalUSD.setText(Html.fromHtml(getString(negativeOrPositive, Conversions.formatCurrencyAmount(totalValues.usdSale), Conversions.formatCurrencyAmount(totalValues.diffUSD))));
            
        } else {
            
            totalBTC.setText(Conversions.formatBitcoinAmount(bitcoinValue));
            totalARS.setText(Conversions.formatCurrencyAmount(arsValue));
            totalUSD.setText(Conversions.formatCurrencyAmount(usdValue));
        }
    }

    @Override
    public void onFocusChange(View view, boolean b)
    {
        switch (view.getId()) {
            case R.id.editARS:
                whoHasFocus = EDIT_ARS;
                break;
            case R.id.editUSD:
                whoHasFocus = EDIT_USD;
                break;
            case R.id.editBTC:
                whoHasFocus = EDIT_BTC;
                break;
            case R.id.editSaleARS:
                whoHasFocus = EDIT_SALE_ARS;
                break;
            case R.id.editSaleUSD:
                whoHasFocus = EDIT_SALE_USD;
                break;
            case R.id.editFee:
                whoHasFocus = EDIT_FEE;
                break;
            case R.id.editCommission:
                whoHasFocus = EDIT_COMMISSION;
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {
    }

    @Override
    public void afterTextChanged(Editable editable)
    {
        String stringValue = editable.toString();
       
        switch (whoHasFocus) {
            
            case EDIT_ARS:
                
                if (editARS.getText().hashCode() == editable.hashCode()) {
                    
                    arsValue = Doubles.convertToDouble(stringValue);
                    
                    bitcoinValue = Calculations.calculateBTC(arsValue, rateARS);
                    
                    usdValue = Calculations.calculateUSDValue(rateUSD, bitcoinValue);

                    if(Strings.isBlank(stringValue)) {
                        editUSD.setText("");
                        editBTC.setText("");
                    } else {
                        editUSD.setText(Conversions.formatCurrencyAmount(usdValue));
                        editBTC.setText(Conversions.formatBitcoinAmount(bitcoinValue));
                    }
                }
                
                break;
            
            case EDIT_USD:
                
                if (editUSD.getText().hashCode() == editable.hashCode()) {

                    usdValue = Doubles.convertToDouble(stringValue);
                    
                    bitcoinValue = Calculations.calculateBTC(usdValue, rateUSD);
                    
                    arsValue = Calculations.calculateARSValue(rateARS, bitcoinValue);

                    if(Strings.isBlank(stringValue)) {
                        editARS.setText("");
                        editBTC.setText("");
                    } else {
                        editARS.setText(Conversions.formatCurrencyAmount(arsValue));
                        editBTC.setText(Conversions.formatBitcoinAmount(bitcoinValue));
                    }
                }
                break;
            case EDIT_BTC:
                
                if (editBTC.getText().hashCode() == editable.hashCode()) {

                    bitcoinValue = Doubles.convertToDouble(stringValue);
                    
                    usdValue = Calculations.calculateUSDValue(rateUSD, bitcoinValue);
                    
                    arsValue = Calculations.calculateARSValue(rateARS, bitcoinValue);

                    if(Strings.isBlank(stringValue)) {
                        editARS.setText("");
                        editUSD.setText("");
                    } else {
                        editARS.setText(Conversions.formatCurrencyAmount(arsValue));
                        editUSD.setText(Conversions.formatCurrencyAmount(usdValue));
                    }
                }
                break;
            case EDIT_SALE_ARS:
                
                if (editSaleARS.getText().hashCode() == editable.hashCode()) {

                    double ars = Doubles.convertToDouble(stringValue);
                    
                    double btc = Calculations.calculateBTC(ars, rateARS);

                    double usd = Calculations.calculateUSDValue(rateUSD, btc);

                    if(Strings.isBlank(stringValue)) {
                        editSaleUSD.setText("");
                    } else {
                        editSaleUSD.setText(Conversions.formatCurrencyAmount(usd));
                    }
                }
                break;

            case EDIT_SALE_USD:
                
                if (editSaleUSD.getText().hashCode() == editable.hashCode()) {

                    double usd = Doubles.convertToDouble(stringValue);
                    
                    double btc = Calculations.calculateBTC(usd, rateUSD);

                    double ars = Calculations.calculateARSValue(rateARS, btc);

                    if(Strings.isBlank(stringValue)) {
                        editSaleARS.setText("");
                    } else {
                        editSaleARS.setText(Conversions.formatCurrencyAmount(ars));
                    }
                }
                break;
            
            case EDIT_FEE:
            case EDIT_COMMISSION:
                break;
        }
        
        calculateTotal();
    }

    public void createAlert()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialogview, null);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(dialogView);

        final EditText input = (EditText) dialogView.findViewById(android.R.id.text1);
        input.setImeActionLabel("Enter", KeyEvent.KEYCODE_ENTER);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String value = input.getText().toString();
                    generateReceipt(value);
                    dialog.dismiss();
                }
                
                return false;
            }
        });
        
        final Button button1 = (Button) dialogView.findViewById(android.R.id.button1);
        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String value = input.getText().toString();
                generateReceipt(value);
                dialog.dismiss();
            }
        });

        final Button button2 = (Button) dialogView.findViewById(android.R.id.button2);
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });
        
        dialog = alertDialog.show();
    }

    private void onReceipt()
    {
        createAlert();
    }

    private void shareReceipt(File file)
    {
        Timber.d("Share Receipt");
        
        Uri uri = Uri.fromFile(file);
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, file.getName());
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("text/csv");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(sendIntent);
    }
    
    public void generateReceipt(String title)
    {
        Timber.d("Generate Receipt");
        
        String saleValueARS = editSaleARS.getText().toString();
        String saleValueUSD = editSaleUSD.getText().toString();
        String saleValueBTC = editSaleUSD.getText().toString();
        String feeValue = editFee.getText().toString();
        String commissionValue = editCommission.getText().toString();
        
        final WriterService writerService = new WriterService();
        Observable<File> observable = writerService.writeReceiptFileObservable(title, exchange, String.valueOf(bitcoinValue), String.valueOf(arsValue),
                String.valueOf(usdValue), saleValueARS, saleValueUSD, saleValueBTC, commissionValue, feeValue);
        observable.subscribe(new Action1<File>()
        {
            @Override
            public void call(File file)
            {
                shareReceipt(file);
            }
        }, new Action1<Throwable>()
        {
            @Override
            public void call(Throwable throwable)
            {
                reportError(throwable);
            }
        });
    }
}
