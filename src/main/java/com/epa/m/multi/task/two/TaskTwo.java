package com.epa.m.multi.task.two;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;

public class TaskTwo {
    static final List<Integer> numbers = new ArrayList<>();
    static final Random random = new Random();
    static volatile boolean running = true;
    private static final Logger logger = Logger.getLogger(TaskTwo.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Thread writerThread = createWriterThread();
        Thread sumThread = createSumThread();
        Thread squareRootThread = createSquareRootThread();

        writerThread.start();
        sumThread.start();
        squareRootThread.start();
        stopThreads(5000);
    }


    public static Thread createWriterThread() {
        return new Thread(() -> {
            int writeCount = 0;
            while (running) {
                synchronized (numbers) {
                    int num = random.nextInt(100);
                    numbers.add(num);
                    if (++writeCount % 5 == 0) {
                        logger.log(Level.INFO, "Writer Thread: Added fifth value {0}", num);
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "WriterThread");
    }

    public static Thread createSumThread() {
        return new Thread(() -> {
            int sumCount = 0;
            while (running) {
                int sum = 0;
                synchronized (numbers) {
                    for (int number : numbers) {
                        sum += number;
                    }
                    if (++sumCount % 5 == 0) {
                        logger.log(Level.INFO, "Sum Thread: Computed fifth sum {0}", sum);
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "SumThread");
    }

    public static Thread createSquareRootThread() {
        return new Thread(() -> {
            int sqrtCount = 0;
            while (running) {
                double sumOfSquares = 0;
                synchronized (numbers) {
                    for (int number : numbers) {
                        sumOfSquares += Math.pow(number, 2);
                    }
                    if (++sqrtCount % 5 == 0) {
                        logger.log(Level.INFO, "Square Root Thread: Computed fifth square root {0}", Math.sqrt(sumOfSquares));
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "SquareRootThread");
    }

    public static void stopThreads(int millis) throws InterruptedException {
        Thread.sleep(millis);
        running = false;
    }
}

//Single lock (synchronized block) with the numbers collection is used to avoid
//both data inconsistency and deadlock.
//Each thread accesses the shared resource (numbers list) in a synchronized block
//ensuring atomicity of operations.
//To simulate some computation time and to decrease the likelihood of high contention
//on the numbers object lock, we made each thread sleep for 100 milliseconds after each operation.
//This setup imitates a more realistic scenario where processing takes some amount of time.
//Deadlocks are avoided as there is only one shared resource and the threads synchronize
//using the same object, ensuring that no two threads can hold the lock on numbers
//concurrently while trying to acquire another lock.