package org.example;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class FizzBuzz {
    private final static Lock lock=new ReentrantLock();
    private final static Condition condition=lock.newCondition();
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

    public void run(int n) {
        AtomicInteger number = new AtomicInteger();
        number.set(1);
        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
        ExecutorService worker = Executors.newFixedThreadPool(4);
        worker.submit(() -> {
            while (number.get() <= n) {
                if (number.get() % 3 == 0 && number.get() % 5 != 0 && number.get()%15!=0) {
                    queue.add("Fizz");
                    number.getAndIncrement();
                    signalCondition();
                } else {
                    try {
                            awaitCondition();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        worker.submit(() -> {
            while (number.get() <= n) {
                if (number.get() % 5 == 0 && number.get() % 3 != 0 && number.get()%15!=0) {
                    queue.add("Buzz");
                    number.getAndIncrement();
                    signalCondition();
                } else {
                    try {
                        awaitCondition();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        worker.submit(()->{
            while (number.get() <= n) {
                if (number.get() % 3 != 0 && number.get() % 5 != 0 && number.get()%15!=0) {
                    queue.add(number.get());
                    number.getAndIncrement();
                    signalCondition();
                } else {
                    try {
                        awaitCondition();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        worker.submit(()->{
            while (number.get() <= n) {
                if (number.get() % 15 == 0) {
                    queue.add("FizzBuzz");
                    number.getAndIncrement();
                    signalCondition();
                } else {
                    try {
                        awaitCondition();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        worker.submit(() -> {
            while (true) {
                if (queue.isEmpty()) {

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                while (!queue.isEmpty()) {
                    System.out.println(queue.poll());
                }
            }
        });
    }
}
