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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Conversions {

    private Conversions() {
    }

    public static double diffOfTwoValues(String value1, String value2) {
        return convertToDouble(value1) - convertToDouble(value2);
    }

    public static double sumOfTwoValues(String value1, String value2) {
        return convertToDouble(value1) + convertToDouble(value2);
    }

    public static double sumOfTwoValues(double value1, String value2) {
        return value1 + convertToDouble(value2);
    }

    public static String convertDollarsCents(String centValue) {
        // Declaration of variables.
        double dollars;
        double cents;
        String inputNumberString;
        double inputNumber;
        double calculatedAnswer;

        // Convert String to int
        inputNumber = Double.parseDouble(centValue);

        // Calculate the number
        dollars = inputNumber / 100;
        cents = inputNumber % 100;

        return dollars + "." + cents;
    }

    public static String convertToCents(String cents) {
        if (cents == null) return "0.00";
        if (cents.contains(".")) return cents;
        if (cents.length() < 2) {
            return "0.0" + cents;
        } else if (cents.length() == 2) {
            return "0." + cents;
        } else if (cents.length() > 2) {
            String dollars = cents.substring(0, cents.length() - 2);
            String andcents = cents.substring(cents.length() - 2, cents.length());
            return dollars + "." + andcents;
        }

        return cents;
    }

    public static String formatCurrencyAmount(String amount) {
        return formatCurrencyAmount(amount, 2, 2);
    }

    public static String formatBitcoinAmount(Double amount) {
        NumberFormat formatter = new DecimalFormat("###.#####");
        String f = formatter.format(amount);
        return f;
    }


    public static String formatWholeNumber(Double amount) {
        NumberFormat formatter = new DecimalFormat("###");
        String f = formatter.format(amount);
        return f;
    }

    public static String formatCurrencyAmount(Double amount) {
        NumberFormat formatter = new DecimalFormat("###.#####");
        String f = formatter.format(amount);
        return formatCurrencyAmount(f, 2, 2);
    }

    public static String formatCurrencyAmount(Double amount, int max, int min) {
        NumberFormat formatter = new DecimalFormat("###.#####");
        String f = formatter.format(amount);
        return formatCurrencyAmount(f, max, min);
    }

    private static String formatCurrencyAmount(String amount, int maxDecimal, int minDecimal) {
        try {
            if (amount == null) return "";
            if (amount.equals("") || amount.equals(".")) amount = "0.00";
            if (amount.length() > 0 && (amount.lastIndexOf(".") > amount.length())) { // return default if multiple periods
                return "0.00";
            }

            if (amount.contains(",")) amount = amount.replace(",", ".");
            BigDecimal balanceNumber = new BigDecimal(amount);
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(maxDecimal);
            df.setMinimumFractionDigits(minDecimal);
            df.setGroupingUsed(false);

            if (balanceNumber.compareTo(BigDecimal.ZERO) == -1) {
                balanceNumber = balanceNumber.multiply(new BigDecimal(-1));
            }

            return df.format(balanceNumber);

        } catch (NumberFormatException e) {
            return "0.00";
        }
    }

    private static String removeLastChar(String str) {
        str = str.substring(0, str.length() - 1);
        return str;
    }

    public static double convertToDouble(String value) {
        if (value == null || Strings.isBlank(value)) return 0;

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double convertToDoubleOrDefault(String value, double defaultValue) {
        if (value == null)
            return defaultValue;

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return defaultValue;
    }


    public static float convertToFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String formatCurrencyAmount(Float amount, int maxDecimal, int minDecimal) {

        BigDecimal balanceNumber = new BigDecimal(amount);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(maxDecimal);
        df.setMinimumFractionDigits(minDecimal);
        df.setGroupingUsed(false);

        if (balanceNumber.compareTo(BigDecimal.ZERO) == -1) {
            balanceNumber = balanceNumber.multiply(new BigDecimal(-1));
        }

        return df.format(balanceNumber);
    }

    public static String formatBitcoinAmount(String btc, int maxDecimal, int minDecimal) {
        if (btc == null) return null;

        Double balanceNumber = Double.parseDouble(btc);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(maxDecimal);
        df.setMinimumFractionDigits(minDecimal);
        df.setGroupingUsed(false);
        return df.format(balanceNumber);
    }
}