package com.epa.m.multi.task.five.service;

import com.epa.m.multi.task.five.dao.AccountDao;
import com.epa.m.multi.task.five.model.Account;
import com.epa.m.multi.task.five.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceTest {
    private AccountDao accountDao;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountDao = mock(AccountDao.class);
        accountService = new AccountService(accountDao);
    }

    @Test
    void testDepositToAccount() throws IOException, ClassNotFoundException {
        AccountDao accountDao = mock(AccountDao.class);
        AccountService accountService = new AccountService(accountDao);
        Account account = new Account("testAccount");

        when(accountDao.loadAccount("testAccount")).thenReturn(account);

        accountService.depositToAccount("testAccount", Currency.USD, BigDecimal.valueOf(100));

        verify(accountDao).saveAccount(account);
        assertEquals(0, BigDecimal.valueOf(100).compareTo(account.getBalance(Currency.USD)));
    }

    @Test
    void testCreateAccountFailure() throws IOException {
        doThrow(new IOException()).when(accountDao).saveAccount(any(Account.class));

        assertThrows(IOException.class, () -> accountService.createAccount("testAccount"));
    }

    @Test
    void testDepositNegativeAmount() throws IOException, ClassNotFoundException {
        Account account = new Account("testAccount");
        when(accountDao.loadAccount("testAccount")).thenReturn(account);

        assertThrows(IllegalArgumentException.class, () -> accountService.depositToAccount("testAccount", Currency.USD, BigDecimal.valueOf(-100)));
    }

    @Test
    void testDepositToNonExistentAccount() throws IOException, ClassNotFoundException {
        when(accountDao.loadAccount("nonExistentAccount")).thenThrow(new IOException("Account not found"));

        assertThrows(IOException.class, () -> accountService.depositToAccount("nonExistentAccount", Currency.USD, BigDecimal.valueOf(100)));
    }

    @Test
    void testConcurrentDeposits() throws InterruptedException, IOException, ClassNotFoundException {
        Account account = new Account("concurrentAccount");
        when(accountDao.loadAccount("concurrentAccount")).thenReturn(account);

        Thread thread1 = new Thread(() -> {
            try {
                accountService.depositToAccount("concurrentAccount", Currency.USD, BigDecimal.valueOf(100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                accountService.depositToAccount("concurrentAccount", Currency.USD, BigDecimal.valueOf(200));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // The total deposit should be the sum of both thread deposits
        assertEquals(0, BigDecimal.valueOf(300).compareTo(account.getBalance(Currency.USD)));
    }
}
