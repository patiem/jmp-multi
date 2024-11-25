package com.epa.m.multi.task.five.model;

import com.epa.m.multi.task.five.exception.InsufficientFundsException;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Account implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(Account.class.getName());

    private final String accountName;
    private final Map<Currency, BigDecimal> balances;

    public Account(String accountName) {
        this.accountName = accountName;
        this.balances = new HashMap<>();
    }

    public synchronized void deposit(Currency currency, BigDecimal amount) {
        balances.merge(currency, amount, BigDecimal::add);
        LOGGER.info("Deposited " + amount + " to account " + accountName);
    }

    public synchronized void withdraw(Currency currency, BigDecimal amount) throws InsufficientFundsException {
        BigDecimal currentBalance = balances.getOrDefault(currency, BigDecimal.ZERO);
        BigDecimal newBalance = currentBalance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        balances.put(currency, newBalance);
        LOGGER.info("Withdrawn " + amount + " to account " + accountName);
    }

    public synchronized BigDecimal getBalance(Currency currency) {
        return balances.getOrDefault(currency, BigDecimal.ZERO);
    }

    public String getAccountName() {
        return accountName;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountName='" + accountName + '\'' +
                ", balances=" + balances +
                '}';
    }
}
