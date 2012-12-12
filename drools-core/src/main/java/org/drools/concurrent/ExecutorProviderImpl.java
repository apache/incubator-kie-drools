package org.drools.concurrent;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;

import org.kie.concurrent.KieExecutors;

public class ExecutorProviderImpl implements KieExecutors {

    private static final java.util.concurrent.ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });;

    public Executor getExecutor() {
        return executor;
    }

    public <T> CompletionService<T> getCompletionService() {
        return new ExecutorCompletionService<T>(getExecutor());
    }
}
