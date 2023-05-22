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
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ServicePort;

final class PortUtils {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private PortUtils() {
    }

    /**
     * Return for the best fit for ContainerPort.
     *
     * @param containerPorts {@link List<ContainerPort>}
     * @return port filtered {@link ContainerPort}
     */
    static ContainerPort findContainerPort(List<ContainerPort> containerPorts, KubernetesResourceUri kubeURI) {
        Optional<ContainerPort> containerPort = containerPortHasCustomPort(containerPorts, kubeURI)
                .or(() -> containerPortHasSSLPort(containerPorts))
                .or(() -> containerPortHasDefaultNamedPort(containerPorts));

        if (containerPort.isPresent()) {
            return containerPort.get();
        } else {
            logger.warn("Didn't find Container ports named as {}, {} or {}, returning the first in the list [{}]. For a more precise query set the {} KubeURI query parameter",
                    KubeConstants.NONSECURE_HTTP_PROTOCOL,
                    KubeConstants.SECURE_HTTP_PROTOCOL,
                    KubeConstants.WEB_PORT_NAME,
                    containerPorts.get(0).getName(),
                    KubeConstants.CUSTOM_PORT_NAME_PROPERTY);
            return containerPorts.get(0);
        }
    }

    /**
     * Return for the best fit ServicePort
     *
     * @param servicePorts {@link List<ServicePort>}
     * @return port filtered {@link ServicePort}
     */
    static ServicePort findServicePort(List<ServicePort> servicePorts, KubernetesResourceUri kubeURI) {
        Optional<ServicePort> servicePort = servicePortHasCustomPort(servicePorts, kubeURI)
                .or(() -> servicePortHasSSLPort(servicePorts))
                .or(() -> servicePortHasDefaultNamedPort(servicePorts));

        if (servicePort.isPresent()) {
            return servicePort.get();
        } else {
            logger.warn("Didn't find Service ports named as {}, {} or {}, returning the first in the list [{}]. For a more precise query set the {} KubeURI query parameter",
                    KubeConstants.NONSECURE_HTTP_PROTOCOL,
                    KubeConstants.SECURE_HTTP_PROTOCOL,
                    KubeConstants.WEB_PORT_NAME,
                    servicePorts.get(0).getName(),
                    KubeConstants.CUSTOM_PORT_NAME_PROPERTY);
            return servicePorts.get(0);
        }
    }

    static boolean isServicePortSecure(ServicePort servicePort) {
        return servicePort.getPort() == KubeConstants.SECURE_PORT || servicePort.getPort() == KubeConstants.APP_SECURE_PORT;
    }

    private static boolean isServiceTargetPortPresent(ServicePort servicePort) {
        return servicePort.getTargetPort() != null && servicePort.getTargetPort().getIntVal() != null;
    }

    /**
     * Search if there is the KubeURI custom port parameter set, if so, returns the named ContainerPort.
     * 
     * @param containerPorts {@link List<ContainerPort>}
     * @param kubeURI {@link KubernetesResourceUri}
     * @return port {@link Optional<ContainerPort>}, if no port is found return Optional.empty()
     */
    private static Optional<ContainerPort> containerPortHasCustomPort(List<ContainerPort> containerPorts, KubernetesResourceUri kubeURI) {
        if (kubeURI.getCustomPortName() != null) {
            Optional<ContainerPort> sp = containerPorts.stream().filter(p -> p.getName() != null && p.getName().equals(kubeURI.getCustomPortName())).findFirst();
            if (sp.isPresent()) {
                logger.debug("Found custom container port named as {}, returning...", kubeURI.getCustomPortName());
                return sp;
            } else {
                logger.warn("Custom container port is set to {}, but no port with such name was found. Trying the next available container port",
                        kubeURI.getCustomPortName());
            }
        }
        return Optional.empty();
    }

