package org.drools.spi;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExecutorFactory {

    private static final boolean CAN_SPAWN_THREADS = false;

    private ExecutorFactory() { }

    private static class ExecutorServiceHolder {
        private static final ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });;
    }

    public static Executor getExecutor() {
        return CAN_SPAWN_THREADS ? ExecutorServiceHolder.executor : SYNC_EXECUTOR;
    }

    public static <T> ExecutorCompletionService<T> getExecutorCompletionService() {
        return new ExecutorCompletionService<T>(getExecutor());
    }

    private static final SyncExecutor SYNC_EXECUTOR = new SyncExecutor();

    private static class SyncExecutor implements Executor {
        public void execute(Runnable runnable) {
            runnable.run();
        }
    }
}
