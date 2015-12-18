/*
 * Copyright (c) 2015. DusApp
 */

package com.thanksmister.btcblue.data.api.transforms;

import com.thanksmister.btcblue.data.api.model.Bluelytic;
import com.thanksmister.btcblue.utils.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import retrofit.client.Response;
import rx.functions.Func1;

public class ResponseToBluelytics implements Func1<Response, List<Bluelytic>>
{
    @Override
    public List<Bluelytic> call(Response response)
    {
        //Try to get response body
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Parser.parseBluelytic(sb.toString());
    }
}
