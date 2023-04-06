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
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.smallrye.config.ConfigSourceInterceptor;
import io.smallrye.config.ConfigSourceInterceptorContext;
import io.smallrye.config.ConfigValue;

public class KubeDiscoveryConfigSourceInterceptor implements ConfigSourceInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final transient ConfigValueExpander configValueExpander;

    public KubeDiscoveryConfigSourceInterceptor() {
        logger.debug("Configuring k8s client...");

        OpenShiftConfig config = OpenShiftConfig.wrap(new ConfigBuilder().build());
        config.setDisableApiGroupCheck(true); // Quarkus LTS 2.13 compatibility

        var kubernetesClient = new DefaultKubernetesClient(config);

        var knativeServiceDiscovery = new KnativeServiceDiscovery(kubernetesClient.adapt(KnativeClient.class));

        var vanillaKubernetesResourceDiscovery = new VanillaKubernetesResourceDiscovery(kubernetesClient,
                knativeServiceDiscovery);

        var openShiftResourceDiscovery = new OpenShiftResourceDiscovery(kubernetesClient.adapt(OpenShiftClient.class),
                vanillaKubernetesResourceDiscovery);

        var kubeDiscoveryConfigCacheUpdater = new KubeDiscoveryConfigCacheUpdater(vanillaKubernetesResourceDiscovery,
                openShiftResourceDiscovery, knativeServiceDiscovery);

        var kubeDiscoveryConfigCache = new KubeDiscoveryConfigCache(kubeDiscoveryConfigCacheUpdater);

        this.configValueExpander = new ConfigValueExpander(kubeDiscoveryConfigCache);
    }

    @Override
    public ConfigValue getValue(ConfigSourceInterceptorContext context, String s) {
        return configValueExpander.expand(context.proceed(s));
    }
}
