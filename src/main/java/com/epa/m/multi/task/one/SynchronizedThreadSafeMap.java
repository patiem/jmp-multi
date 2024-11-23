package com.epa.m.multi.task.one;

import java.util.HashMap;
import java.util.Map;

public class SynchronizedThreadSafeMap<K, V> implements ThreadSafeMap<K, V> {
    private final Map<K, V> map = new HashMap<>();


    @Override
    public synchronized void put(K key, V value) {
        map.put(key, value);
    }

    @Override
    public synchronized V get(K key) {
        return map.get(key);
    }

    @Override
    public synchronized V remove(K key) {
        return map.remove(key);
    }

    @Override
    public synchronized int size() {
        return map.size();
    }
}