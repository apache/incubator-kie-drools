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

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;

final class PodUtils {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private PodUtils() {
    }

    static Optional<URI> queryPodByName(KubernetesClient client, KubernetesResourceUri kubeURI) {
        Pod pod = client.pods().inNamespace(kubeURI.getNamespace())
                .withName(kubeURI.getResourceName())
                .get();
        if (pod == null) {
            logger.error("Pod {} not found on the {} namespace.", kubeURI.getResourceName(), kubeURI.getNamespace());
            return Optional.empty();
        }

        return ServiceUtils.queryServiceByLabelOrSelector(
                client,
                pod.getMetadata().getLabels(),
                null,
                kubeURI)
                .or(() -> {
                    if (pod.getStatus().getPodIP() != null && !pod.getStatus().getPodIP().isBlank()) {
                        logger.debug("Returning podIP from pod {}", pod.getMetadata().getName());
                        ContainerPort cPort = PortUtils.findContainerPort(pod.getSpec().getContainers().get(0).getPorts(), kubeURI);
                        return URIUtils.builder(KubeConstants.NONSECURE_HTTP_PROTOCOL,
                                cPort.getContainerPort(),
                                pod.getStatus().getPodIP());
                    } else {
                        logger.warn("Didn't find any service for pod {} and pod is not accessible.", pod.getMetadata().getName());
                        return Optional.empty();
                    }
                });
    }

    static Optional<URI> queryPodByOwnerReference(KubernetesClient client, String uid, String namespace, KubernetesResourceUri kubeURI) {

        Optional<Pod> foundPod = client.pods()
                .inNamespace(namespace)
                .list()
                .getItems()
                .stream()
                // OpenShift pods' have the deployer pod suffixed with -deploy
                .filter(pod -> pod.hasOwnerReferenceFor(uid) && !pod.getMetadata().getName().endsWith("deploy"))
                .findFirst().or(() -> {
                    logger.warn("not found any pod with owner uid {}", uid);
                    return Optional.empty();
                });

        return foundPod.flatMap(pod -> {
            ContainerPort cPort = PortUtils.findContainerPort(pod.getSpec().getContainers().get(0).getPorts(), kubeURI);
            return URIUtils.builder(KubeConstants.NONSECURE_HTTP_PROTOCOL,
                    cPort.getContainerPort(),
                    foundPod.get().getStatus().getPodIP());
        });
    }
}
