package com.epa.m.multi.task.five;

import com.epa.m.multi.task.five.dao.AccountDao;
import com.epa.m.multi.task.five.exception.InsufficientFundsException;
import com.epa.m.multi.task.five.model.Currency;
import com.epa.m.multi.task.five.model.CurrencyExchange;
import com.epa.m.multi.task.five.service.AccountService;
import com.epa.m.multi.task.five.service.ExchangeService;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ExchangeApp {

    private static final Logger LOGGER = Logger.getLogger(ExchangeApp.class.getName());


    public static void main(String[] args) {
        // Setup services and data
        var accountDao = new AccountDao();
        var accountService = new AccountService(accountDao);
        var currencyExchange = new CurrencyExchange();
        var exchangeService = new ExchangeService(accountService, currencyExchange);

        // Sample exchange rates
        currencyExchange.setExchangeRate(Currency.USD, Currency.EUR, BigDecimal.valueOf(0.85));
        currencyExchange.setExchangeRate(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.18));

        try {
            // Create and populate account
            var accountName = "sampleAccount";
            accountService.createAccount(accountName);
            accountService.depositToAccount(accountName, Currency.USD, BigDecimal.valueOf(1000));
            accountService.depositToAccount(accountName, Currency.EUR, BigDecimal.valueOf(500));

            // Execute concurrent exchanges
            var executorService = Executors.newFixedThreadPool(2);
            for (int i = 0; i < 10; i++) {
                int finalI = i;
                executorService.submit(() -> {
                    try {
                        if (finalI % 2 == 0) {
                            exchangeService.executeExchange(accountName, Currency.USD, Currency.EUR, BigDecimal.valueOf(100));
                        } else {
                            exchangeService.executeExchange(accountName, Currency.EUR, Currency.USD, BigDecimal.valueOf(100));
                        }
                    } catch (InsufficientFundsException e) {
                        LOGGER.info(e.getMessage());
                    }
                });
            }

            // Shutdown executor and await termination
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);

            // Display final account state
            System.out.println(accountService.getAccount(accountName));
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
    }
}
