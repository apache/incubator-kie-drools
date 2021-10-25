/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.k8s;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;

public class EndpointBuilder {

    public static final String SECURE_HTTP_PROTOCOL = "https";
    public static final String NONSECURE_HTTP_PROTOCOL = "http";
    public static final String PRIMARY_PORT_NAME = "primary-port-name";
    private static final int SECURE_PORT = 443;
    private static final int APP_SECURE_PORT = 8443;

    /**
     * Protocol, Host, Port
     */
    private static final String FORMAT_SVC_URL = "%s://%s:%s";
    private static final String CLUSTER_TYPE_NONE = "None";

    /**
     * Build the {@link Endpoint} for the given Kubernetes {@link Service}.
     * The precedence to choose the primary URL follows this order:
     * <ol>
     * <li>The port with the same name as the value of the label "primary-port-name" in the service</li>
     * <li>The port with name "https"</li>
     * <li>The port with name "http"</li>
     * <li>If none of these conditions are met, the first port in the list will be the defined as primary</li>
     * </ol>
     * These conditions were borrowed from <a href="https://docs.spring.io/spring-cloud-kubernetes/docs/current/reference/html/#discoveryclient-for-kubernetes">Spring Boot Kubernetes Client -
     * Discovery Service</a> document.
     */
    public Endpoint buildFrom(final Service service) {
        if (service == null || isClusterIPEmpty(service.getSpec()) || service.getSpec().getPorts().isEmpty()) {
            return null;
        }
        final Endpoint endpoint = new Endpoint();
        endpoint.setLabels(service.getMetadata().getLabels());

        // look for the label primary-port-name
        String primaryPort = null;
        if (service.getMetadata().getLabels() != null) {
            primaryPort = service.getMetadata().getLabels().get(PRIMARY_PORT_NAME);
        }

        String url = null;
        for (ServicePort port : service.getSpec().getPorts()) {
            if (primaryPort != null && !primaryPort.isEmpty() && primaryPort.equals(port.getName())) {
                endpoint.setUrl(urlForServiceAndPort(port, service, httpProtocolForPort(port.getPort())));
                endpoint.setName(port.getName());
                continue;
            }
            if (SECURE_HTTP_PROTOCOL.equals(port.getName())) {
                url = urlForServiceAndPort(port, service, SECURE_HTTP_PROTOCOL);
                if (NONSECURE_HTTP_PROTOCOL.equals(endpoint.getName())) {
                    endpoint.setUrl(url);
                    endpoint.setName(port.getName());
                } else {
                    endpoint.setUrlIfEmpty(port.getName(), url);
                }
            }
            if (NONSECURE_HTTP_PROTOCOL.equals(port.getName())) {
                url = urlForServiceAndPort(port, service, httpProtocolForPort(port.getPort()));
                endpoint.setUrlIfEmpty(port.getName(), url);
            }
            if (url == null) {
                url = urlForServiceAndPort(port, service, httpProtocolForPort(port.getPort()));
            }
            if (port.getName() != null && !port.getName().isEmpty()) {
                endpoint.addSecondaryUrl(port.getName(), url);
            }
            url = null;
        }

        // fallback to the first one
        if (endpoint.urlIsEmpty()) {
            endpoint.setUrl(urlForServiceAndPort(service.getSpec().getPorts().get(0), service, NONSECURE_HTTP_PROTOCOL));
            endpoint.removeSecondaryUrl(service.getSpec().getPorts().get(0).getName());
            return endpoint;
        }

        this.removeDuplicateUrls(endpoint);
        return endpoint;
    }

    private void removeDuplicateUrls(final Endpoint endpoint) {
        if (endpoint.getUrl().equals(endpoint.getSecondaryUrl(NONSECURE_HTTP_PROTOCOL))) {
            endpoint.removeSecondaryUrl(NONSECURE_HTTP_PROTOCOL);
        }
        if (endpoint.getUrl().equals(endpoint.getSecondaryUrl(SECURE_HTTP_PROTOCOL))) {
            endpoint.removeSecondaryUrl(SECURE_HTTP_PROTOCOL);
        }
    }

    private String urlForServiceAndPort(final ServicePort port, final Service service, final String protocol) {
        return String.format(FORMAT_SVC_URL, protocol,
                service.getSpec().getClusterIP(),
                port.getPort());
    }

    private String httpProtocolForPort(final int port) {
        if (SECURE_PORT == port || APP_SECURE_PORT == port) {
            return SECURE_HTTP_PROTOCOL;
        }
        return NONSECURE_HTTP_PROTOCOL;
    }

    /**
     * Verifies if ClusterIP is valid.
     *
     * @see <a href="https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.22/#servicespec-v1-core">Service Spec ClusterIP</a>
     */
    private boolean isClusterIPEmpty(final ServiceSpec serviceSpec) {
        return serviceSpec.getClusterIP() == null ||
                serviceSpec.getClusterIP().isEmpty() ||
                CLUSTER_TYPE_NONE.equals(serviceSpec.getClusterIP());
    }
}
