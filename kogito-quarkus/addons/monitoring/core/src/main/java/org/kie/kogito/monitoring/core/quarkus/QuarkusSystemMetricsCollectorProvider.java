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
package org.kie.kogito.monitoring.core.quarkus;

import org.kie.kogito.KogitoGAV;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.monitoring.core.common.system.metrics.SystemMetricsCollector;
import org.kie.kogito.monitoring.core.common.system.metrics.SystemMetricsCollectorProvider;

import io.micrometer.core.instrument.Metrics;
import io.quarkus.runtime.Startup;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@Startup
public class QuarkusSystemMetricsCollectorProvider implements SystemMetricsCollectorProvider {

    @Inject
    ConfigBean configBean;

    SystemMetricsCollector systemMetricsCollector;

    @PostConstruct
    public void init() {
        systemMetricsCollector = new SystemMetricsCollector(configBean.getGav().orElse(KogitoGAV.EMPTY_GAV), Metrics.globalRegistry);
    }

    public SystemMetricsCollector get() {
        return systemMetricsCollector;
    }
}
