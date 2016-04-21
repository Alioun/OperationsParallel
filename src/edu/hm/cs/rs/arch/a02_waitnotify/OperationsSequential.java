package edu.hm.cs.rs.arch.a02_waitnotify;

import static edu.hm.cs.rs.arch.a02_waitnotify.Operation.*;

public class OperationsSequential {
    public static void main(String... args) {
        init(args);
        A1.exec();
        A2.exec();
        A3.exec();
        B1.exec();
        B2.exec();
        B3.exec();
        C1.exec();
        C2.exec();
        C3.exec();
        System.out.println("complete");
    }
}