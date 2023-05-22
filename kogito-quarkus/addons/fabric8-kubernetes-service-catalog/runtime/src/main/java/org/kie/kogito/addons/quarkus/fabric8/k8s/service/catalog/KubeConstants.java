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

final class KubeConstants {

    private KubeConstants() {
    }

    // k8s service types
    static final String EXTERNAL_NAME_TYPE = "ExternalName";
    static final String NODE_PORT_TYPE = "NodePort";
    static final String CLUSTER_IP_TYPE = "ClusterIP";
    static final String LOAD_BALANCER_TYPE = "LoadBalancer";

    // Networking
    static final String SECURE_HTTP_PROTOCOL = "https";
    static final String NONSECURE_HTTP_PROTOCOL = "http";
    static final String WEB_PORT_NAME = "web";

    static final int NON_SECURE_PORT = 80;
    static final int SECURE_PORT = 443;
    static final int APP_SECURE_PORT = 8443;

    // Custom Query params
    static final String CUSTOM_PORT_NAME_PROPERTY = "port-name";
    static final String CUSTOM_RESOURCE_LABEL_PROPERTY = "labels";
}
