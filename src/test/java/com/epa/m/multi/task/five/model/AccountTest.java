package com.epa.m.multi.task.five.model;

import com.epa.m.multi.task.five.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AccountTest {
    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("testAccount");
    }

    @Test
    void testDeposit() {
        account.deposit(Currency.USD, BigDecimal.valueOf(100));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(account.getBalance(Currency.USD)));
    }

    @Test
    void testWithdrawalWithSufficientFunds() throws InsufficientFundsException {
        account.deposit(Currency.USD, BigDecimal.valueOf(200));
        account.withdraw(Currency.USD, BigDecimal.valueOf(150));
        assertEquals(0, BigDecimal.valueOf(50).compareTo(account.getBalance(Currency.USD)));
    }

    @Test
    void testWithdrawalWithInsufficientFunds() {
        account.deposit(Currency.USD, BigDecimal.valueOf(100));
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(Currency.USD, BigDecimal.valueOf(150)));

    }

    @Test
    void testBalanceRetrieval() {
        account.deposit(Currency.EUR, BigDecimal.valueOf(250));
        assertEquals(0, BigDecimal.valueOf(250).compareTo(account.getBalance(Currency.EUR)));
    }

    @Test
    void testMultiCurrencyOperations() throws InsufficientFundsException {
        account.deposit(Currency.USD, BigDecimal.valueOf(100));
        account.deposit(Currency.EUR, BigDecimal.valueOf(200));
        assertNotEquals(account.getBalance(Currency.USD), account.getBalance(Currency.EUR));
        account.withdraw(Currency.EUR, BigDecimal.valueOf(50));
        assertEquals(0, BigDecimal.valueOf(150).compareTo(account.getBalance(Currency.EUR)));
    }

    @Test
    void testThreadSafetyOnConcurrentAccess() throws InterruptedException {
        Thread thread1 = new Thread(() -> account.deposit(Currency.USD, BigDecimal.valueOf(100)));
        Thread thread2 = new Thread(() -> account.deposit(Currency.USD, BigDecimal.valueOf(200)));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        assertEquals(0, BigDecimal.valueOf(300).compareTo(account.getBalance(Currency.USD)));
    }
}
