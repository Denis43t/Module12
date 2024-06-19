package org.example;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeCounter {
    private AtomicInteger timeLeftAfterRun = new AtomicInteger();
    private final static Lock lock = new ReentrantLock();
    private final static Condition condition = lock.newCondition();
    private boolean isReady = false;

    private void awaitCondition() throws InterruptedException {
        lock.lock();
        try {
            while (!isReady) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public void signalCondition() {
        lock.lock();
        try {
            isReady = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void counter() {

        Thread fiveSecondRemain = new Thread(() -> {
            while (true) {
                if (timeLeftAfterRun.get() % 5 == 0 && timeLeftAfterRun.get() != 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("5 second left");

                }
                try {
                    awaitCondition();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread stopwatch = new Thread(() -> {
            while (true) {
                System.out.println(timeLeftAfterRun.getAndIncrement());
                try {
                    Thread.sleep(1000);
                    signalCondition();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        stopwatch.start();
        fiveSecondRemain.start();
    }
}
