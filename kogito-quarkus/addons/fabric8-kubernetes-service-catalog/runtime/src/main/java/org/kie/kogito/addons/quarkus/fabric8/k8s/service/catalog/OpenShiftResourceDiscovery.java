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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.client.OpenShiftClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class OpenShiftResourceDiscovery extends AbstractResourceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final OpenShiftClient openShiftClient;

    private final KubernetesResourceDiscovery kubernetesResourceDiscovery;

    @Inject
    OpenShiftResourceDiscovery(OpenShiftClient openShiftClient, KubernetesResourceDiscovery kubernetesResourceDiscovery) {
        this.openShiftClient = openShiftClient;
        this.kubernetesResourceDiscovery = kubernetesResourceDiscovery;
    }

    Optional<URI> query(KubernetesResourceUri resourceUri) {
        resourceUri = resolveNamespace(resourceUri, openShiftClient::getNamespace);

        switch (resourceUri.getGvk()) {
            case DEPLOYMENT_CONFIG:
                return queryDeploymentConfigByName(resourceUri);

            case ROUTE:
                return queryRouteByName(resourceUri);

            default:
                return kubernetesResourceDiscovery.query(resourceUri);
        }
    }

    private KubernetesResourceUri resolveNamespace(KubernetesResourceUri uri, Supplier<String> defaultNamespaceSupplier) {
        if (uri.getNamespace() == null) {
            String defaultNamespace = defaultNamespaceSupplier.get();

            logDefaultNamespace(defaultNamespace);

            uri = uri.copyBuilder()
                    .withNamespace(defaultNamespace)
                    .build();
        }
        return uri;
    }

    private Optional<URI> queryDeploymentConfigByName(KubernetesResourceUri kubeURI) {
        logConnection(openShiftClient, kubeURI.getResourceName());

        DeploymentConfig deploymentConfig = openShiftClient.deploymentConfigs()
                .inNamespace(kubeURI.getNamespace())
                .withName(kubeURI.getResourceName())
                .get();

        if (null == deploymentConfig) {
            logger.error("Openshift DeploymentConfig {} not found on the {} namespace.", kubeURI.getResourceName(), kubeURI.getNamespace());
            return Optional.empty();
        }
        logger.debug("Service for DeploymentConfig {} found, returning.", kubeURI.getResourceName());
        return ServiceUtils.queryServiceByLabelOrSelector(openShiftClient,
                deploymentConfig.getMetadata().getLabels(),
                deploymentConfig.getSpec().getSelector(),
                kubeURI).or(() -> {
                    if (deploymentConfig.getStatus().getReplicas() == 1) {
                        logger.debug("No service found for selector label {}, 1 replica found, trying to return podIP.",
                                deploymentConfig.getSpec().getSelector());
                        return openShiftClient.replicationControllers()
                                .inNamespace(kubeURI.getNamespace())
                                .withLabels(deploymentConfig.getSpec().getTemplate().getMetadata().getLabels())
                                .list().getItems().stream()
                                .filter(rss -> rss.hasOwnerReferenceFor(deploymentConfig.getMetadata().getUid()))
                                // if found map the use the rc to query the pod, if nothing is found return an empty uri
                                .findFirst().flatMap(foundRc -> PodUtils.queryPodByOwnerReference(openShiftClient,
                                        foundRc.getMetadata().getUid(),
                                        kubeURI.getNamespace(),
                                        kubeURI))
                                .or(Optional::empty);

                    } else if (deploymentConfig.getStatus().getReplicas() > 1) {
                        logger.error("DeploymentConfig has {} replicas but not service was found, KubeURI {} will not be translated",
                                deploymentConfig.getSpec().getReplicas(),
                                kubeURI);
                    }
                    return Optional.empty();
                });
    }

    private Optional<URI> queryRouteByName(KubernetesResourceUri kubeURI) {
        logConnection(openShiftClient, kubeURI.getResourceName());

        Route route = openShiftClient.routes()
                .inNamespace(kubeURI.getNamespace())
                .withName(kubeURI.getResourceName())
                .get();

        if (route == null) {
            logger.error("Route {} not found on the {} namespace.", kubeURI.getResourceName(), kubeURI.getNamespace());
            return Optional.empty();
        }
        String scheme = route.getSpec().getTls() != null ? KubeConstants.SECURE_HTTP_PROTOCOL : KubeConstants.NONSECURE_HTTP_PROTOCOL;
        int port = scheme.equals(KubeConstants.SECURE_HTTP_PROTOCOL) ? KubeConstants.SECURE_PORT : KubeConstants.NON_SECURE_PORT;
        return URIUtils.builder(scheme, port, route.getSpec().getHost());
    }
}
