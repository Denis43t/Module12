package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
//        TimeCounter counter=new TimeCounter();
//        counter.counter();
        FizzBuzz fizzBuzz=new FizzBuzz();
        fizzBuzz.run(100);
    }
}