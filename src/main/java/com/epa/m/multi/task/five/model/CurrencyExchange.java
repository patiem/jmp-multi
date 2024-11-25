package com.epa.m.multi.task.five.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CurrencyExchange {

        private final Map<CurrencyPair, BigDecimal> exchangeRates = new HashMap<>();

        public void setExchangeRate(Currency from, Currency to, BigDecimal rate) {
            if (rate.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Exchange rate unavailable for " + from + " to " + to);
            exchangeRates.put(new CurrencyPair(from, to), rate);
        }

        public Optional<BigDecimal> getExchangeRate(Currency from, Currency to) {
            // direct rate
            CurrencyPair pair = new CurrencyPair(from, to);
            BigDecimal directRate = exchangeRates.get(pair);
            if (directRate != null) {
                return Optional.of(directRate);
            }
            // inverted rate
            CurrencyPair inversePair = new CurrencyPair(to, from);
            BigDecimal inverseRate = exchangeRates.get(inversePair);
            if (inverseRate != null) {
                return Optional.of(BigDecimal.ONE.divide(inverseRate, 4, BigDecimal.ROUND_HALF_EVEN));
            }
            return Optional.empty();
        }

        public BigDecimal exchange(BigDecimal amount, Currency from, Currency to) {
            return getExchangeRate(from, to)
                    .map(amount::multiply)
                    .orElseThrow(() -> new IllegalArgumentException("Exchange rate unavailable for " + from + " to " + to));
        }


    static class CurrencyPair {
        private final Currency from;
        private final Currency to;

        public CurrencyPair(Currency from, Currency to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CurrencyPair that = (CurrencyPair) o;
            return from == that.from && to == that.to;
        }

        @Override
        public int hashCode() {
            return 31 * from.hashCode() + to.hashCode();
        }
    }
}
