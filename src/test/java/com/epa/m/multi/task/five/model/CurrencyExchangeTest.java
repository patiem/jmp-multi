package com.epa.m.multi.task.five.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyExchangeTest {

    private CurrencyExchange currencyExchange;

    @BeforeEach
    void setUp() {
        currencyExchange = new CurrencyExchange();
    }

    @Test
    void testSetAndGetExchangeRates() {
        currencyExchange.setExchangeRate(Currency.USD, Currency.EUR, BigDecimal.valueOf(0.85));
        Optional<BigDecimal> rate = currencyExchange.getExchangeRate(Currency.USD, Currency.EUR);
        assertTrue(rate.isPresent());
        assertEquals(0, BigDecimal.valueOf(0.85).compareTo(rate.get()));
    }

    @Test
    void testConversionWithAvailableRates() {
        currencyExchange.setExchangeRate(Currency.USD, Currency.EUR, BigDecimal.valueOf(0.85));
        BigDecimal converted = currencyExchange.exchange(BigDecimal.valueOf(100), Currency.USD, Currency.EUR);
        assertEquals(0, BigDecimal.valueOf(85).compareTo(converted));
    }

    @Test
    void testConversionWithMissingRates() {
        Optional<BigDecimal> rate = currencyExchange.getExchangeRate(Currency.USD, Currency.EUR);
        assertFalse(rate.isPresent());
        assertThrows(IllegalArgumentException.class, () -> currencyExchange.exchange(BigDecimal.valueOf(100), Currency.USD, Currency.EUR));
    }

    @Test
    void testInvalidRateValues() {
        assertThrows(IllegalArgumentException.class, () -> currencyExchange.setExchangeRate(Currency.USD, Currency.EUR, BigDecimal.valueOf(-1)));
        assertThrows(IllegalArgumentException.class, () -> currencyExchange.setExchangeRate(Currency.USD, Currency.EUR, BigDecimal.ZERO));
    }

    @Test
    void testAsymmetricRateVerification() {
        currencyExchange.setExchangeRate(Currency.USD, Currency.EUR, BigDecimal.valueOf(0.85));
        currencyExchange.setExchangeRate(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.18));
        BigDecimal convertToEUR = currencyExchange.exchange(BigDecimal.valueOf(100), Currency.USD, Currency.EUR);
        BigDecimal convertToUSD = currencyExchange.exchange(BigDecimal.valueOf(100), Currency.EUR, Currency.USD);
        assertEquals(0, BigDecimal.valueOf(85).compareTo(convertToEUR));
        assertEquals(0, BigDecimal.valueOf(118).compareTo(convertToUSD));
    }

    @Test
    void testMultiCurrencyConversion() {
        currencyExchange.setExchangeRate(Currency.USD, Currency.EUR, BigDecimal.valueOf(0.85));
        currencyExchange.setExchangeRate(Currency.EUR, Currency.JPY, BigDecimal.valueOf(130));
        BigDecimal amountInEUR = currencyExchange.exchange(BigDecimal.valueOf(100), Currency.USD, Currency.EUR);
        BigDecimal amountInJPY = currencyExchange.exchange(amountInEUR, Currency.EUR, Currency.JPY);
        assertEquals(0, BigDecimal.valueOf(11050).compareTo(amountInJPY));  // 100 USD -> 85 EUR -> 11050 JPY
    }

    @Test
    void testExchangeRateSettingAndGetting() {
        CurrencyExchange currencyExchange = new CurrencyExchange();
        currencyExchange.setExchangeRate(Currency.USD, Currency.EUR, BigDecimal.valueOf(0.85));

        Optional<BigDecimal> rate = currencyExchange.getExchangeRate(Currency.USD, Currency.EUR);

        assertTrue(rate.isPresent());
        assertEquals(0, BigDecimal.valueOf(0.85).compareTo(rate.get()));
    }
}
