package edu.hm.cs.rs.arch.a02_waitnotify;

import static edu.hm.cs.rs.arch.a02_waitnotify.Operation.*;

public class OperationsSequential {
    public static void main(String... args) {
        init(args);
        Runnable runAs = () -> {
            A1.exec();
            A2.exec();
            A3.exec();
        };

        Runnable runBs = () -> {
            B1.exec();
            B2.exec();
            B3.exec();
        };

        Runnable runCs = () -> {
            C1.exec();
            C2.exec();
            C3.exec();
        };

        new Thread(runAs).start();
        new Thread(runBs).start();
        new Thread(runCs).start();

        System.out.println("complete");
    }
}