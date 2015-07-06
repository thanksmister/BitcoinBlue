/*
 * Copyright (c) 2015 ThanksMister LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
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
import android.widget.Toast;

import com.thanksmister.btcblue.Injector;
import com.thanksmister.btcblue.R;

import javax.inject.Inject;

import butterknife.ButterKnife;

/** Base activity which sets up a per-activity object graph and performs injection. */
public abstract class BaseActivity extends AppCompatActivity 
{
    @Override 
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override 
    protected void onDestroy() 
    {
        super.onDestroy();

        ButterKnife.reset(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        getApplicationContext().unregisterReceiver(connReceiver);
    }

    @Override
    public void onResume() {

        super.onResume();
        
        getApplicationContext().registerReceiver(connReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    
    // TODO replace with RxAndroid
    private BroadcastReceiver connReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
            NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
               // do nothing
            } else {
                Snackbar.make(findViewById(R.id.coordinatorLayout), getString(R.string.error_no_internet), Snackbar.LENGTH_LONG)
                        .show(); // Do not forget to show!
            }
        }
    };
    
    protected void reportError(Throwable throwable)
    {
        if(throwable != null && throwable.getLocalizedMessage() != null)
            toast(throwable.getLocalizedMessage());
    }

    protected void toast(int messageId)
    {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }


    protected void toast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
