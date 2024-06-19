package org.example;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class FizzBuzz {
    private final static Lock lock = new ReentrantLock();
    private final static Condition condition = lock.newCondition();
    private boolean isReady = false;
    private ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
    private ExecutorService worker = Executors.newFixedThreadPool(4);
    private AtomicInteger num = new AtomicInteger();

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

    private void fizz(AtomicInteger number) {
        if (number.get() % 3 == 0 && number.get() % 5 != 0) {
            queue.add("Fizz");
            number.getAndIncrement();
        } else {
            try {
                awaitCondition();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void buzz(AtomicInteger number) {
        if (number.get() % 5 == 0 && number.get() % 3 != 0) {
            queue.add("Buzz");
            number.getAndIncrement();
        } else {
            try {
                awaitCondition();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fizzbuzz(AtomicInteger number) {
        if (number.get() % 15 == 0) {
            queue.add("FizzBuzz");
            number.getAndIncrement();
        } else {
            try {
                awaitCondition();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void number(AtomicInteger number) {
        for (int i = 0; i < queue.size(); i++) {
            System.out.println(queue.poll());
        }
    }


    public void run(int n) {
        num.set(1);
        for (int i = 1; i <= n; i++) {
            worker.submit(() -> fizz(num));
            worker.submit(() -> buzz(num));
            worker.submit(() -> fizzbuzz(num));
            if (num.get()%3!=0 && num.get()%5!=0){
                try {
                    Thread.sleep(50);
                    queue.add(num.getAndIncrement());
                    signalCondition();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            worker.submit(() -> number(num));
        }
        worker.shutdown();
    }
}
