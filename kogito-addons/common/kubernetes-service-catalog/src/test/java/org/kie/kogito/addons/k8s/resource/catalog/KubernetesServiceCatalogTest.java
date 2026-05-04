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
package org.kie.kogito.addons.k8s.resource.catalog;

import java.net.URI;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.addons.k8s.resource.catalog.KubernetesProtocol.KNATIVE;
import static org.kie.kogito.addons.k8s.resource.catalog.KubernetesProtocol.KUBERNETES;
import static org.kie.kogito.addons.k8s.resource.catalog.KubernetesProtocol.OPENSHIFT;

/**
 * Test classes for implementations of {@link KubernetesServiceCatalog} must extend this class.
 * It tests all use cases an implementation of {@link KubernetesServiceCatalog} must cover.
 */
public abstract class KubernetesServiceCatalogTest {

    private static final String EXPECTED_KNATIVE_URI = "http://serverless-workflow-greeting-quarkus.default.10.99.154.147.sslip.io";

    private static final String EXPECTED_KUBERNETES_URI = "http://serverless-workflow-greeting-quarkus-kubernetes.default.10.99.154.147.sslip.io";

    private static final String EXPECTED_OPENSHIFT_URI = "http://serverless-workflow-greeting-quarkus-openshift.default.10.99.154.147.sslip.io";

    private static final String NAMESPACE = "default";

    private static final String KNATIVE_SERVICENAME = "serverless-workflow-greeting-quarkus";

    private static final String KUBERNETES_SERVICENAME = "serverless-workflow-greeting-quarkus-kubernetes";

    private static final String OPENSHIFT_SERVICENAME = "serverless-workflow-greeting-quarkus-openshift";

    private static final String NAMESPACE_KNATIVE_SERVICENAME = NAMESPACE + '/' + KNATIVE_SERVICENAME;

    private static final String NAMESPACE_KUBERNETES_SERVICENAME = NAMESPACE + '/' + KUBERNETES_SERVICENAME;

    private static final String NAMESPACE_OPENSHIFT_SERVICENAME = NAMESPACE + '/' + OPENSHIFT_SERVICENAME;

    private static final String GVK = "services.v1.serving.knative.dev";

    private static final String GVK_KNATIVE_SERVICENAME = GVK + '/' + KNATIVE_SERVICENAME;

    private static final String GVK_NAMESPACE_SERVICENAME = GVK + '/' + NAMESPACE_KNATIVE_SERVICENAME;

    private static final String GVK_KUBERNETES_SERVICENAME = GVK + '/' + KUBERNETES_SERVICENAME;

    private static final String GVK_NAMESPACE_KUBERNETES_SERVICENAME = GVK + '/' + NAMESPACE_KUBERNETES_SERVICENAME;

    private static final String GVK_OPENSHIFT_SERVICENAME = GVK + '/' + OPENSHIFT_SERVICENAME;

    private static final String GVK_NAMESPACE_OPENSHIFT_SERVICENAME = GVK + '/' + NAMESPACE_OPENSHIFT_SERVICENAME;

    private final KubernetesServiceCatalog kubernetesServiceCatalog;

    protected KubernetesServiceCatalogTest(KubernetesServiceCatalog kubernetesServiceCatalog) {
        this.kubernetesServiceCatalog = kubernetesServiceCatalog;
    }

    protected final String getNamespace() {
        return NAMESPACE;
    }

    protected final String getKnativeServiceName() {
        return KNATIVE_SERVICENAME;
    }

    protected final String getKubernetesServiceName() {
        return KUBERNETES_SERVICENAME;
    }

    protected final String getOpenshiftServicename() {
        return OPENSHIFT_SERVICENAME;
    }

    @ParameterizedTest
    @MethodSource("possibleUriFormats")
    void getServiceAddress(KubernetesProtocol kubernetesProtocol, String coordinates, String expectedUri) {
        KubernetesServiceCatalogKey key = new KubernetesServiceCatalogKey(kubernetesProtocol, coordinates);
        assertThat(kubernetesServiceCatalog.getServiceAddress(key))
                .map(URI::toString)
                .hasValue(expectedUri);
    }

    protected static Stream<Arguments> possibleUriFormats() {
        return Stream.of(
                Arguments.of(KNATIVE, KNATIVE_SERVICENAME, EXPECTED_KNATIVE_URI),
                Arguments.of(KNATIVE, NAMESPACE_KNATIVE_SERVICENAME, EXPECTED_KNATIVE_URI),
                Arguments.of(KNATIVE, GVK_KNATIVE_SERVICENAME, EXPECTED_KNATIVE_URI),
                Arguments.of(KNATIVE, GVK_NAMESPACE_SERVICENAME, EXPECTED_KNATIVE_URI),

                Arguments.of(KUBERNETES, GVK_KUBERNETES_SERVICENAME, EXPECTED_KUBERNETES_URI),
                Arguments.of(KUBERNETES, GVK_NAMESPACE_KUBERNETES_SERVICENAME, EXPECTED_KUBERNETES_URI),

                Arguments.of(OPENSHIFT, GVK_OPENSHIFT_SERVICENAME, EXPECTED_OPENSHIFT_URI),
                Arguments.of(OPENSHIFT, GVK_NAMESPACE_OPENSHIFT_SERVICENAME, EXPECTED_OPENSHIFT_URI));
    }
}