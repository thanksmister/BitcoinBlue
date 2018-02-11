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

import android.text.InputFilter;
import android.text.Spanned;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Calculations {
    public static String calculateAverageBidAskFormatted(String bid, String ask) {
        return Conversions.formatCurrencyAmount(calculateAverageBidAskValue(bid, ask));
    }

    public static double calculateAverageBidAskValue(String bid, String ask) {
        double bidDouble = Conversions.convertToDouble(bid);
        double askDouble = Conversions.convertToDouble(ask);

        return (bidDouble + askDouble) / 2;
    }

    public static String calculateBlueARSFormatted(String blueDollar, String bitcoinUSD) {
        double blue = Doubles.convertToDouble(blueDollar);
        double btc = Doubles.convertToDouble(bitcoinUSD);
        return Conversions.formatCurrencyAmount(calculateBlueARSValue(blue, btc));
    }

    public static String calculateBlueARSFormatted(double blueDollar, double bitcoinUSD) {
        return Conversions.formatCurrencyAmount(calculateBlueARSValue(blueDollar, bitcoinUSD));
    }

    public static double calculateBlueARSValue(double blueDollar, double bitcoinUSD) {
        double blue = blueDollar;
        double btc = bitcoinUSD;
        return blue * btc;
    }

    public static double calculateARSValue(double ars, double btc) {
        return ars * btc;
    }

    public static double calculateBTC(double editFiat, double defaultFiat) {
        double btcValue;
        String btc;
        btcValue = editFiat / defaultFiat;
        btc = Conversions.formatBitcoinAmount(btcValue);

        return Doubles.convertToDouble(btc);
    }

    public static double calculateBTCFromFiat(double btcUSD, double fiat) {
        double btcValue = fiat / btcUSD;

        String btc = Conversions.formatBitcoinAmount(btcValue);

        return Doubles.convertToDouble(btc);
    }

    public static double calculateUSDValue(double bitcoinUSD, double btcAmount) {
        return bitcoinUSD * btcAmount;
    }

    public static double calculateSaleBTC(double saleAmountARS, double arsAmount, double btcAmount) {
        String btc = Conversions.formatBitcoinAmount((saleAmountARS / arsAmount) * btcAmount);
        return Doubles.convertToDouble(btc);
    }

    public static double calculateSaleARS(double sale, double btcAmount) {
        String ars = Conversions.formatBitcoinAmount(sale * btcAmount);
        return Doubles.convertToDouble(ars);
    }

    public static String formatCurrency(double amountToFormat) {
        DecimalFormat format = new DecimalFormat("#.##");
        return format.format(amountToFormat);
    }

    public static class DecimalPlacesInputFilter implements InputFilter {
        private final int decimalDigits;

        public DecimalPlacesInputFilter(int decimalDigits) {
            this.decimalDigits = decimalDigits;
        }

        @Override
        public CharSequence filter(CharSequence source,
                                   int start,
                                   int end,
                                   Spanned dest,
                                   int dstart,
                                   int dend) {


            int dotPos = -1;
            int len = dest.length();
            for (int i = 0; i < len; i++) {
                char c = dest.charAt(i);
                if (c == '.' || c == ',') {
                    dotPos = i;
                    break;
                }
            }

            if (dotPos >= 0) {
                // protects against many decimals
                if (source.equals(".") || source.equals(",")) {
                    return "";
                }
                // if the text is entered before the dot
                if (dend <= dotPos) {
                    return null;
                }
                if (len - dotPos > decimalDigits) {
                    return "";
                }
            }

            return null;
        }
    }

    public static CalculatedValue calculateTotalSales(double saleAmount, double commissionAmount, double feeAmount, double btcValue, double arsValue, double usdValue) {
        CalculatedValue fees = calculateFees(feeAmount, btcValue, arsValue, usdValue);
        CalculatedValue sales = calculateSale(saleAmount, btcValue, arsValue, usdValue);
        CalculatedValue commission = calculateCommission(commissionAmount, btcValue, arsValue, usdValue);

        // these are always deducted from final
        double diffBTC = fees.diffBTC + commission.diffBTC;
        double diffARS = fees.diffARS + commission.diffARS;
        double diffUSD = fees.diffUSD + commission.diffUSD;

        double btcSale;
        double arsSale;
        double usdSale;

        if (diffBTC == 0) { // only handle sale amount positive or negative

            btcSale = (saleAmount < arsValue) ? btcValue - sales.diffBTC : btcValue + sales.diffBTC;
            arsSale = (saleAmount < arsValue) ? arsValue - sales.diffARS : arsValue + sales.diffARS;
            usdSale = (saleAmount < arsValue) ? usdValue - sales.diffUSD : usdValue + sales.diffUSD;

            diffBTC = Math.abs(sales.diffBTC - diffBTC);
            diffARS = Math.abs(sales.diffARS - diffARS);
            diffUSD = Math.abs(sales.diffUSD - diffUSD);

        } else if (saleAmount > arsValue) { // handle positive sale amount

            // remove commission and fees which are negative
            btcSale = btcValue + sales.diffBTC - diffBTC;
            arsSale = arsValue + sales.diffARS - diffARS;
            usdSale = usdValue + sales.diffUSD - diffUSD;

            diffBTC = Math.abs(sales.diffBTC - diffBTC);
            diffARS = Math.abs(sales.diffARS - diffARS);
            diffUSD = Math.abs(sales.diffUSD - diffUSD);

        } else { // negative sale amount

            diffBTC = Math.abs(sales.diffBTC + diffBTC);
            diffARS = Math.abs(sales.diffARS + diffARS);
            diffUSD = Math.abs(sales.diffUSD + diffUSD);

            btcSale = btcValue - diffBTC;
            arsSale = arsValue - diffARS;
            usdSale = usdValue - diffUSD;
        }

        return new CalculatedValue(btcSale, arsSale, usdSale, diffBTC, diffARS, diffUSD);
    }

    public static CalculatedValue calculateSale(double saleAmount, double btcValue, double arsValue, double usdValue) {
        if (saleAmount == 0) {
            return new CalculatedValue(btcValue, arsValue, usdValue, 0, 0, 0);
        }

        double btcSale = calculateSaleBTC(saleAmount, arsValue, btcValue);
        double usdSale = usdValue / btcValue * btcSale;

        double diffBTC = Math.abs(btcValue - btcSale);
        double diffARS = 0;
        double diffUSD = 0;

        if (arsValue > 0) {
            diffARS = Math.abs(arsValue - saleAmount);
        }

        if (usdValue > 0) {
            diffUSD = Math.abs(usdValue - usdSale);
        }

        return new CalculatedValue(btcSale, saleAmount, usdSale, diffBTC, diffARS, diffUSD);
    }

    public static CalculatedValue calculateCommission(double commission, double btcValue, double arsValue, double usdValue) {
        if (commission == 0) {
            return new CalculatedValue(btcValue, arsValue, usdValue, 0, 0, 0);
        }

        double commValue = (commission / 100); // %

        double diffBTC = btcValue * commValue;
        double diffARS = arsValue * commValue;
        double diffUSD = usdValue * commValue;

        double btcSale = Math.abs(btcValue - diffBTC);
        double arsSale = Math.abs(arsValue - diffARS);
        double usdSale = Math.abs(usdValue - diffUSD);

        return new CalculatedValue(btcSale, arsSale, usdSale, diffBTC, diffARS, diffUSD);
    }

    public static CalculatedValue calculateFees(double feeAmount, double btcValue, double arsValue, double usdValue) {
        if (feeAmount == 0) {
            return new CalculatedValue(btcValue, arsValue, usdValue, 0, 0, 0);
        }

        double feeValue = btcValue - feeAmount; // minus fees from btc amount

        double arsSale = Calculations.calculateSaleARS((arsValue / btcValue), feeValue);

        double usdSale = Calculations.calculateUSDValue((usdValue / btcValue), feeValue);

        double diffBTC = Math.abs(btcValue - feeValue);
        double diffARS = 0;
        double diffUSD = 0;

        if (arsValue > 0) {
            diffARS = Math.abs(arsValue - arsSale);
        }
        if (usdValue > 0) {
            diffUSD = Math.abs(usdValue - usdSale);
        }

        return new CalculatedValue(feeValue, arsSale, usdSale, diffBTC, diffARS, diffUSD);
    }

    public static class CalculatedValue {
        public double arsSale;
        public double btcSale;
        public double usdSale;
        public double diffARS;
        public double diffBTC;
        public double diffUSD;

        public CalculatedValue(double btcSale, double arsSale, double usdSale, double diffBTC, double diffARS, double diffUSD) {
            this.btcSale = btcSale;
            this.arsSale = arsSale;
            this.usdSale = usdSale;
            this.diffBTC = diffBTC;
            this.diffARS = diffARS;
            this.diffUSD = diffUSD;
        }

        public boolean hasDiffs() {
            return (diffBTC > 0);
        }
    }
}
