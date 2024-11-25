package com.epa.m.multi.task.one;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NoSynchroThreadSafeMapTest {

    @Test
    public void testConcurrentPutAndGet() throws InterruptedException {
        ThreadSafeMap<Integer, String> map = new NoSynchroThreadSafeMap<>();
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            executorService.submit(() -> map.put(finalI, "Value " + finalI));
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        for (int i = 0; i < numberOfThreads; i++) {
            assertEquals("Value " + i, map.get(i));
        }
    }

    @Test
    public void testConcurrentPutAndRemove() throws InterruptedException {
        ThreadSafeMap<Integer, String> map = new NoSynchroThreadSafeMap<>();
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            executorService.submit(() -> {
                map.put(finalI, "Value " + finalI);
                map.remove(finalI);
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(0, map.size());
    }

    @Test
    public void testSize() throws InterruptedException {
        ThreadSafeMap<Integer, String> map = new NoSynchroThreadSafeMap<>();
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            executorService.submit(() -> map.put(finalI, "Value " + finalI));
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(numberOfThreads, map.size());
    }

    @Test
    public void testRemove() {
        ThreadSafeMap<Integer, String> map = new NoSynchroThreadSafeMap<>();
        map.put(1, "Value 1");
        assertEquals("Value 1", map.get(1));

        map.remove(1);
        assertNull(map.get(1));
    }
}
