/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KogitoEventExecutor {

    public static final String MAX_THREADS_PROPERTY = "kogito.quarkus.events.threads.poolSize";
    public static final int DEFAULT_MAX_THREADS_INT = 10;
    public static final String DEFAULT_MAX_THREADS = "10";
    public static final int DEFAULT_QUEUE_SIZE_INT = 1;
    public static final String DEFAULT_QUEUE_SIZE = "1";
    public static final String QUEUE_SIZE_PROPERTY = "kogito.quarkus.events.threads.queueSize";
    public static final String BEAN_NAME = "kogito-event-executor";

    public static ExecutorService getEventExecutor() {
        return getEventExecutor(DEFAULT_MAX_THREADS_INT, DEFAULT_QUEUE_SIZE_INT);
    }

    public static ExecutorService getEventExecutor(int numOfThreads, int blockQueueSize) {
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(blockQueueSize);
        RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        return new ThreadPoolExecutor(1, numOfThreads, 1L, TimeUnit.MINUTES, blockingQueue, rejectedExecutionHandler);
    }

    private KogitoEventExecutor() {
    }

}
