package org.optaplanner.core.config.solver.testutil;

import java.util.concurrent.ThreadFactory;

public class MockThreadFactory implements ThreadFactory {

    private static boolean called;

    public static boolean hasBeenCalled() {
        return called;
    }

    public MockThreadFactory() {
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