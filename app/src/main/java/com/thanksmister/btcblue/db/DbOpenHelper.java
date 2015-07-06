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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "bitcoinblue.db";
    private static final int DATABASE_VERSION = 11;
   
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_TEXT_NOT_NULL = " TEXT NOT NULL";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_REAL = " REAL";
    private static final String COMMA_SEP = ", ";
    
    private static final String CREATE_EXCHANGES =
            "CREATE TABLE IF NOT EXISTS " + ExchangeItem.TABLE + " (" +
                    ExchangeItem.ID + " INTEGER PRIMARY KEY," +
                    ExchangeItem.DISPLAY_NAME + TYPE_TEXT + COMMA_SEP +
                    ExchangeItem.ASK + TYPE_TEXT + COMMA_SEP +
                    ExchangeItem.BID + TYPE_TEXT + COMMA_SEP +
                    ExchangeItem.LAST + TYPE_TEXT + COMMA_SEP +
                    ExchangeItem.BLUE_ASK + TYPE_TEXT + COMMA_SEP +
                    ExchangeItem.BLUE_BID + TYPE_TEXT + COMMA_SEP +
                    ExchangeItem.SOURCE + TYPE_TEXT + COMMA_SEP +
                    ExchangeItem.OFFICIAL_ASK + TYPE_TEXT + COMMA_SEP +
                    ExchangeItem.OFFICIAL_BID + TYPE_TEXT + COMMA_SEP +
                    ExchangeItem.CREATED_AT + TYPE_TEXT +")";

    
    public DbOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null /* factory */, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_EXCHANGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion < newVersion) {
            
            final String ALTER_TBL =
                    "ALTER TABLE " + ExchangeItem.TABLE +
                            " ADD COLUMN " + ExchangeItem.SOURCE + TYPE_TEXT;

            db.execSQL(ALTER_TBL);
            
        }
    }
}
