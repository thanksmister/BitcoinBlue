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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

public class NetworkUtils {
    private static final String HmacSHA256 = "HmacSHA256";

    public static String hmacSha256Hex(String message, String secret) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), HmacSHA256);
        Mac mac = Mac.getInstance(HmacSHA256);
        mac.init(keySpec);
        byte[] bytes = mac.doFinal(message.getBytes());
        return asHex(bytes).toLowerCase();
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String asHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Generates a nonce value for signing.
     * A 63 bit positive integer, for example unix timestamp as milliseconds.
     *
     * @return String generated nonce
     */
    public static String generateNonce() {
        String nonce = String.valueOf(System.currentTimeMillis() / 1000);
        Timber.d("Nonce: " + nonce);
        return nonce;
    }

    /**
     * Generate a Hmac signature for signging requests
     *
     * @param nonce
     * @param hmac_auth_key
     * @param hmac_auth_secret
     * @return
     * @throws Exception
     */
    public static String createSignature(String nonce, String hmac_auth_key, String hmac_auth_secret) {
        try {
            String signature = null;
            String message = String.valueOf(nonce) + "." + hmac_auth_key;
            String digest = hmacSha256Hex(message, hmac_auth_secret);
            signature = message + "." + digest;
            Timber.d("Signature: " + signature);
            return signature;
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }

        return null;
    }

    public static boolean isNetworkConnected(Context context) {
        boolean isConnected;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());

        return isConnected;
    }
}