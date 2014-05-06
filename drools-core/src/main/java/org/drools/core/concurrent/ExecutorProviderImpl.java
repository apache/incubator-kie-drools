package org.drools.core.concurrent;

import org.kie.api.concurrent.KieExecutors;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorProviderImpl implements KieExecutors {

    private static class ExecutorHolder {
        private static final java.util.concurrent.ExecutorService executor;

        static {
            executor = new ThreadPoolExecutor(0, Pool.SIZE,
                                              60L, TimeUnit.SECONDS,
                                              new SynchronousQueue<Runnable>(),
                                              new DaemonThreadFactory());
        }
    }

    public Executor getExecutor() {
        return ExecutorHolder.executor;
    }

    public Executor newSingleThreadExecutor() {
        return Executors.newSingleThreadExecutor(new DaemonThreadFactory());
    }

    public <T> CompletionService<T> getCompletionService() {
        return new ExecutorCompletionService<T>(getExecutor());
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        private static final AtomicInteger threadCount = new AtomicInteger();

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("drools-worker-" + threadCount.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }
}
