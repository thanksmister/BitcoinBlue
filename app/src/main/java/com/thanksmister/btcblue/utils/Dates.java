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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
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
    
    public static String parseOrderLocalDateString(String dateTime)
    {
        String dateString;

        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy\nhh:mma");
        try {
            dateString = (dateFormat.format(ISO8601.toCalendar(dateTime).getTime()));
        } catch (ParseException e) {
            Date date = new Date();
            dateString = (dateFormat.format(date.getTime()));
        }
        return dateString ;
    }

    public static String getLocalDateMilitaryTime()
    {
        String dateTime = createISODate();
        String dateString;
        // Date
        //yyyy-MM-dd kk:mm:ss
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        try {
            dateString = (dateFormat.format(ISO8601.toCalendar(dateTime).getTime()));
        } catch (ParseException e) {
            Date date = new Date();
            dateString = (dateFormat.format(date.getTime()));
        }

        return dateString ;
    }

    public static String getLocalDateMilitaryTimeShort()
    {
        String dateTime = createISODate();
        String dateString;
        // Date
        //yyyy-MM-dd kk:mm:ss
        SimpleDateFormat dateFormat = new SimpleDateFormat("kk:mm");
        try {
            dateString = (dateFormat.format(ISO8601.toCalendar(dateTime).getTime()));
        } catch (ParseException e) {
            Date date = new Date();
            dateString = (dateFormat.format(date.getTime()));
        }

        return dateString ;
    }

    public static String parseFileTimeStamp()
    {
        String dateTime = createISODate();
        String dateString;

        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_dd_yyyy_kk_mm");
        try {
            dateString = (dateFormat.format(ISO8601.toCalendar(dateTime).getTime()));
        } catch (ParseException e) {
            dateString = null;
        }
        return dateString ;
    }

    public static String createISODate()
    {
        return  ISO8601.now();
    }

}
