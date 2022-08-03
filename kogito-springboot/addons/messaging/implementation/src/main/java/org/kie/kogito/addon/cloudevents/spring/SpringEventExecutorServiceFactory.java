/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addon.cloudevents.spring;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kie.kogito.event.EventExecutorServiceFactory;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.event.KogitoThreadPoolFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringEventExecutorServiceFactory implements EventExecutorServiceFactory {

    @Value("${" + KogitoEventStreams.MAX_THREADS_PROPERTY + ":#{" + KogitoEventStreams.DEFAULT_MAX_THREADS + "}}")
    int numThreads;

    @Value("${" + KogitoEventStreams.QUEUE_SIZE_PROPERTY + ":#{" + KogitoEventStreams.DEFAULT_QUEUE_SIZE + "}}")
    int queueSize;

    @Override
    public ExecutorService getExecutorService(String channelName) {
        return new ThreadPoolExecutor(1, numThreads, 1L, TimeUnit.MINUTES, new ArrayBlockingQueue<>(queueSize), new KogitoThreadPoolFactory(KogitoEventStreams.THREAD_NAME),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
