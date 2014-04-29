package org.drools.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorProviderImpl implements ExecutorProvider {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorProviderImpl.class);
    private static final AtomicInteger threadCount = new AtomicInteger();
    private static final java.util.concurrent.ExecutorService executor;

    static {
        // Don't saturate the CPUs with the JIT
        int poolSize = Math.max(Runtime.getRuntime().availableProcessors() / 2, 1);
        poolSize = getExecutorParameter("drools.executor.poolSize", poolSize);
        int queueSize = getExecutorParameter("drools.executor.queueSize", 1000);
        BlockingQueue<Runnable> workQueue =
                queueSize > 0 ? new LinkedBlockingQueue<Runnable>(queueSize) : new SynchronousQueue<Runnable>();
        executor = new ThreadPoolExecutor(0, poolSize, 60L, TimeUnit.SECONDS, workQueue,
                new ThreadFactory() {
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("drools-" + threadCount.incrementAndGet());
                        t.setDaemon(true);
                        return t;
                    }
                });
    }

    private static int getExecutorParameter(String key, int defaultValue) {
        String property = System.getProperty(key);
        if (property != null && property.length() > 0) {
            try {
                return Integer.parseInt(property);
            } catch (NumberFormatException e) {
                logger.error("The value of {} cannot be parsed as an integer", key, e);
            }
        }
        return defaultValue;
    }

    public Executor getExecutor() {
        return executor;
    }

    public <T> CompletionService<T> getCompletionService() {
        return new ExecutorCompletionService<T>(getExecutor());
    }
}
