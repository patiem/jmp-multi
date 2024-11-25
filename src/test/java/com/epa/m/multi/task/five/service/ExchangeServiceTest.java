package com.epa.m.multi.task.five.service;

import com.epa.m.multi.task.five.exception.InsufficientFundsException;
import com.epa.m.multi.task.five.model.Account;
import com.epa.m.multi.task.five.model.Currency;
import com.epa.m.multi.task.five.model.CurrencyExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeServiceTest {

    private AccountService accountService;
    private CurrencyExchange currencyExchange;
    private ExchangeService exchangeService;

    @BeforeEach
    void setUp() {
        accountService = mock(AccountService.class);
        currencyExchange = mock(CurrencyExchange.class);
        exchangeService = new ExchangeService(accountService, currencyExchange);
    }

    @Test
    void testExecuteExchange() throws InsufficientFundsException, IOException, ClassNotFoundException {
        AccountService accountService = mock(AccountService.class);
        CurrencyExchange currencyExchange = mock(CurrencyExchange.class);
        ExchangeService exchangeService = new ExchangeService(accountService, currencyExchange);

        Account account = new Account("testAccount");
        account.deposit(Currency.USD, BigDecimal.valueOf(200));
        when(accountService.getAccount("testAccount")).thenReturn(account);
        when(currencyExchange.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(Optional.of(BigDecimal.valueOf(0.5)));

        exchangeService.executeExchange("testAccount", Currency.USD, Currency.EUR, BigDecimal.valueOf(100));

        verify(accountService).updateAccount(account);
        assertEquals(0, BigDecimal.valueOf(5000, 2).compareTo(account.getBalance(Currency.EUR)));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(account.getBalance(Currency.USD)));
    }

    @Test
    void testExchangeWithNonExistentCurrencyRate() throws Exception {
        Account account = new Account("testAccount");
        when(accountService.getAccount("testAccount")).thenReturn(account);
        when(currencyExchange.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(Optional.empty());

        assertThrows(InsufficientFundsException.class, () -> exchangeService.executeExchange("testAccount", Currency.USD, Currency.EUR, BigDecimal.TEN));
    }

    @Test
    void testExchangeInvalidAmounts() throws Exception {
        Account account = new Account("testAccount");
        when(accountService.getAccount("testAccount")).thenReturn(account);

        assertThrows(IllegalArgumentException.class, () -> exchangeService.executeExchange("testAccount", Currency.USD, Currency.EUR, BigDecimal.valueOf(-100)));
    }

    @Test
    void testExchangeWithInsufficientFunds() throws Exception {
        Account account = new Account("testAccount");
        account.deposit(Currency.USD, BigDecimal.valueOf(50)); // Insufficient for 100 USD exchange
        when(accountService.getAccount("testAccount")).thenReturn(account);
        when(currencyExchange.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(Optional.of(BigDecimal.valueOf(0.85)));

        assertThrows(InsufficientFundsException.class, () -> exchangeService.executeExchange("testAccount", Currency.USD, Currency.EUR, BigDecimal.valueOf(100)));
    }

    @Test
    void testConcurrentExchanges() throws InterruptedException, Exception {
        Account account = new Account("concurrentAccount");
        account.deposit(Currency.USD, BigDecimal.valueOf(1000));
        when(accountService.getAccount("concurrentAccount")).thenReturn(account);
        when(currencyExchange.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(Optional.of(BigDecimal.valueOf(0.85)));

        Thread thread1 = new Thread(() -> {
            try {
                exchangeService.executeExchange("concurrentAccount", Currency.USD, Currency.EUR, BigDecimal.valueOf(100));
            } catch (InsufficientFundsException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                exchangeService.executeExchange("concurrentAccount", Currency.USD, Currency.EUR, BigDecimal.valueOf(200));
            } catch (InsufficientFundsException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        BigDecimal expectedEur = BigDecimal.valueOf(100).add(BigDecimal.valueOf(200)).multiply(BigDecimal.valueOf(0.85));
        assertEquals(0, expectedEur.compareTo(account.getBalance(Currency.EUR)));
    }

}
