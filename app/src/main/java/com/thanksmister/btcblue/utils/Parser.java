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

package com.thanksmister.btcblue.utils;

import com.thanksmister.btcblue.data.api.model.Bluelytic;
import com.thanksmister.btcblue.data.api.model.Exchange;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Parser {

    public static Exchange parseExchange(String response) {
        JSONObject jsonObject;
        String created_at = Dates.getLocalDateTime();
        Exchange exchange = new Exchange("", "", "", "", "", created_at);
        try {
            jsonObject = new JSONObject(response);
            String ask = "";
            String bid = "";
            String last = "";
            String source = "";
            String display_name = "";
            if (jsonObject.has("symbols")) {
                JSONObject symbols = jsonObject.getJSONObject("symbols");
                if (symbols.has("BTCUSD")) {
                    JSONObject BTCUSD = symbols.getJSONObject("BTCUSD");
                    if (BTCUSD.has("ask")) ask = (BTCUSD.getString("ask"));
                    if (BTCUSD.has("bid")) bid = (BTCUSD.getString("bid"));
                    if (BTCUSD.has("last")) last = (BTCUSD.getString("last"));
                }
            }

            if (jsonObject.has("name")) source = (jsonObject.getString("name"));
            if (jsonObject.has("display_name")) display_name = (jsonObject.getString("display_name"));
            exchange = new Exchange(display_name, ask, bid, last, source, created_at);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return exchange;
    }

    public static List<Bluelytic> parseBluelytic(String response) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        ArrayList<Bluelytic> items = new ArrayList<>();
        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            try {
                if (jsonObject.get(key) instanceof JSONObject) {
                    JSONObject obj = (JSONObject) jsonObject.get(key);
                    Bluelytic bluelytic = new Bluelytic();
                    bluelytic.source = key;
                    bluelytic.value_avg = obj.getString("value_avg");
                    bluelytic.value_sell = obj.getString("value_sell");
                    bluelytic.value_buy = obj.getString("value_buy");
                    bluelytic.last_update = jsonObject.getString("last_update");
                    items.add(bluelytic);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }
}