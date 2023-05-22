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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;

final class DeploymentUtils {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private DeploymentUtils() {
    }

    /**
     * Try to resolve the valid endpoint for the given Deployment, it first try to get attached services, if no service
     * is found then try the PodIP from the given Deployment
     * 
     * @param client
     * @param kubeURI
     * @return the valid endpoint for the given Deployment
     */
    static Optional<URI> queryDeploymentByName(KubernetesClient client, KubernetesResourceUri kubeURI) {
        Deployment deployment = client.apps().deployments()
                .inNamespace(kubeURI.getNamespace())
                .withName(kubeURI.getResourceName())
                .get();

        if (deployment == null) {
            logger.error("Deployment {} not found on the {} namespace.", kubeURI.getResourceName(), kubeURI.getNamespace());
            return Optional.empty();
        }
        logger.debug("Deployment {} found, returning.", kubeURI.getResourceName());
        return ServiceUtils.queryServiceByLabelOrSelector(client,
                deployment.getMetadata().getLabels(),
                deployment.getSpec().getSelector().getMatchLabels(),
                kubeURI).or(() -> {
                    if (deployment.getStatus().getReplicas() == 1) {
                        logger.debug("No service found for selector label {}, 1 replica found, trying to return podIP.",
                                deployment.getSpec().getSelector().getMatchLabels());
                        return client.apps().replicaSets()
                                .inNamespace(kubeURI.getNamespace())
                                .withLabels(deployment.getSpec().getTemplate().getMetadata().getLabels())
                                .list().getItems().stream()
                                .filter(rss -> rss.hasOwnerReferenceFor(deployment.getMetadata().getUid()))
                                .findFirst().flatMap(foundRc -> PodUtils.queryPodByOwnerReference(client,
                                        foundRc.getMetadata().getUid(),
                                        kubeURI.getNamespace(),
                                        kubeURI))
                                .or(Optional::empty);

                    } else if (deployment.getStatus().getReplicas() > 1) {
                        logger.warn("Deployment set has {} replicas but no service was found, KubeURI {} will not be translated.",
                                deployment.getSpec().getReplicas(),
                                kubeURI);
                    }
                    return Optional.empty();
                });
    }
}
