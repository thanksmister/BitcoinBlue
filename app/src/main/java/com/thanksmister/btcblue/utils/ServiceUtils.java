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

import java.net.UnknownHostException;

import retrofit.RetrofitError;
import timber.log.Timber;

public class ServiceUtils
{
    public static boolean isNetworkError(Throwable throwable)
    {
        if(throwable instanceof UnknownHostException) {
            return true;
        } else if (throwable instanceof RetrofitError) {
            RetrofitError retroError = (RetrofitError) throwable;
            return (getStatusCode(retroError) == 503);
        }

        return false;
    }
    
    // authorization error
    public static boolean isHttp403Error(Throwable throwable)
    {
        if (throwable instanceof RetrofitError) {
            RetrofitError retroError = (RetrofitError) throwable;
            return (getStatusCode(retroError) == 403);
        } 

        return false;
    }

    // bad request
    public static boolean isHttp400Error(Throwable throwable)
    {
        if (throwable instanceof RetrofitError) {
            RetrofitError retroError = (RetrofitError) throwable;
            return (getStatusCode(retroError) == 400);
        }

        return false;
    }

    // network error
    public static boolean isHttp401Error(Throwable throwable)
    {
        if (throwable instanceof RetrofitError) {
            RetrofitError retroError = (RetrofitError) throwable;
            return (getStatusCode(retroError) == 401);
        }

        return false;
    }

    // server error
    public static boolean isHttp500Error(Throwable throwable)
    {
        if (throwable instanceof RetrofitError) {
            RetrofitError retroError = (RetrofitError) throwable;
            return (getStatusCode(retroError) == 500);
        }

        return false;
    }

    public static boolean isHttp404Error(Throwable throwable)
    {
        if (throwable instanceof RetrofitError) {
            RetrofitError retroError = (RetrofitError) throwable;
            return (getStatusCode(retroError) == 404);
        }

        return false;
    }

    public static int getStatusCode(RetrofitError error) 
    {
        try {
            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                return 503; // Use another code if you'd prefer
            } 
        } catch (Exception e){
            Timber.e(e.getLocalizedMessage());
            return 503; // Use another code if you'd prefer
        }
        
        try {
            return error.getResponse().getStatus();
        } catch(Throwable e){
            Timber.e("Error Status: " + e.getMessage());
        }
        
        return 0;
    }
}
