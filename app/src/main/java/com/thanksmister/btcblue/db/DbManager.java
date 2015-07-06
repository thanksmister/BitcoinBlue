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

package com.thanksmister.btcblue.db;

import android.database.Cursor;

import com.squareup.sqlbrite.SqlBrite;
import com.thanksmister.btcblue.data.api.model.Exchange;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class DbManager 
{
    private SqlBrite db;
    
    @Inject
    public DbManager(SqlBrite db)
    {
        this.db = db;
    }

    /**
     * Resets Db manager and clear all preferences
     */
    public void clearDbManager()
    {
        db.delete(ExchangeItem.TABLE, null);
    }

    public Observable<List<Exchange>> exchangeQuery()
    {
        return db.createQuery(ExchangeItem.TABLE, ExchangeItem.QUERY)
                .map(ExchangeItem.MAP);
    }

    public Observable<Exchange> exchangeQuery(String displayName)
    {
        return db.createQuery(ExchangeItem.TABLE, ExchangeItem.QUERY_ITEM_DISPLAY_NAME, displayName)
                .map(ExchangeItem.MAP_SINGLE);
    }
    
    public void updateExchanges(List<Exchange> exchanges)
    {
        HashMap<String, Exchange> entryMap = new HashMap<String, Exchange>();

        for (Exchange item : exchanges) {
            entryMap.put(item.getDisplay_name(), item);
        }

        db.beginTransaction();

        // Get list of all items
        Cursor cursor = db.query(ExchangeItem.QUERY);

        try {
            
            while (cursor.moveToNext()) {

                long id = Db.getLong(cursor, ExchangeItem.ID);
                String name = Db.getString(cursor, ExchangeItem.DISPLAY_NAME);

                Exchange match = entryMap.get(name);

                if (match != null) {
                    
                    // Entry exists. Remove from entry map to prevent insert later. Do not update
                    entryMap.remove(name);

                    db.update(ExchangeItem.TABLE, ExchangeItem.createBuilder(match).build(), ExchangeItem.ID + " = ?", String.valueOf(id));
                    
                } else {
                    // Entry doesn't exist. Remove it from the database.
                    db.delete(ExchangeItem.TABLE, ExchangeItem.ID + " = ?", String.valueOf(id));
                }
            }

            // Add new items
            for (Exchange item : entryMap.values()) {
       
                db.insert(ExchangeItem.TABLE, ExchangeItem.createBuilder(item).build());
                
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            cursor.close();
        }
    }

    public void updateExchange(final Exchange exchange)
    {
        db.beginTransaction();
        Cursor cursor = db.query(ExchangeItem.QUERY);
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
