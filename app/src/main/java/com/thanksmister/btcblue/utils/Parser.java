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

import com.thanksmister.btcblue.data.api.model.Exchange;
import com.thanksmister.btcblue.data.api.model.ExchangeData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Parser
{
    public static ExchangeData parseExchanges(String response)
    {
        JSONObject jsonObject;
        
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        
        ExchangeData exchangeData = new ExchangeData();
        ArrayList<Exchange> exchanges = new ArrayList<Exchange>();
        //ArrayList<String> keyList = new ArrayList<String>();

        if(jsonObject.has("timestamp")) try {
            exchangeData.setTimestamp(jsonObject.getString("timestamp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator<?> keys = jsonObject.keys();
        while( keys.hasNext() ){
            
            String key = (String) keys.next();
            //keyList.add(key);
            
            try {
                if( jsonObject.get(key) instanceof JSONObject) {
                    JSONObject obj = (JSONObject) jsonObject.get(key);
                    
                    String ask = "";
                    String bid = "";
                    String last = "";
                    String source = "";
                    String display_name = "";
                    String created_at = Dates.getLocalDateMilitaryTime();

                    if(obj.has("rates")) {
                        JSONObject rates = obj.getJSONObject("rates");
                        if(rates.has("ask")) ask = (rates.getString("ask"));
                        if(rates.has("bid")) bid = (rates.getString("bid"));
                        if(rates.has("last")) last =(rates.getString("last"));
                    }

                    if(obj.has("source")) source = (obj.getString("source"));
                    if(obj.has("display_name")) display_name = (obj.getString("display_name"));
                   
                    Exchange exchange = new Exchange(display_name, ask, bid, last, source, created_at);
                    exchanges.add(exchange);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        exchangeData.setExchanges(exchanges);

        return exchangeData;
    }
}
