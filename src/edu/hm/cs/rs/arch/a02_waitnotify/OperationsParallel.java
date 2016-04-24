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
public class OperationsParallel {

    /**
     * Static variable three.
     * Helps with
     */
    private static final int THREE = 3;

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
     * Counts up when all of A, B or C is executed.
     * Never should go above 3.
     */
    private int thirdCounter;

    /**
     * Constructor initializes firstCounter and secondCounter to zero.
     */
    public OperationsParallel() {
        firstCounter = 0;
        secondCounter = 0;
        thirdCounter = 0;
    }

    /**
     * Main method runs all the Operations in order: As and Bs sequentially; C2 if A1,B1,C1 already executed; C3 if
     * two of A2,B2,C2 got executed.
     *
     * @param args Durations of the operations.
     */
    public static void main(String... args) {
        init(args);

        final OperationsParallel operationsParallel = new OperationsParallel();
        final Object monitor = new Object();

        final Runnable runAs = () -> {
            //System.out.println("A1 started.");
            A1.exec();
            operationsParallel.synchronizeCountHelper(monitor, 1);
            //System.out.println("A2 started.");
            A2.exec();
            operationsParallel.synchronizeCountHelper(monitor, 2);
            //System.out.println("A3 started.");
            A3.exec();
            operationsParallel.synchronizeCountHelper(monitor, THREE);
        };

        final Runnable runBs = () -> {
            //System.out.println("B1 started.");
            B1.exec();
            operationsParallel.synchronizeCountHelper(monitor, 1);

            //System.out.println("B2 started.");
            B2.exec();
            operationsParallel.synchronizeCountHelper(monitor, 2);

            //System.out.println("B3 started.");
            B3.exec();
            operationsParallel.synchronizeCountHelper(monitor, THREE);
        };

        final Runnable runCs = () -> {
            //System.out.println("C1 started.");
            C1.exec();
            operationsParallel.synchronizeC2Helper(monitor, operationsParallel);
            //System.out.println("C2 started.");
            C2.exec();
            synchronized (monitor) {
                while (operationsParallel.getSecondCounter() < 2) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
            //System.out.println("C3 started.");
            C3.exec();
            operationsParallel.synchronizeCountHelper(monitor, THREE);
        };

        new Thread(runAs).start();
        new Thread(runBs).start();
        new Thread(runCs).start();

        synchronized (monitor) {
            while (operationsParallel.thirdCounter != THREE) {
                try {
                    monitor.wait();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
            System.out.println("complete");
        }
    }

    private synchronized int getFirstCounter() {
        return firstCounter;
    }

    private synchronized int getSecondCounter() {
        return secondCounter;
    }

    private void setSecondCounter(int data) {
        secondCounter = data;
    }

    /**
     * Counts the counters up in an synchronized block.
     *
     * @param monitor       monitor to synchronize to.
     * @param counterNumber number of the counter to count up.
     */
    private void synchronizeCountHelper(Object monitor, int counterNumber) {
        synchronized (monitor) {
            if (counterNumber == 1) {
                firstCounter++;
            } else if (counterNumber == 2) {
                secondCounter++;
            } else if (counterNumber == THREE) {
                thirdCounter++;
            }
            monitor.notifyAll();
        }
    }

    /**
     * Checks if the firstCounter is two after that it counts up the second counter and notifies all monitors waiting.
     *
     * @param monitor            monitor to synchronize to.
     * @param operationsParallel operationsParallel object to access the first and second counter.
     */
    private void synchronizeC2Helper(Object monitor, OperationsParallel operationsParallel) {
        synchronized (monitor) {
            while (operationsParallel.getFirstCounter() != 2) {
                try {
                    monitor.wait();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
            operationsParallel.setSecondCounter(operationsParallel.getSecondCounter() + 1);
            monitor.notifyAll();
        }
    }
}