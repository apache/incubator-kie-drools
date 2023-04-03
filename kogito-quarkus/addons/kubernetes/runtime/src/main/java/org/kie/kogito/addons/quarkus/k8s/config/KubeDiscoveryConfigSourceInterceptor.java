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
package org.kie.kogito.addons.quarkus.k8s.config;

import java.lang.invoke.MethodHandles;

import org.kie.kogito.addons.quarkus.k8s.discovery.KnativeServiceDiscovery;
import org.kie.kogito.addons.quarkus.k8s.discovery.OpenShiftResourceDiscovery;
import org.kie.kogito.addons.quarkus.k8s.discovery.VanillaKubernetesResourceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.smallrye.config.ConfigSourceInterceptor;
import io.smallrye.config.ConfigSourceInterceptorContext;
import io.smallrye.config.ConfigValue;

public class KubeDiscoveryConfigSourceInterceptor implements ConfigSourceInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final transient KubeDiscoveryConfigCache kubeDiscoveryConfigCache;

    public KubeDiscoveryConfigSourceInterceptor() {
        logger.debug("Configuring k8s client...");

        var kubernetesClient = new DefaultKubernetesClient();

        var knativeServiceDiscovery = new KnativeServiceDiscovery(kubernetesClient.adapt(KnativeClient.class));

        var vanillaKubernetesResourceDiscovery = new VanillaKubernetesResourceDiscovery(kubernetesClient,
                knativeServiceDiscovery);

        var openShiftResourceDiscovery = new OpenShiftResourceDiscovery(kubernetesClient.adapt(OpenShiftClient.class),
                vanillaKubernetesResourceDiscovery);

        var kubeDiscoveryConfigCacheUpdater = new KubeDiscoveryConfigCacheUpdater(vanillaKubernetesResourceDiscovery,
                openShiftResourceDiscovery, knativeServiceDiscovery);

        this.kubeDiscoveryConfigCache = new KubeDiscoveryConfigCache(kubeDiscoveryConfigCacheUpdater);
    }

    @Override
    public ConfigValue getValue(ConfigSourceInterceptorContext context, String s) {
        ConfigValue configValue = context.proceed(s);
        if (configValue == null) {
            return null;
        }
        return kubeDiscoveryConfigCache.get(configValue.getName(), configValue.getValue())
                .map(configValue::withValue)
                .orElse(configValue);
    }
}
