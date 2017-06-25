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

package com.thanksmister.btcblue.db;

import android.database.Cursor;

import com.squareup.sqlbrite.SqlBrite;
import com.thanksmister.btcblue.data.api.model.Exchange;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class DbManager {
    private SqlBrite db;

    @Inject
    public DbManager(SqlBrite db) {
        this.db = db;
    }

    /**
     * Resets Db manager and clear all preferences
     */
    public void clearDbManager() {
        db.delete(ExchangeItem.TABLE, null);
    }

    public Observable<List<Exchange>> exchangeQuery() {
        return db.createQuery(ExchangeItem.TABLE, ExchangeItem.QUERY)
                .map(ExchangeItem.MAP);
    }

    public Observable<Exchange> exchangeQuery(String source) {
        return db.createQuery(ExchangeItem.TABLE, ExchangeItem.QUERY_ITEM_SOURCE, source)
                .map(ExchangeItem.MAP_SINGLE);
    }
    
    public void updateExchange(final Exchange exchange) {
        db.beginTransaction();
        Cursor cursor = db.query(ExchangeItem.QUERY_ITEM_SOURCE, exchange.getSource());
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                long id = Db.getLong(cursor, ExchangeItem.ID);
                db.update(ExchangeItem.TABLE, ExchangeItem.createBuilder(exchange).build(), ExchangeItem.ID + " = ?", String.valueOf(id));
            } else {
                db.insert(ExchangeItem.TABLE, ExchangeItem.createBuilder(exchange).build());
            }
            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            cursor.close();
        }
    }
}
