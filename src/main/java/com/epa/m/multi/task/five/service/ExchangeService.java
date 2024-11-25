package com.epa.m.multi.task.five.service;

import com.epa.m.multi.task.five.exception.InsufficientFundsException;
import com.epa.m.multi.task.five.model.Account;
import com.epa.m.multi.task.five.model.Currency;
import com.epa.m.multi.task.five.model.CurrencyExchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

public class ExchangeService {

    private static final Logger LOGGER = Logger.getLogger(ExchangeService.class.getName());

    private final AccountService accountService;
    private final CurrencyExchange exchangeRateManager;

    public ExchangeService(AccountService accountDao, CurrencyExchange exchangeRateManager) {
        this.accountService = accountDao;
        this.exchangeRateManager = exchangeRateManager;
    }

    public void executeExchange(String accountName, Currency from, Currency to, BigDecimal amount) throws InsufficientFundsException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("amount must be positive");

        try {
            Account account = accountService.getAccount(accountName);
            BigDecimal rate = exchangeRateManager.getExchangeRate(from, to).orElse(BigDecimal.ZERO);

            synchronized (account) {
                account.withdraw(from, amount);
                BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
                account.deposit(to, convertedAmount);
            }
            accountService.updateAccount(account);
            LOGGER.info(String.format("Exchanged account %s from %s to %s", accountName, from, to));

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}