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

package com.thanksmister.btcblue.data.api;

import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;

@Module(
        complete = false,
        library = true
)
public final class ApiModule 
{
    private static final String BITSTAMP_API_ENDPOINT = "https://www.bitstamp.net";
    private static final String BLUELYTICS_API_ENDPOINT = "http://api.bluelytics.com.ar";
    private static final String COINBASE_API_ENDPOINT = "https://coinbase.com";
    private static final String BITCOIN_AVERAGE_API_ENDPOINT = "https://api.bitcoinaverage.com";
   
    @Provides 
    @Singleton
    Client provideClient(OkHttpClient client) 
    {
        client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout
        client.setConnectionPool(new ConnectionPool(0, 5 * 60 * 1000));
        return new OkClient(client);
    }
    
    @Provides
    @Singleton
    BitcoinAverage provideBitcoinAverage(Client client)
    {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setEndpoint(BITCOIN_AVERAGE_API_ENDPOINT)
                .build();
        return restAdapter.create(BitcoinAverage.class);
    }

    @Provides
    @Singleton
    CoinbaseMarket provideCoinbaseMarket(Client client)
    {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setEndpoint(COINBASE_API_ENDPOINT)
                .build();
        return restAdapter.create(CoinbaseMarket.class);
    }

    @Provides
    @Singleton
    BitstampExchange provideBitstampExchange(Client client)
    {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setEndpoint(BITSTAMP_API_ENDPOINT)
                .build();
        return restAdapter.create(BitstampExchange.class);
    }

    @Provides
    @Singleton
    Bluelytics provideBluelytics(Client client)
    {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setEndpoint(BLUELYTICS_API_ENDPOINT)
                .build();
        return restAdapter.create(Bluelytics.class);
    }
}