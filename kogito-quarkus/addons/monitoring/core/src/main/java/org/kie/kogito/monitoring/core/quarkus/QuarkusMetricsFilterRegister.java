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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.monitoring.core.common.Constants;
import org.kie.kogito.monitoring.core.common.system.interceptor.MetricsInterceptor;
import org.kie.kogito.monitoring.core.common.system.metrics.SystemMetricsCollector;

import io.micrometer.core.instrument.Metrics;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class QuarkusMetricsFilterRegister implements DynamicFeature {

    // Indirect Instance<Boolean> to solve warning message during compilation:
    // WARNING Directly injecting a @ConfigProperty into a JAX-RS provider may lead to unexpected results.
    // To ensure proper results, please change the type of the field to jakarta.enterprise.inject.Instance<Boolean>.
    @ConfigProperty(name = Constants.HTTP_INTERCEPTOR_USE_DEFAULT, defaultValue = "true")
    Instance<Boolean> httpInterceptorUseDefault;

    ConfigBean configBean;

    public QuarkusMetricsFilterRegister() {
        // See https://github.com/quarkusio/quarkus/issues/12780
    }

    @Inject
    public QuarkusMetricsFilterRegister(final ConfigBean configBean) {
        this.configBean = configBean;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (httpInterceptorUseDefault.isResolvable() && httpInterceptorUseDefault.get()) {
            SystemMetricsCollector systemMetricsCollector =
                    new SystemMetricsCollector(configBean.getGav().orElse(KogitoGAV.EMPTY_GAV), Metrics.globalRegistry);
            MetricsInterceptor metricsInterceptor = new MetricsInterceptor(systemMetricsCollector);
            context.register(new QuarkusMetricsInterceptor(metricsInterceptor));
        }
    }

    // for testing purpose
    void setHttpInterceptorUseDefault(Instance<Boolean> httpInterceptorUseDefault) {
        this.httpInterceptorUseDefault = httpInterceptorUseDefault;
    }
}
