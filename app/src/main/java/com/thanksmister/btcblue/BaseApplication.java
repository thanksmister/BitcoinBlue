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

package com.thanksmister.btcblue;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

import timber.log.Timber;

public class BaseApplication extends Application
{
    @Override
    public void onCreate() 
    {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Force set the default locale to US for conversions
        Locale.setDefault(Locale.US);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.locale = Locale.US;
        res.updateConfiguration(config, res.getDisplayMetrics());
        
        Injector.init(this);
    }
}
