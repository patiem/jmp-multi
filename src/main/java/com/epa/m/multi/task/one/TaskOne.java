package com.epa.m.multi.task.one;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskOne {
    public static void main(String[] args) {
        Map<Integer, Integer> hMap = new HashMap<>();
        Map<Integer, Integer> cMap = new ConcurrentHashMap<>();
        Map<Integer, Integer> sMap = Collections.synchronizedMap(new HashMap<>());

        TaskOne experiment = new TaskOne();
        experiment.startExperiment(hMap);
        experiment.startExperiment(cMap);
        experiment.startExperiment(sMap);
    }

    private  void startExperiment(Map<Integer, Integer> map) {
        long startTime = System.currentTimeMillis();
        try {
            Thread thread1 = createMapThread(map);
            Thread thread2 = sumMapThread(thread1, map);
            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.err.println("Experiment execution was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();  //Re-setting the interrupt status of the thread
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Experiment execution time: " + duration + "ms");
        }
    }

    private Thread sumMapThread(Thread thread1, Map<Integer, Integer> map) {
        return new Thread(() -> {
            int sum;
            while (thread1.isAlive()) {
                sum = getSum(map);
                System.out.println("Current sum: " + sum);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            sum = getSum(map);
            System.out.println("Final sum: " + sum);
        });
    }

    private Thread createMapThread(Map<Integer, Integer> map) {
        return new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                map.put(i, i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private int getSum(Map<Integer, Integer> map) {
        return map.values().stream().mapToInt(Integer::intValue).sum();
    }
}

//Re-setting the interrupt status of the thread after catching an InterruptedException
// ensures that the state of being interrupted propagates correctly
// and allows other parts of application to respond to the interrupt if necessary.
// This practice leads to more robust, responsive, and correct multithreaded behavior.