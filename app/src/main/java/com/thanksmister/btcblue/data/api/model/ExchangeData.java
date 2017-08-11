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

import java.util.ArrayList;

public class ExchangeData {
    private String timestamp;
    private ArrayList<Exchange> exchanges;

    private Integer index = 0;


    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<Exchange> getExchanges() {
        return exchanges;
    }

    public void setExchanges(ArrayList<Exchange> exchanges) {
        this.exchanges = exchanges;
    }

    public ExchangeData() {
        exchanges = new ArrayList<Exchange>();

    }


}
