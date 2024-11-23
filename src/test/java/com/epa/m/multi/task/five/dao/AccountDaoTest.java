package com.epa.m.multi.task.five.dao;

import com.epa.m.multi.task.five.model.Account;
import com.epa.m.multi.task.five.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountDaoTest {
    @TempDir
    Path tempDir;

    @Test
    void testSaveAndLoadAccount() throws IOException, ClassNotFoundException {
        AccountDao accountDao = new AccountDao(tempDir.toString());
        Account account = new Account("testAccount");
        accountDao.saveAccount(account);

        Account loadedAccount = accountDao.loadAccount("testAccount");

        assertEquals(account.getAccountName(), loadedAccount.getAccountName());
    }

    @Test
    void testLoadNonExistentAccount() {
        AccountDao accountDao = new AccountDao(tempDir.toString());
        assertThrows(IOException.class, () -> accountDao.loadAccount("nonExistentAccount"));
    }

    @Test
    void testConcurrentAccess() throws InterruptedException, IOException, ClassNotFoundException {
        AccountDao accountDao = new AccountDao(tempDir.toString());
        Account account = new Account("concurrentAccount");

        Thread thread1 = new Thread(() -> {
            try {
                accountDao.saveAccount(account);
                Thread.sleep(100);
                Account loaded = accountDao.loadAccount("concurrentAccount");
                loaded.deposit(Currency.USD, BigDecimal.valueOf(100));
                accountDao.saveAccount(loaded);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(50); // Make sure this runs slightly after thread1 starts
                Account loaded = accountDao.loadAccount("concurrentAccount");
                loaded.deposit(Currency.USD, BigDecimal.valueOf(200));
                accountDao.saveAccount(loaded);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        Account finalAccount = accountDao.loadAccount("concurrentAccount");
        // Check if the final balance is the total of the deposits from both threads
        assertEquals(0, BigDecimal.valueOf(300).compareTo(finalAccount.getBalance(Currency.USD)));
    }
}