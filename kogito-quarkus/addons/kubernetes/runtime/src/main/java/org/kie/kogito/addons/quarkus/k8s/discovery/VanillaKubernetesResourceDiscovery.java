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
package org.kie.kogito.addons.quarkus.k8s.discovery;

import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.addons.quarkus.k8s.discovery.utils.DeploymentUtils;
import org.kie.kogito.addons.quarkus.k8s.discovery.utils.IngressUtils;
import org.kie.kogito.addons.quarkus.k8s.discovery.utils.PodUtils;
import org.kie.kogito.addons.quarkus.k8s.discovery.utils.ServiceUtils;
import org.kie.kogito.addons.quarkus.k8s.discovery.utils.StatefulSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;

@ApplicationScoped
public class VanillaKubernetesResourceDiscovery extends AbstractResourceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(VanillaKubernetesResourceDiscovery.class.getName());

    private final KubernetesClient kubernetesClient;

    private final KnativeServiceDiscovery knativeServiceDiscovery;

    @Inject
    public VanillaKubernetesResourceDiscovery(KubernetesClient kubernetesClient, KnativeServiceDiscovery knativeServiceDiscovery) {
        this.kubernetesClient = kubernetesClient;
        this.knativeServiceDiscovery = knativeServiceDiscovery;
    }

    public Optional<URI> query(VanillaKubernetesResourceUri resourceUri) {
        logConnection(kubernetesClient, resourceUri.getResourceName());

        resourceUri = resolveNamespace(resourceUri, kubernetesClient::getNamespace);

        switch (resourceUri.getGvk()) {
            case SERVICE:
                return ServiceUtils.queryServiceByName(kubernetesClient, resourceUri);

            case KNATIVE_SERVICE:
                return knativeServiceDiscovery.query(new KnativeServiceUri(resourceUri.getNamespace(), resourceUri.getResourceName()));

            case POD:
                return PodUtils.queryPodByName(kubernetesClient, resourceUri);

            case DEPLOYMENT:
                return DeploymentUtils.queryDeploymentByName(kubernetesClient, resourceUri);

            case STATEFUL_SET:
                return StatefulSetUtils.queryStatefulSetByName(kubernetesClient, resourceUri);

            case INGRESS:
                return IngressUtils.queryIngressByName(kubernetesClient, resourceUri);

            default:
                logger.debug("Resource kind {} is not supported yet.", resourceUri.getGvk().getValue());
                return Optional.empty();
        }
    }

    private VanillaKubernetesResourceUri resolveNamespace(VanillaKubernetesResourceUri uri, Supplier<String> defaultNamespaceSupplier) {
        if (uri.getNamespace() == null) {
            String defaultNamespace = defaultNamespaceSupplier.get();

            logDefaultNamespace(defaultNamespace);

            uri = uri.copyBuilder()
                    .withNamespace(defaultNamespace)
                    .build();
        }
        return uri;
    }
}
