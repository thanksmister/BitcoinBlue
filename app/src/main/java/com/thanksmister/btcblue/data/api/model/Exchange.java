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
package com.thanksmister.btcblue.data.api.model;

import com.thanksmister.btcblue.utils.Calculations;

public class Exchange
{
    private long _id;
    private String display_name;
    private String ask;
    private String bid;
    private String last;
    private String source;
    private String blue_bid;
    private String blue_ask;
    private String official_bid;
    private String official_ask;
    private String created_at;

    public Exchange(long id, String name, String ask, String bid, String last, String source, String blue_bid, String blue_ask, String official_bid, String official_ask, String date) {
        this._id = id;
        this.display_name = name;
        this.ask = ask;
        this.bid = bid;
        this.last = last;
        this.source = source;
        this.blue_bid = blue_bid;
        this.blue_ask = blue_ask;
        this.official_bid = official_bid;
        this.official_ask = official_ask;
        this.created_at = date;
    }

    public Exchange(String name, String ask, String bid, String last, String source, String date) {
        this.display_name = name;
        this.ask = ask;
        this.bid = bid;
        this.last = last;
        this.source = source;
        this.created_at = date;
    }
    
    public long get_id()
    {
        return _id;
    }

    public String getDisplay_name()
    {
        return display_name;
    }

    public String getAsk()
    {
        return ask;
    }

    public String getBid()
    {
        return bid;
    }

    public String getLast()
    {
        return last;
    }

    public String getSource()
    {
        return source;
    }

    public String getBlue_bid()
    {
        return blue_bid;
    }

    public String getBlue_ask()
    {
        return blue_ask;
    }

    public String getOfficial_bid()
    {
        return official_bid;
    }

    public String getOfficial_ask()
    {
        return official_ask;
    }

    public String getCreated_at()
    {
        return created_at;
    }

    public void setBlue_bid(String blue_bid)
    {
        this.blue_bid = blue_bid;
    }

    public void setBlue_ask(String blue_ask)
    {
        this.blue_ask = blue_ask;
    }

    public void setOfficial_bid(String official_bid)
    {
        this.official_bid = official_bid;
    }

    public void setOfficial_ask(String official_ask)
    {
        this.official_ask = official_ask;
    }

    public String getBlueFormatted()
    {
        return Calculations.calculateAverageBidAskFormatted(blue_bid, blue_ask);
    }

    public double getBlueValue()
    {
        return Calculations.calculateAverageBidAskValue(blue_bid, blue_ask);
    }

    public String getUSDFormatted()
    {
        return Calculations.calculateAverageBidAskFormatted(bid, ask);
    }

    public double getUSDValue()
    {
        return Calculations.calculateAverageBidAskValue(bid, ask);
    }

    public String getARSFormatted()
    {
        return Calculations.calculateBlueARSFormatted(getBlueValue(), getUSDValue());
    }

    public String getOfficialFormatted()
    {
        return Calculations.calculateAverageBidAskFormatted(official_bid, official_ask);
    }

    public String getOfficialARSFormatted()
    {
        return Calculations.calculateAverageBidAskFormatted(getOfficialFormatted(), official_ask);
    }

    public double getARSValue()
    {
        return Calculations.calculateBlueARSValue(getBlueValue(), getUSDValue());
    }
}
