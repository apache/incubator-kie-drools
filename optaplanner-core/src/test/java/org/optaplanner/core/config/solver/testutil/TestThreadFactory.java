package org.optaplanner.core.config.solver.testutil;

import java.util.concurrent.ThreadFactory;

public class TestThreadFactory implements ThreadFactory {

    private static boolean called;

    public static boolean hasBeenCalled() {
        return called;
    }

    public TestThreadFactory() {
        called = false;
    }

    @Override
    public Thread newThread(Runnable r) {
        called = true;
        Thread newThread = new Thread(r, "testing thread");
        newThread.setDaemon(false);
        return newThread;
    }

}