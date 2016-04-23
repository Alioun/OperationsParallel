package edu.hm.cs.rs.arch.a02_waitnotify;

import static edu.hm.cs.rs.arch.a02_waitnotify.Operation.*;

/**
 * Executes different operations in different Threads.
 * <p>
 * Hochschule München, Fakultät 07
 * Softwarearchitektur, IF4B
 *
 * @author Florian Frank, Alioun Diagne
 * @version 2016-04-23
 **/
public class OperationsSequential {
    /**
     * Counts up when A1 or B1 is executed.
     * Never should go above 2.
     */
    private int firstCounter;

    /**
     * Counts up when A2, B2 or C2 is executed.
     * Never should go above 3.
     */
    private int secondCounter;

    /**
     * Constructor initializes firstCounter and secondCounter to zero.
     */
    public OperationsSequential() {
        firstCounter = 0;
        secondCounter = 0;
    }

    /**
     * Main method runs all the Operations in order: As and Bs sequentially; C2 if A1,B1,C1 already executed; C3 if
     * two of A2,B2,C2 got executed.
     *
     * @param args Durations of the operations.
     */
    public static void main(String... args) {
        init(args);

        final OperationsSequential operationsSequential = new OperationsSequential();
        final Object monitor = new Object();

        final Runnable runAs = () -> {
            System.out.println("A1 started.");
            A1.exec();
            operationsSequential.synchronizeCountHelper(monitor, 1);
            System.out.println("A2 started.");
            A2.exec();
            operationsSequential.synchronizeCountHelper(monitor, 2);

            System.out.println("A3 started.");
            A3.exec();
        };

        final Runnable runBs = () -> {
            System.out.println("B1 started.");
            B1.exec();
            operationsSequential.synchronizeCountHelper(monitor, 1);

            System.out.println("B2 started.");
            B2.exec();
            operationsSequential.synchronizeCountHelper(monitor, 2);

            System.out.println("B3 started.");
            B3.exec();
        };

        final Runnable runCs = () -> {
            System.out.println("C1 started.");
            C1.exec();
            synchronized (monitor) {
                while (operationsSequential.getFirstCounter() != 2) {
                    System.out.println("waiting ...");
                    try {
                        monitor.wait();
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                    System.out.println("woke up ...");
                }
                operationsSequential.setSecondCounter(operationsSequential.getSecondCounter() + 1);
                monitor.notifyAll();
            }
            System.out.println("C2 started.");
            C2.exec();
            synchronized (monitor) {
                while (operationsSequential.getSecondCounter() < 2) {
                    System.out.println("waiting ...");
                    try {
                        monitor.wait();
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
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

    private int getFirstCounter() {
        return firstCounter;
    }

    private int getSecondCounter() {
        return secondCounter;
    }

    private void setSecondCounter(int data) {
        secondCounter = data;
    }

    /**
     * Counts the counters up in an synchronized block.
     * @param monitor monitor to synchronize to.
     * @param counterNumber number of the counter to count up.
     */
    private void synchronizeCountHelper(Object monitor, int counterNumber) {
        synchronized (monitor) {
            if (counterNumber == 1) {
                firstCounter++;
            } else if (counterNumber == 2) {
                secondCounter++;
            }
            monitor.notifyAll();
        }
    }
}