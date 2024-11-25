package com.epa.m.multi.task.five.dao;

import com.epa.m.multi.task.five.model.Account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccountDao {
    private static final String ACCOUNTS_DIR = "accounts";
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public AccountDao() {
        this(ACCOUNTS_DIR);
    }

    public AccountDao(String string) {
        // Ensure directory exists
        new File(ACCOUNTS_DIR).mkdirs();
    }

    public void saveAccount(Account account) throws IOException {
        lock.writeLock().lock();
        File file = new File(ACCOUNTS_DIR, sanitizeFileName(account.getAccountName()) + ".dat");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(account);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Account loadAccount(String accountName) throws IOException, ClassNotFoundException {
        lock.readLock().lock();
        File file = new File(ACCOUNTS_DIR, sanitizeFileName(accountName) + ".dat");
        if (!file.exists()) {
            lock.readLock().unlock();
            throw new FileNotFoundException("Account file not found for " + accountName);
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (Account) in.readObject();
        } finally {
            lock.readLock().unlock();
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9\\._]+", "_");
    }
}