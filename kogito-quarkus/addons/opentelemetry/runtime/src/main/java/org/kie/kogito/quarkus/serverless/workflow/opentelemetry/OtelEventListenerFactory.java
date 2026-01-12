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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry;

import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.quarkus.serverless.workflow.opentelemetry.config.SonataFlowOtelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.properties.IfBuildProperty;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

/**
 * Factory for producing OpenTelemetry event listeners.
 *
 * This factory is responsible for creating and configuring the NodeOtelEventListener
 * as a KogitoProcessEventListener so it gets automatically registered with the process engine.
 * The listener will only be produced when OpenTelemetry is enabled.
 */
@Dependent
public class OtelEventListenerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtelEventListenerFactory.class);

    @Inject
    NodeSpanManager spanManager;

    @Inject
    SonataFlowOtelConfig config;

    @Inject
    HeaderContextExtractor headerExtractor;

    /**
     * Produces the NodeOtelEventListener as a KogitoProcessEventListener.
     * This ensures it gets automatically registered with the Kogito process engine.
     * The listener is only produced when OpenTelemetry is enabled.
     *
     * @return the NodeOtelEventListener for process monitoring
     */
    @Produces
    @IfBuildProperty(name = "sonataflow.otel.enabled", stringValue = "true", enableIfMissing = true)
    public KogitoProcessEventListener produceOtelEventListener() {
        LOGGER.info("Producing NodeOtelEventListener for process monitoring");
        return new NodeOtelEventListener(spanManager, config, headerExtractor);
    }
}
