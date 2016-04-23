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
            A1.exec();
            synchronized (monitor){
            operationsSequential.setFirstCounter(operationsSequential.getSecondCounter()+1);
                monitor.notifyAll();
            }
            A2.exec();
            synchronized (monitor){
                operationsSequential.setSecondCounter(operationsSequential.getSecondCounter()+1);
                monitor.notifyAll();
            }
            A3.exec();
        };

        Runnable runBs = () -> {
            B1.exec();
            synchronized (monitor){
                operationsSequential.setFirstCounter(operationsSequential.getSecondCounter()+1);
                monitor.notifyAll();
            }
            B2.exec();
            synchronized (monitor){
                operationsSequential.setSecondCounter(operationsSequential.getSecondCounter()+1);
                monitor.notifyAll();
            }
            B3.exec();
        };

        Runnable runCs = () -> {
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
            C3.exec();
        };

        new Thread(runAs).start();
        new Thread(runBs).start();
        new Thread(runCs).start();

        System.out.println("complete");
    }
}