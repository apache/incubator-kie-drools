package org.kie.api.concurrent;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorService;

import org.kie.api.internal.utils.KieService;

public interface KieExecutors extends KieService {

    ExecutorService getExecutor();

    ExecutorService newSingleThreadExecutor();

    ExecutorService newFixedThreadPool();

    ExecutorService newFixedThreadPool(int nThreads);

    <T> CompletionService<T> getCompletionService();

    public static class Pool {
        public static int SIZE = Runtime.getRuntime().availableProcessors();
    }
}
