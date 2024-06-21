package org.example;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class FizzBuzz {
    private final ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
    private final ExecutorService worker = Executors.newFixedThreadPool(4);
    private final AtomicInteger num = new AtomicInteger();
    private int counter;
    CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
        if (num.get() % 3 != 0 && num.get() % 5 != 0 && num.get()<=counter) {
            queue.add(num.getAndIncrement());
        }
    });

    private void fizz(AtomicInteger number) {
        if (number.get() % 3 == 0 && number.get() % 5 != 0) {
            queue.add("Fizz");
            number.getAndIncrement();
        }
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private void buzz(AtomicInteger number) {
        if (number.get() % 5 == 0 && number.get() % 3 != 0) {
            queue.add("Buzz");
            number.getAndIncrement();
        }
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private void fizzbuzz(AtomicInteger number) {
        if (number.get() % 15 == 0) {
            queue.add("FizzBuzz");
            number.getAndIncrement();
        }
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private void number(AtomicInteger number) {
        for (int i = 0; i < queue.size(); i++) {
            System.out.print(queue.poll() + " ");
        }
    }


    public void run(int n) {
        num.set(1);
        for (int i=1;i<=n;i++){
            counter=i;
            worker.submit(() -> fizz(num));
            worker.submit(() -> buzz(num));
            worker.submit(() -> fizzbuzz(num));
            worker.submit(() -> number(num));

        }
        worker.shutdown();
    }
}
