package com.epa.m.multi.task.one;

public interface ThreadSafeMap<K, V> {
    void put(K key, V value);
    V get(K key);
    V remove(K key);
    int size();
}
