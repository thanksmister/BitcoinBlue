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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.thanksmister.btcblue.Injector;
import com.thanksmister.btcblue.R;
import com.thanksmister.btcblue.utils.ServiceUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.ButterKnife;
import rx.functions.Action0;
import timber.log.Timber;

/**
 * Base activity which sets up a per-activity object graph and performs injection.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private AtomicBoolean hasNetwork = new AtomicBoolean(true);
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(connReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(connReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void reportError(Throwable throwable) {
        Timber.d("reportError");
        if (throwable != null && throwable.getMessage() != null) {
            Timber.e("Data Error: " + throwable.getMessage());
        } else {
            Timber.e("Null Error");
        }
    }

    protected void handleError(Throwable throwable) {
        handleError(throwable, "", null);
    }

    protected void handleError(Throwable throwable, String label, Action0 action) {
        Timber.d("handleError");
        if (ServiceUtils.isNetworkError(throwable)) {
            Timber.e("Data Error: " + "Code 503");
            snackAction(getString(R.string.error_no_internet), label, action);
        } else if (ServiceUtils.isHttp401Error(throwable)) {
            // na-da Ignore this because its a nonce error
        } else if (ServiceUtils.isHttp500Error(throwable)) {
            Timber.e("Data Error: " + "Code 500");
            snackAction(getString(R.string.error_service_error), label, action);
        } else if (ServiceUtils.isHttp404Error(throwable)) {
            Timber.e("Data Error: " + "Code 404");
            snackAction(getString(R.string.error_service_error), label, action);
        } else if (ServiceUtils.isHttp400Error(throwable)) {
            snackAction(getString(R.string.error_service_error), label, action);
        } else if (throwable != null && throwable.getLocalizedMessage() != null) {
            Timber.e("Data Error: " + throwable.getLocalizedMessage());
            snackAction(throwable.getLocalizedMessage(), label, action);
        } else {
            snackAction(R.string.error_unknown_error, label, action);
        }
    }

    protected void snack(final String message) {
        snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    protected void snack(final int message) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG)
                .show();
    }

    protected void snackAction(final int message, final String actionLabel, final Action0 action0) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG)
                .setAction(actionLabel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        action0.call();
                    }
                })
                .show();
    }

    protected void snackAction(final String message, final String actionLabel, final Action0 action0) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG)
                .setAction(actionLabel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        action0.call();
                    }
                })
                .show();
    }

    protected void toast(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    protected void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleNetworkConnect() {
        Timber.d("handleNetworkConnect");
        if(!hasNetworkConnectivity()) {
            snackbar.dismiss();
            hasNetwork.set(true);
        }
    }

    private void handleNetworkDisconnect() {
        Timber.d("handleNetworkDisconnect");
        if(snackbar != null && !hasNetworkConnectivity()) {
            snack(getString(R.string.error_no_internet));
            hasNetwork.set(false);
        }
    }

    private boolean hasNetworkConnectivity() {
        return hasNetwork.get();
    }

    /*
     * Network connectivity receiver to notify client of the network disconnect issues and
     * to clear any network notifications when reconnected. It is easy for network connectivity
     * to run amok that is why we only notify the user once for network disconnect with
     * a boolean flag.
     */
    private BroadcastReceiver connReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
            assert connectivityManager != null;
            NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
                handleNetworkConnect();
            } else {
                handleNetworkDisconnect();
            }
        }
    };
}