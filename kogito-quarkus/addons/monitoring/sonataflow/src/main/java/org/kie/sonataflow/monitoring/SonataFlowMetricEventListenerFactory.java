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
package org.kie.sonataflow.monitoring;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.serverless.workflow.monitoring.SonataFlowMetricProcessEventListener;
import org.kie.kogito.serverless.workflow.monitoring.SonataFlowMetricProcessEventListener.ArrayStoreMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Metrics;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class SonataFlowMetricEventListenerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonataFlowMetricEventListenerFactory.class);

    @Inject
    ConfigBean configBean;

    @ConfigProperty(name = "kie.monitoring.sonataflow.arrays.store", defaultValue = "JSON_STRING")
    ArrayStoreMode arrayStoreMode;

    @Produces
    public KogitoProcessEventListener produceProcessListener() {
        LOGGER.info("Producing sonataflow listener for process monitoring.");
        return new SonataFlowMetricProcessEventListener(
                configBean.getGav().orElse(KogitoGAV.EMPTY_GAV), Metrics.globalRegistry, arrayStoreMode);
    }
}
