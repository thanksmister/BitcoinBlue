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

/*
 * Modified from example here:
 * http://stackoverflow.com/questions/11341931/how-to-create-a-csv-on-android
 */

package com.thanksmister.btcblue.data;

import android.content.Context;
import android.os.Environment;

import com.thanksmister.btcblue.R;
import com.thanksmister.btcblue.data.api.model.Exchange;
import com.thanksmister.btcblue.utils.Conversions;
import com.thanksmister.btcblue.utils.Dates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

@Singleton
public class WriterService {
    
    @Inject
    public WriterService() {
    }

    public Observable<File> writeReceiptFileObservable(final Context context, final String title, final Exchange exchange,
                                                       final String btcValue, final String arsValue, final String usdValue,
                                                       final String rateUsed) {

        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                Date date = new Date();
                String dateString = Dates.getLocalDateTime(date);
                String timestamp = Dates.parseFileTimeStamp(date);
                final String filename = (title.isEmpty()) ? timestamp : (title + "_" + timestamp) + ".csv";
                final File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename);
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

                    bw.append(context.getString(R.string.column_name));
                    bw.append(',');

                    bw.append(context.getString(R.string.column_date));
                    bw.append(',');

                    bw.append(context.getString(R.string.column_exchange));
                    bw.append(',');

                    bw.append(context.getString(R.string.column_ars_usdb));
                    bw.append(',');

                    bw.append(context.getString(R.string.column_ars_usd));
                    bw.append(',');

                    bw.append(context.getString(R.string.column_ars_amount));
                    bw.append(',');

                    bw.append(context.getString(R.string.column_usd_amount));
                    bw.append(',');

                    bw.append(context.getString(R.string.column_btc_amount));
                    bw.append(',');

                    bw.append(context.getString(R.string.column_rate_used));

                    bw.newLine();

                    bw.append(title);
                    bw.append(',');

                    bw.append(dateString);
                    bw.append(',');

                    bw.append(exchange.getDisplay_name());
                    bw.append(',');

                    bw.append(exchange.getBlueFormatted());
                    bw.append(',');

                    bw.append(exchange.getOfficialFormatted());
                    bw.append(',');

                    bw.append(Conversions.formatCurrencyAmount(arsValue));
                    bw.append(',');

                    bw.append(usdValue);
                    bw.append(',');

                    bw.append(btcValue);
                    bw.append(',');

                    bw.append(rateUsed);

                    bw.newLine();
                    bw.close();

                    subscriber.onNext(outFile);
                    subscriber.onCompleted();

                } catch (Exception e) {
                    Timber.e(e.getMessage());
                    String err = (e.getMessage() == null) ? context.getString(R.string.error_failed_write_file) : e.getMessage();
                    subscriber.onError(new Throwable(err));
                }
            }
        });
    }
}