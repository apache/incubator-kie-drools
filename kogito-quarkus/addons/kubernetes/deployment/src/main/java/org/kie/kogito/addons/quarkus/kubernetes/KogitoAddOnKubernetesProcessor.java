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
package org.kie.kogito.addons.quarkus.kubernetes;

import org.jboss.logmanager.Level;
import org.kie.kogito.addons.quarkus.k8s.EndpointCallerProducer;
import org.kie.kogito.addons.quarkus.k8s.EndpointDiscoveryProducer;
import org.kie.kogito.addons.quarkus.k8s.config.ServiceDiscoveryConfigBuilder;
import org.kie.kogito.quarkus.addons.common.deployment.AnyEngineKogitoAddOnProcessor;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LogCategoryBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigBuilderBuildItem;

class KogitoAddOnKubernetesProcessor extends AnyEngineKogitoAddOnProcessor {

    private static final String FEATURE = "kie-addon-kubernetes-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public AdditionalBeanBuildItem endpointDiscoveryProducer() {
        return new AdditionalBeanBuildItem(EndpointDiscoveryProducer.class);
    }

    @BuildStep
    public AdditionalBeanBuildItem endpointCallerProducer() {
        return new AdditionalBeanBuildItem(EndpointCallerProducer.class);
    }

    @BuildStep
    void runtimeInitConfigBuilderProducer(BuildProducer<RunTimeConfigBuilderBuildItem> rcb) {
        rcb.produce(new RunTimeConfigBuilderBuildItem(ServiceDiscoveryConfigBuilder.class.getName()));
    }

    /**
     * Defaults the logger to warn to no print it at STATIC_INIT time
     * To enable back just set quarkus.log.category."okhttp3.OkHttpClient".level=INFO
     * 
     * @param categories
     */
    @BuildStep
    public void produceLoggingCategories(BuildProducer<LogCategoryBuildItem> categories) {
        categories.produce(new LogCategoryBuildItem("okhttp3.OkHttpClient", Level.WARN));
    }
}
