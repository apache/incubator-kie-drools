/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.quarkus.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigGroup
public interface KogitoEventingRuntimeConfig {

    String MAX_THREADS_PROPERTY = "threads.poolSize";
    String DEFAULT_MAX_THREADS = "10";
    String DEFAULT_QUEUE_SIZE = "1";
    String QUEUE_SIZE_PROPERTY = "threads.queueSize";

    /**
     * Maximum number of threads to handle incoming events by channel
     */
    @WithName(MAX_THREADS_PROPERTY)
    @WithDefault(DEFAULT_MAX_THREADS)
    int maxThreads();

    /**
     * Maximum size of queue to hold incoming events to be processed by threads in the pool
     */
    @WithName(QUEUE_SIZE_PROPERTY)
    @WithDefault(DEFAULT_QUEUE_SIZE)
    int queueSize();
}
