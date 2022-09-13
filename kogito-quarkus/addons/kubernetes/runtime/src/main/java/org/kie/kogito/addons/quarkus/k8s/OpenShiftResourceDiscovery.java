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
package org.kie.kogito.addons.quarkus.k8s;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Optional;

import org.kie.kogito.addons.quarkus.k8s.parser.KubeURI;
import org.kie.kogito.addons.quarkus.k8s.utils.PodUtils;
import org.kie.kogito.addons.quarkus.k8s.utils.ServiceUtils;
import org.kie.kogito.addons.quarkus.k8s.utils.URIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.client.OpenShiftClient;

public class OpenShiftResourceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private OpenShiftClient openShiftClient;

    public OpenShiftResourceDiscovery(final KubernetesClient kubernetesClient) {
        logger.debug("Trying to adapt kubernetes client to OpenShift");
        this.openShiftClient = kubernetesClient.adapt(OpenShiftClient.class);
    }

    public Optional<URI> queryDeploymentConfigByName(KubeURI kubeURI) {
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
                                kubeURI.getUrl());
                    }
                    return Optional.empty();
                });
    }

    public Optional<URI> queryRouteByName(KubeURI kubeURI) {
        Route route = openShiftClient.routes()
                .inNamespace(kubeURI.getNamespace())
                .withName(kubeURI.getResourceName())
                .get();

        if (route == null) {
            logger.error("Route {} not found on the {} namespace.", kubeURI.getResourceName(), kubeURI.getNamespace());
            return Optional.empty();
        }
        String scheme = route.getSpec().getTls() != null ? KubeConstants.SECURE_HTTP_PROTOCOL : KubeConstants.NONSECURE_HTTP_PROTOCOL;
        Integer port = scheme.equals(KubeConstants.SECURE_HTTP_PROTOCOL) ? KubeConstants.SECURE_PORT : KubeConstants.NON_SECURE_PORT;
        return URIUtils.builder(scheme, port, route.getSpec().getHost());
    }
}
