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

package com.thanksmister.btcblue.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class Dates
{
    public static enum DateDistance
    {
        RECENT,
        NOT_SO_MUCH,
        LONG_GONE
    }

    public static DateDistance getTimeInDistance(String dateString)
    {
        long ago = System.currentTimeMillis();
    
        try {
            DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
            Date mDate = sdf.parse(dateString);
            ago = mDate.getTime();
        } catch (ParseException e) {
            Timber.e(e.getMessage());
        }

        long current = System.currentTimeMillis();

        if((current - ago) > 900000 ) { // 5 minutes
            return DateDistance.LONG_GONE;
        } else if ((current - ago) > 300000) { // 15 minutes
            return DateDistance.NOT_SO_MUCH;
        } else {
            return DateDistance.RECENT;
        }
    }

    public static String getLocalDateTime()
    {
        String dateString;
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        Date date = new Date();
        dateString = (dateFormat.format(date.getTime()));
        return dateString ;
    }

    public static String getLocalDateTime(Date date)
    {
        String dateString;
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        dateString = (dateFormat.format(date.getTime()));
        return dateString ;
    }
    
    public static String parseFileTimeStamp(Date date)
    {
        return new SimpleDateFormat("MM_dd_yyyy_HH_mm").format(date);
    }
}