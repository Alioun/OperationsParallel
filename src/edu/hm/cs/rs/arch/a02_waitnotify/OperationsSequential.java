package edu.hm.cs.rs.arch.a02_waitnotify;

import static edu.hm.cs.rs.arch.a02_wai tnotify.Operation.*;

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
            A3.exec();
        };

        Runnable runBs = () -> {
            B1.exec();
            synchronized (monitor){
                operationsSequential.setFirstCounter(operationsSequential.getSecondCounter()+1);
                monitor.notifyAll();
            }
            B2.exec();
            B3.exec();
        };

        Runnable runCs = () -> {
            C1.exec();
            synchronized (monitor){
            if(operationsSequential.getFirstCounter()==2){
                //do stuff
            }
            }
            C2.exec();
            C3.exec();
        };

        new Thread(runAs).start();
        new Thread(runBs).start();
        new Thread(runCs).start();

        System.out.println("complete");
    }
}