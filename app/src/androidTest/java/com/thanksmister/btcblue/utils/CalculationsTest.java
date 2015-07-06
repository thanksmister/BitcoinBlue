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

import junit.framework.TestCase;

public class CalculationsTest extends TestCase
{
    public void setUp() throws Exception
    {
        super.setUp();
    }

    public void tearDown() throws Exception
    {
    }

    public void testCalculateTotalSalesSalePriceSame() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateTotalSales(6709.65, 0, 0, 1, 6709.65, 474.18);

        String expectedArsSale = "6709.65";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "0.00";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "1";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "474.18";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "0.00";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }
    
    public void testCalculateTotalSalesSalePriceLess() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateTotalSales(6500, 0, 0, 1, 6709.65, 474.18);

        String expectedArsSale = "6500.00";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "209.65";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "0.96875";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.03125";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "459.36";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "14.82";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateTotalSalesSalePriceGreater() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateTotalSales(7000, 0, 0, 1, 6709.65, 474.18);

        String expectedArsSale = "7000.00";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "290.35";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "1.04327";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.04327";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "494.70";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "20.52";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateSales() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateSale(7000, 1, 8000, 500);

        // #1 (7000)
        String expectedArsSale = "7000.00";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        // #2 8000 - #1 (8000 - 7000)
        String expectedDiffArs = "1000.00";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        // #3 #1/8000 (7000/8000)
        String expectedBtcSale = "0.875";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        // #4 1 - #3 (.875)
        String expectedDiffBtc = "0.125";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        // #5 500 * #3 (0.875)
        String expectedUsdSale = "437.50";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        // #6 500 - #5 (500 - 437.5)
        String expectedDiffUsd = "62.50";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateSalesBitcoinGreater() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateSale(21000, 3, 24000, 1500);

        // #1 (21000)
        String expectedArsSale = "21000.00";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        // #2 24000  - #1 (24000- 21000)
        String expectedDiffArs = "3000.00";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        // #3 #1/24000 (21000/24000) * 3
        String expectedBtcSale = "2.625";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        // #4 1 - #3 (2.625)
        String expectedDiffBtc = "0.375";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        // #5 1500/3 * #3 (1500/3 * 2.625)
        String expectedUsdSale = "1312.50";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        // #6 1500 - #5 (1500 - 1312.50)
        String expectedDiffUsd = "187.50";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateTotalSalesSalePriceAndFeesLess() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateTotalSales(6500, 0, .0001, 1, 7000, 500);

        String expectedArsSale = "6499.30"; //6500 - .70
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "500.70"; // 500 + .70
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "0.92847"; // 0.92857 - .0001
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.07153"; // 0.07143 + .0001
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "464.24"; // 464.28 - .05
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "35.76"; // 35.72 + .05
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateTotalSalesSalePriceAndFeesGreater() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateTotalSales(7500, 0, .0001, 1, 7000, 500);

        String expectedArsSale = "7499.30"; // 7500 - .70
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "499.30"; // 500.00 - 70 
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "1.07133"; // 1.07143 - .0001
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.07133"; // 0.07143 - .0001 
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "535.66"; // 535.72 - .05
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "35.66"; // 35.72 - .05
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateTotalSalesSalePriceAndCommissionLess() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateTotalSales(6500, 1, 0, 1, 7000, 500);

        String expectedArsSale = "6430.00"; //6500 - 70
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "570.00"; // 500 + 70
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "0.91857"; // 0.92857 - .01
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.08143"; // 0.07143 + .01
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "459.28"; // 464.28 - 5.00
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "40.72"; // 35.72 + 5.00
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateTotalSalesSalePriceAndCommissionGreater() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateTotalSales(7500, 1, 0, 1, 7000, 500);

        String expectedArsSale = "7430.00"; // 7500 - 70
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "430.00"; // 500.00 - 70 
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "1.06143"; // 1.07143 - .01
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.06143"; // 0.07143 - .01 
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "530.72"; // 535.72 - 5.00
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "30.72"; // 35.72 - 5.00
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateTotalSalesSalePriceAndCommissionAndFeesLess() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateTotalSales(6500, 1, .0001, 1, 7000, 500);

        String expectedArsSale = "6429.30"; //6500 - 70  - .70
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "570.70"; // 500 + 70 + .70
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "0.91847"; // 0.92857 - .01 - .0001
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.08153"; // 0.07143 + .01 + .0001
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "459.24"; // 464.28 - 5.00 - .05
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "40.76"; // 35.72 + 5.00 + .05
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateTotalSalesSalePriceAndCommissionAndFeesGreater() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateTotalSales(7500, 1, .0001, 1, 7000, 500);

        String expectedArsSale = "7429.30"; // 7500 - 70 - .70
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "429.30"; // 500.00 - 70 - .70
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "1.06133"; // 1.07143 - .01 - .0001
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.06133"; // 0.07143 - .01 - .0001
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "530.66"; // 535.72 - 5.00 - .05
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "30.66"; // 35.72 - 5.00 - .05
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateTotalSalesCommission() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateCommission(1, 1, 7000, 500);

        String expectedArsSale = "6930.00";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "70.00";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "0.99";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.01";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "495.00";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "5.00";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateTotalSalesFees() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateFees(.0001, 1, 7000, 500);

        String expectedArsSale = "6999.30";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "0.70";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "0.9999";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.0001";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "499.95";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "0.05";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }
    
    public void testCalculateSaleARS() throws Exception
    {
       double salesARS = Calculations.calculateSaleARS(0, 1);
       assertEquals(0.0, salesARS);
    }

    public void testCalculateUSDValue() throws Exception
    {
        double saleUSD = Calculations.calculateUSDValue(500, 1);
        assertEquals(500.0, saleUSD);
    }

    public void testCalculateSale() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateSale(6500, 1, 6705.47, 473.89);

        String expectedArsSale = "6500.00";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "205.47";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "0.96936";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.03064";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "459.37";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "14.52";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateCommission() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateCommission(1, 1, 6705.47, 473.88);

        String expectedArsSale = "6638.42";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "67.05";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "0.99";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.01";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "469.14";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "4.74";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateFees() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateFees(.0001, 1, 8000, 500);

        String expectedArsSale = "7999.20";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "0.80";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "0.9999";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.0001";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "499.95";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "0.05";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateFeesIncreaseBitcoin() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateFees(.0001, 3, 24000, 1500);

        // #1 3 - .0001 * 240000/3 (8000)
        String expectedArsSale = "23999.20";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        // #2 24000  -  #1 (24000 - 23999.20
        String expectedDiffArs = "0.80";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "2.9999";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.0001";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "1499.95";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "0.05";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }

    public void testCalculateFeesIncreaseFees() throws Exception
    {
        Calculations.CalculatedValue calculatedValue = Calculations.calculateFees(.0003, 3, 24000, 1500);

        String expectedArsSale = "23997.60";
        assertEquals(expectedArsSale, Conversions.formatCurrencyAmount(calculatedValue.arsSale));

        String expectedDiffArs = "2.40";
        assertEquals(expectedDiffArs, Conversions.formatCurrencyAmount(calculatedValue.diffARS));

        String expectedBtcSale = "2.9997";
        assertEquals(expectedBtcSale, Conversions.formatBitcoinAmount(calculatedValue.btcSale));

        String expectedDiffBtc = "0.0003";
        assertEquals(expectedDiffBtc, Conversions.formatBitcoinAmount(calculatedValue.diffBTC));

        String expectedUsdSale = "1499.85";
        assertEquals(expectedUsdSale, Conversions.formatCurrencyAmount(calculatedValue.usdSale));

        String expectedDiffUsd = "0.15";
        assertEquals(expectedDiffUsd, Conversions.formatCurrencyAmount(calculatedValue.diffUSD));
    }
}