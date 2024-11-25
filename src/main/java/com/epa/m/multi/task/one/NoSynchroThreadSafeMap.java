package com.epa.m.multi.task.one;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NoSynchroThreadSafeMap<K, V> implements ThreadSafeMap<K, V> {
    private Map<K, V> map = new ConcurrentHashMap<>();

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public V remove(K key) {
        return map.remove(key);
    }

    public int size() {
        return map.size();
    }
}
