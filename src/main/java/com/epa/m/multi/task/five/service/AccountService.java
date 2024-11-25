package com.epa.m.multi.task.five.service;

import com.epa.m.multi.task.five.dao.AccountDao;
import com.epa.m.multi.task.five.model.Account;
import com.epa.m.multi.task.five.model.Currency;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;

public class AccountService {
    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());

    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void createAccount(String accountName) throws IOException {
        Account account = new Account(accountName);
        accountDao.saveAccount(account);
        LOGGER.info("Account created: " + accountName);
    }

    public void updateAccount(Account account) throws IOException {
        accountDao.saveAccount(account);
        LOGGER.info("Account updated: " + account.getAccountName());
    }

    public void depositToAccount(String accountName, Currency currency, BigDecimal amount) throws IOException, ClassNotFoundException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("amount cannot be negative");
        Account account = accountDao.loadAccount(accountName);
        account.deposit(currency, amount);
        accountDao.saveAccount(account);
        LOGGER.info("Account deposited: " + account.getAccountName());
    }

    public Account getAccount(String accountName) throws IOException, ClassNotFoundException {
        return accountDao.loadAccount(accountName);
    }
}