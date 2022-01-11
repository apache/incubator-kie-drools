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
package org.kie.kogito.monitoring.elastic.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.elastic.ElasticConfig;
import io.micrometer.elastic.ElasticMeterRegistry;

public class ElasticRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ElasticRegistry.class);
    private ElasticMeterRegistry registry;
    protected CompositeMeterRegistry compositeMeterRegistry = Metrics.globalRegistry;

    protected ElasticRegistry() {
    }

    protected void start(ElasticConfig elasticConfig) {
        start(elasticConfig, Executors.defaultThreadFactory());
    }

    protected void start(ElasticConfig elasticConfig, ThreadFactory threadFactory) {
        registry = ElasticMeterRegistry.builder(elasticConfig).build();
        compositeMeterRegistry.add(registry);
        registry.start(threadFactory);
        logger.debug("Micrometer Elastic publisher started.");
    }
}
