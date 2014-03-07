package org.drools.core.concurrent;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.kie.api.concurrent.KieExecutors;

public class ExecutorProviderImpl implements KieExecutors {

    private static final java.util.concurrent.ExecutorService executor = Executors.newCachedThreadPool(new DaemonThreadFactory());

    public Executor getExecutor() {
        return executor;
    }

    public Executor newSingleThreadExecutor() {
        return Executors.newSingleThreadExecutor(new DaemonThreadFactory());
    }

    public <T> CompletionService<T> getCompletionService() {
        return new ExecutorCompletionService<T>(getExecutor());
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    }
}
