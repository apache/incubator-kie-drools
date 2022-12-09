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

package org.kie.kogito.quarkus.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class KogitoEventingRuntimeConfig {

    private static final String MAX_THREADS_PROPERTY = "threads.poolSize";
    private static final String DEFAULT_MAX_THREADS = "10";
    private static final String DEFAULT_QUEUE_SIZE = "1";
    private static final String QUEUE_SIZE_PROPERTY = "threads.queueSize";

    /**
     * Maximum number of threads to handle incoming events by channel
     */
    @ConfigItem(name = MAX_THREADS_PROPERTY, defaultValue = DEFAULT_MAX_THREADS)
    public int maxThreads;

    /**
     * Maximum size of queue to hold incoming events to be processed by threads in the pool
     */
    @ConfigItem(name = QUEUE_SIZE_PROPERTY, defaultValue = DEFAULT_QUEUE_SIZE)
    public int queueSize;
}
