/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.monitoring.prometheus.common.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.monitoring.core.common.MonitoringRegistry;
import org.kie.kogito.monitoring.core.common.system.metrics.SystemMetricsCollector;
import org.kie.kogito.monitoring.prometheus.common.PrometheusRegistryProvider;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class PrometheusRegistryProviderTest {

    @Test
    public void prometheusMetricsAreExported() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        MonitoringRegistry.addRegistry(registry);
        PrometheusRegistryProvider.setPrometheusMeterRegistry(registry);
        SystemMetricsCollector systemMetricsCollector = new SystemMetricsCollector(KogitoGAV.EMPTY_GAV, MonitoringRegistry.getDefaultMeterRegistry());
        systemMetricsCollector.registerElapsedTimeSampleMetrics("endpoint", 1);
        Assertions.assertTrue(PrometheusRegistryProvider.getPrometheusMeterRegistry().scrape().contains("api_execution_elapsed_seconds"));
    }
}
