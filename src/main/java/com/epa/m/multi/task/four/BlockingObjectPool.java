package com.epa.m.multi.task.four;

import java.util.LinkedList;

public class BlockingObjectPool {
    private final LinkedList<Object> pool;
    private final int size;

    /**
     * Creates filled pool of passed size
     *
     * @param size of pool
     */
    public BlockingObjectPool(int size) {
        this.size = size;
        this.pool = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            pool.add(new Object());
        }
    }

    /**
     * Gets object from pool or blocks if pool is empty
     *
     * @return object from pool
     */
    public synchronized Object get() {
        while (pool.isEmpty()) {
            try {
                wait();  // Wait until an object is available.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        Object item = pool.removeFirst();
        notifyAll();  // Notify any threads waiting to put objects.
        return item;
    }

    /**
     * Puts object to pool or blocks if pool is full
     *
     * @param object to be taken back to pool
     */
    public synchronized void take(Object object) {
        while (pool.size() >= size) {
            try {
                wait();  // Wait until there's space in the pool.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        pool.addLast(object);
        notifyAll();  // Notify any threads waiting to get objects.
    }
}
