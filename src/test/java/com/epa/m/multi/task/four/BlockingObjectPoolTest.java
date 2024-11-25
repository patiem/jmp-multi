package com.epa.m.multi.task.four;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class BlockingObjectPoolTest {

    @Test
    public void testPoolInitialization() {
        int size = 5;
        BlockingObjectPool pool = new BlockingObjectPool(size);
        for (int i = 0; i < size; i++) {
            assertNotNull(pool.get(), "Pool item must not be null");
        }
    }

    @Test
    public void testGetAndTake() {
        BlockingObjectPool pool = new BlockingObjectPool(2);
        Object obj1 = pool.get();
        Object obj2 = pool.get();
        assertNotNull(obj1);
        assertNotNull(obj2);

        // Return objects to pool
        pool.take(obj1);
        pool.take(obj2);
        assertSame(obj1, pool.get(), "Should be able to retrieve the object just put back");
        assertSame(obj2, pool.get(), "Should be able to retrieve the object just put back");
    }

    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    public void testBlockingOnEmpty() throws InterruptedException {
        final BlockingObjectPool pool = new BlockingObjectPool(1);
        // Get the only object
        Object obj = pool.get();
        assertNotNull(obj);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Object> ref = new AtomicReference<>();

        // Start a thread to test blocking
        Thread t = new Thread(() -> {
            ref.set(pool.get());
            latch.countDown();
        });
        t.start();

        // Delay to ensure the thread attempts to get
        Thread.sleep(200);

        // Return object to pool, should unblock the thread
        pool.take(obj);
        latch.await();  // Ensure thread completes
        assertNotNull(ref.get());
    }

    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    public void testBlockingOnFull() throws InterruptedException {
        final BlockingObjectPool pool = new BlockingObjectPool(1);
        Object obj = pool.get();
        assertNotNull(obj);

        CountDownLatch latch = new CountDownLatch(1);

        // Start a thread to test blocking
        Thread t = new Thread(() -> {
            pool.take(new Object()); // should block since pool is full
            latch.countDown();
        });
        t.start();

        // Delay to ensure the thread attempts to take
        Thread.sleep(200);

        // Retrieve object from pool, should unblock the thread
        pool.get();
        latch.await();  // Ensure thread completes
    }
}
