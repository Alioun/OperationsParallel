package edu.hm.cs.rs.arch.a02_waitnotify;

import static edu.hm.cs.rs.arch.a02_waitnotify.Operation.*;

public class OperationsSequential {
    private int firstCounter;
    private int secondCounter;

    public OperationsSequential(){
        firstCounter = 0;
        secondCounter = 0;
    }

    private void setFirstCounter(int data){
        firstCounter = data;
    }

    private int getFirstCounter(){
        return firstCounter;
    }

    private void setSecondCounter(int data){
        secondCounter = data;
    }

    private int getSecondCounter(){
        return secondCounter;
    }


    public static void main(String... args) {
        init(args);

        OperationsSequential operationsSequential = new OperationsSequential();
        Object monitor = new Object();

        Runnable runAs = () -> {
            System.out.println("A1 started.");
            A1.exec();
            synchronized (monitor){
            operationsSequential.setFirstCounter(operationsSequential.getSecondCounter()+1);
                monitor.notifyAll();
            }
            System.out.println("A2 started.");
            A2.exec();
            synchronized (monitor){
                operationsSequential.setSecondCounter(operationsSequential.getSecondCounter()+1);
                monitor.notifyAll();
            }
            System.out.println("A3 started.");
            A3.exec();
        };

        Runnable runBs = () -> {
            System.out.println("B1 started.");
            B1.exec();
            synchronized (monitor){
                operationsSequential.setFirstCounter(operationsSequential.getFirstCounter()+1);
                monitor.notifyAll();
            }
            System.out.println("B2 started.");
            B2.exec();
            synchronized (monitor){
                operationsSequential.setSecondCounter(operationsSequential.getSecondCounter()+1);
                monitor.notifyAll();
            }
            System.out.println("B3 started.");
            B3.exec();
        };

        Runnable runCs = () -> {
            System.out.println("C1 started.");
            C1.exec();
            synchronized (monitor){
                while(operationsSequential.getFirstCounter()!=2) {
                    System.out.println("waiting ...");
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("woke up ...");
                }
                operationsSequential.setSecondCounter(operationsSequential.getSecondCounter()+1);
                monitor.notifyAll();
            }
            System.out.println("C2 started.");
            C2.exec();
            synchronized (monitor) {
                while (operationsSequential.getSecondCounter() < 2) {
                    System.out.println("waiting ...");
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("woke up ...");
                }
            }
            System.out.println("C3 started.");
            C3.exec();
        };

        new Thread(runAs).start();
        new Thread(runBs).start();
        new Thread(runCs).start();

        System.out.println("complete");
    }
}