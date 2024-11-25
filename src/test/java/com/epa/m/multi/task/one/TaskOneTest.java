package com.epa.m.multi.task.one;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskOneTest {

    private TaskOne taskOne;

    @BeforeEach
    public void setUp() {
        taskOne = new TaskOne();
    }

    @Test
    public void testSumMapThread() {
        // Given
        Thread thread1 = new Thread();
        Map<Integer, Integer> map = Collections.synchronizedMap(new HashMap<>());

        // When
        Thread sumThread = taskOne.sumMapThread(thread1, map);

        // Then
        assertNotNull(sumThread);
    }

    @Test
    public void testCreateMapThread() {
        // Given
        Map<Integer, Integer> map = new ConcurrentHashMap<>();

        // When
        Thread createThread = taskOne.createMapThread(map);

        // Then
        assertNotNull(createThread);
    }
}