    /**
     * Search if there is the KubeURI custom port parameter set, if so, returns the name Service Port.
     *
     * @param servicePorts {@link List<ServicePort>}
     * @param kubeURI {@link KubernetesResourceUri}
     * @return port {@link Optional<ServicePort>}, if no port is found return Optional.empty()
     */
    private static Optional<ServicePort> servicePortHasCustomPort(List<ServicePort> servicePorts, KubernetesResourceUri kubeURI) {
        if (kubeURI.getCustomPortName() != null) {
            Optional<ServicePort> sp = servicePorts.stream().filter(p -> p.getName() != null && p.getName().equals(kubeURI.getCustomPortName())).findFirst();
            if (sp.isPresent()) {
                logger.debug("Found custom service port named as {}, returning...", kubeURI.getCustomPortName());
                return sp;
            } else {
                logger.warn("Custom service port is set to {}, but no port with such name was found. Trying the next available port",
                        kubeURI.getCustomPortName());
            }
        }
        return Optional.empty();
    }

    /**
     * Search for ContainerPort that has the port name as https, no further checks are made at this level.
     * 
     * @param containerPorts {@link List<ContainerPort>}
     * @return port {@link Optional<ContainerPort>}, if no port is found return Optional.empty()
     */
    private static Optional<ContainerPort> containerPortHasSSLPort(List<ContainerPort> containerPorts) {
        Optional<ContainerPort> secp = containerPorts.stream().filter(p -> p.getName() != null && p.getName().equals(KubeConstants.SECURE_HTTP_PROTOCOL)).findFirst();
        secp.ifPresent(v -> logger.debug("ContainerPort {} is secured, please make sure to properly configure the client and server certificates...", v.getName()));
        return secp;
    }

    /**
     * Search for ServicePort that has the port name as https, no further checks are made at this level.
     *
     * @param servicePorts {@link List<ServicePort>}
     * @return port {@link Optional<ServicePort>}, if no port is found return Optional.empty()
     */
    private static Optional<ServicePort> servicePortHasSSLPort(List<ServicePort> servicePorts) {
        Optional<ServicePort> secp = servicePorts.stream().filter(p -> p.getName() != null && p.getName().equals(KubeConstants.SECURE_HTTP_PROTOCOL)).findFirst();
        secp.ifPresent(v -> logger.debug("Port {} is secured, using ssl, please make sure to properly configure the certificates at client and server...", secp.get().getName()));
        return secp;
    }

    /**
     * Search for the default/most common port names, if not found, verifies if there is unnamed port with valid port number set.
     *
     * @param containerPorts {@link List<ContainerPort>}
     * @return port {@link Optional<ContainerPort>}, if no port is found return Optional.empty()
     */
    private static Optional<ContainerPort> containerPortHasDefaultNamedPort(List<ContainerPort> containerPorts) {
        for (ContainerPort port : containerPorts) {
            if (port.getName() != null) {
                if (port.getName().equals(KubeConstants.NONSECURE_HTTP_PROTOCOL) || port.getName().equals(KubeConstants.WEB_PORT_NAME)) {
                    logger.debug("Found port named as {}, returning...", port.getName());
                    return Optional.of(port);
                }
            } else if (port.getName() == null && port.getContainerPort() != null) {
                logger.warn("target service port name is not set but port and Int targetPort is preset, returning...");
                return Optional.of(port);
            }
        }
        return Optional.empty();
    }

    /**
     * Search for the default/most common port names, if not found, verifies if there is unnamed port with valid port number set.
     *
     * @param servicePorts {@link List<ServicePort>}
     * @return port {@link Optional<ServicePort>}, if no port is found return Optional.empty()
     */
    private static Optional<ServicePort> servicePortHasDefaultNamedPort(List<ServicePort> servicePorts) {
        for (ServicePort port : servicePorts) {
            if (port.getName() != null) {
                if (port.getName() != null && port.getName().equals(KubeConstants.NONSECURE_HTTP_PROTOCOL) || port.getName().equals(KubeConstants.WEB_PORT_NAME)) {
                    logger.debug("Found port named as {}, returning...", port.getName());
                    return Optional.of(port);
                }
            } else if (port.getName() == null && isServiceTargetPortPresent(port)) {
                logger.warn("target service port name is not set but port and Int targetPort is set, returning...");
                return Optional.of(port);
            }
        }
        return Optional.empty();
    }
}
