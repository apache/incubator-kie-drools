/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import org.kie.kogito.addons.k8s.resource.catalog.KubernetesServiceCatalog;
import org.kie.kogito.addons.k8s.resource.catalog.KubernetesServiceCatalogProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;

public class Fabric8KubernetesServiceCatalogProvider implements KubernetesServiceCatalogProvider {

    private static final Logger logger = LoggerFactory.getLogger(Fabric8KubernetesServiceCatalogProvider.class);

    @Override
    public KubernetesServiceCatalog create() {
        logger.debug("Configuring k8s client...");

        OpenShiftConfig config = OpenShiftConfig.wrap(new ConfigBuilder().build());
        config.setDisableApiGroupCheck(true); // Quarkus LTS 2.13 compatibility

        @SuppressWarnings("deprecation") // Quarkus LTS 2.13 compatibility
        var kubernetesClient = new DefaultKubernetesClient(config);

        var knativeServiceDiscovery = new KnativeServiceDiscovery(kubernetesClient.adapt(KnativeClient.class));

        var kubernetesResourceDiscovery = new KubernetesResourceDiscovery(kubernetesClient,
                knativeServiceDiscovery);

        var openShiftResourceDiscovery = new OpenShiftResourceDiscovery(kubernetesClient.adapt(OpenShiftClient.class),
                kubernetesResourceDiscovery);

        return new Fabric8KubernetesServiceCatalog(knativeServiceDiscovery, kubernetesResourceDiscovery, openShiftResourceDiscovery);
    }
}
