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
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.KubernetesClient;

final class ServiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private ServiceUtils() {
    }

    /**
     * query k8s service by name
     * 
     * @param client
     * @param kubeURI
     * @return Optional.of(URI) or Optional.empty() if the given service doesn't exist
     */
    static Optional<URI> queryServiceByName(KubernetesClient client, KubernetesResourceUri kubeURI) {
        Service service = client.services().inNamespace(kubeURI.getNamespace()).withName(kubeURI.getResourceName()).get();
        if (service == null) {
            logger.error("Service {} not found on the {} namespace.", kubeURI.getResourceName(), kubeURI.getNamespace());
            return Optional.empty();
        }
        return getURLFromService(service, kubeURI);
    }

    /**
     * Try to query services that are attached to the given pod using label selector
     * 
     * @param client
     * @param labels
     * @param selector
     * @param kubeURI
     * @return the found URI from pod or service
     */
    static Optional<URI> queryServiceByLabelOrSelector(KubernetesClient client, Map<String, String> labels,
            Map<String, String> selector, KubernetesResourceUri kubeURI) {

        if (selector != null) {
            logger.info("filtering service by label selector for resource {}", kubeURI.getResourceName());
            Optional<Service> s = client.services()
                    .inNamespace(kubeURI.getNamespace())
                    .list()
                    .getItems()
                    .stream()
                    .filter(svc -> null != svc.getSpec().getSelector())
                    .filter(svc -> svc.getSpec().getSelector().equals(selector))
                    .findFirst().or(() -> {
                        logger.debug("not found any service by selector, trying with labels...");
                        return Optional.empty();
                    });
            if (s.isPresent()) {
                return getURLFromService(s.get(), kubeURI);
            }
        }

        logger.debug("filtering service with label {}", labels);
        ServiceList services = client.services()
                .inNamespace(kubeURI.getNamespace())
                .withLabels(labels)
                .list();

        if (services.getItems().isEmpty()) {
            logger.warn("Resource [{}] does not have any service related with labels {}",
                    kubeURI.getResourceName(),
                    Arrays.asList(labels));
            return Optional.empty();

        } else if (services.getItems().size() > 1) {
            if (kubeURI.getCustomLabel().size() > 0) {
                logger.info("Multiple services for resource [{}], filtering the service using custom labels: {}",
                        kubeURI.getResourceName(),
                        kubeURI.getCustomLabel());

                return services.getItems().stream()
                        .filter(svc -> isServiceLabelled(svc, kubeURI.getCustomLabel()))
                        .findFirst()
                        .flatMap(s -> getURLFromService(s, kubeURI))
                        .or(() -> {
                            logger.warn("Not able to find any service with custom label {}," +
                                    " returning the first in the list.", kubeURI.getCustomLabel());
                            return getURLFromService(services.getItems().get(0), kubeURI);
                        });

            } else {
                logger.warn("Found more than one service for the resource [{}] using the label selector {}, try to be more specific. " +
                        "Returning the first service.",
                        kubeURI.getResourceName(),
                        labels);
            }
        }
        return getURLFromService(services.getItems().get(0), kubeURI);
    }

    /**
     * build the service url based on the available ports' information.
     * custom port name has and https has precedence over web and http.
     * 
     * @param service
     * @param kubeURI
     * @return the service url
     */
    static Optional<URI> getURLFromService(Service service, KubernetesResourceUri kubeURI) {
        switch (service.getSpec().getType()) {
            case KubeConstants.EXTERNAL_NAME_TYPE:
                logger.debug("Using external service name {}", service.getSpec().getExternalName());
                // ExternalName may not work properly with SSL:
                // https://kubernetes.io/docs/concepts/services-networking/service/#externalname
                return URIUtils.builder("http", 80, service.getSpec().getExternalName());

            // NodePort and clusterIP will be mapped in the same way, as we don't need to access the service outside the
            // cluster in this case, we don't need to use the nodePort.
            case KubeConstants.CLUSTER_IP_TYPE:
            case KubeConstants.NODE_PORT_TYPE:
                ServicePort port = PortUtils.findServicePort(service.getSpec().getPorts(), kubeURI);
                String protocol = PortUtils.isServicePortSecure(port) ? KubeConstants.SECURE_HTTP_PROTOCOL : KubeConstants.NONSECURE_HTTP_PROTOCOL;
                return URIUtils.builder(protocol, port.getPort(), service.getSpec().getClusterIP());

            case KubeConstants.LOAD_BALANCER_TYPE:
                logger.warn("{} type is not yet supported", service.getSpec().getType());
                break;

            default:
                logger.debug("{} type is not supported", service.getSpec().getType());
                break;
        }
        return Optional.empty();
    }

    private static boolean isServiceLabelled(Service service, Map<String, String> labels) {
        return service.getMetadata().getLabels().entrySet().stream().anyMatch(label -> labels.containsKey(label.getKey())
                && labels.containsValue(label.getValue()));
    }
}
