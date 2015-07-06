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

import android.content.ContentValues;
import android.database.Cursor;

import com.thanksmister.btcblue.data.api.model.Exchange;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

import static com.squareup.sqlbrite.SqlBrite.Query;

/**
 * https://github.com/square/sqlbrite/
 */
public abstract class ExchangeItem
{
    public static final String TABLE = "exchange_item";

    public static final String ID = "_id";
    
    public static final String DISPLAY_NAME = "display_name";
    public static final String ASK = "ask";
    public static final String BID = "bid";
    public static final String LAST = "last";
    public static final String SOURCE = "source";
    public static final String BLUE_BID = "blue_bid";
    public static final String BLUE_ASK = "blue_ask";
    public static final String OFFICIAL_BID = "official_bid";
    public static final String OFFICIAL_ASK = "official_ask";
    public static final String CREATED_AT = "created_at";

    public static final String QUERY = "SELECT * FROM " 
            + ExchangeItem.TABLE
            + " ORDER BY "
            + ExchangeItem.DISPLAY_NAME
            + " ASC";

    public static final String QUERY_ITEM_DISPLAY_NAME = "SELECT * FROM "
            + ExchangeItem.TABLE
            + " WHERE "
            + ExchangeItem.DISPLAY_NAME
            + " = ? ORDER BY "
            + ExchangeItem.CREATED_AT
            + " ASC";
    

    public abstract long id();
    public abstract String display_name();
    public abstract String ask();
    public abstract String bid();
    public abstract String last();
    public abstract String source();
    public abstract String blue_bid();
    public abstract String blue_ask();
    public abstract String official_bid();
    public abstract String official_ask();
    public abstract String created_at();
    
    public static final Func1<Query, List<Exchange>> MAP = new Func1<Query, List<Exchange>>() {
        @Override
        public List<Exchange> call(Query query) {
            Cursor cursor = query.run();
            try {
                List<Exchange> values = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    long id = Db.getLong(cursor, ID);
                    String display_name = Db.getString(cursor, DISPLAY_NAME);
                    String ask = Db.getString(cursor, ASK);
                    String bid = Db.getString(cursor, BID);
                    String last = Db.getString(cursor, LAST);
                    String source = Db.getString(cursor, SOURCE);
                    String blue_bid = Db.getString(cursor, BLUE_BID);
                    String blue_ask = Db.getString(cursor, BLUE_ASK);
                    String official_bid = Db.getString(cursor, OFFICIAL_BID);
                    String official_ask = Db.getString(cursor, OFFICIAL_ASK);
                    String created_at = Db.getString(cursor, CREATED_AT);
                    
                    values.add(new Exchange(id, display_name, ask, bid, last, source, blue_bid, blue_ask, official_bid, official_ask, created_at));
                }
                return values;
            } finally {
                cursor.close();
            }
        }
    };

    public static final Func1<Query, Exchange> MAP_SINGLE = new Func1<Query, Exchange>() {
        @Override
        public Exchange call(Query query) {
            Cursor cursor = query.run();
            try {
                if(cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    long id = Db.getLong(cursor, ID);
                    String display_name = Db.getString(cursor, DISPLAY_NAME);
                    String ask = Db.getString(cursor, ASK);
                    String bid = Db.getString(cursor, BID);
                    String last = Db.getString(cursor, LAST);
                    String source = Db.getString(cursor, SOURCE);
                    String blue_bid = Db.getString(cursor, BLUE_BID);
                    String blue_ask = Db.getString(cursor, BLUE_ASK);
                    String official_bid = Db.getString(cursor, OFFICIAL_BID);
                    String official_ask = Db.getString(cursor, OFFICIAL_ASK);
                    String created_at = Db.getString(cursor, CREATED_AT);
                    
                    return new Exchange(id, display_name, ask, bid, last, source, blue_bid, blue_ask, official_bid, official_ask, created_at);
                }

                return null;
             
            } finally {
                cursor.close();
            }
        }
    };

    public static Builder createBuilder(Exchange item)
    {
        return new Builder()
                .display_name(item.getDisplay_name())
                .bid(item.getBid())
                .ask(item.getAsk())
                .last(item.getLast())
                .source(item.getSource())
                .blue_bid(item.getBlue_bid())
                .blue_ask(item.getBlue_ask())
                .official_bid(item.getOfficial_bid())
                .official_ask(item.getOfficial_ask())
                .created_at(item.getCreated_at());
    }
    
    public static final class Builder {
        
        private final ContentValues values = new ContentValues();

        public Builder id(long id) {
            values.put(ID, id);
            return this;
        }

        public Builder display_name(String value) {
            values.put(DISPLAY_NAME, value);
            return this;
        }

        public Builder ask(String value) {
            values.put(ASK, value);
            return this;
        }

        public Builder bid(String value) {
            values.put(BID, value);
            return this;
        }

        public Builder last(String value) {
            values.put(LAST, value);
            return this;
        }

        public Builder source(String value) {
            values.put(SOURCE, value);
            return this;
        }

        public Builder blue_bid(String value) {
            values.put(BLUE_BID, value);
            return this;
        }

        public Builder blue_ask(String value) {
            values.put(BLUE_ASK, value);
            return this;
        }

        public Builder official_bid(String value) {
            values.put(OFFICIAL_BID, value);
            return this;
        }

        public Builder official_ask(String value) {
            values.put(OFFICIAL_ASK, value);
            return this;
        }
        
        public Builder created_at(String value) {
            values.put(CREATED_AT, value);
            return this;
        }
        
        public ContentValues build() {
            return values; 
        }
    }
}
