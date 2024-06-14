package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class TimeCounter {
    private int timeLeftAfterRun=0;
    public void counter() {
        while (true) {

            try {
                System.out.println(timeLeftAfterRun++);
                Thread.sleep(1000);
                Thread fiveSecondRemain = new Thread(() -> {
                    if (timeLeftAfterRun % 5 == 0) {
                        System.out.println("5 second left");
                    }
                });
                fiveSecondRemain.run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
