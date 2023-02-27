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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeServiceDiscoveryTestUtil.createServiceIfNotExists;

@QuarkusTest
@WithKubernetesTestServer
class KnativeServiceDiscoveryTest {

    private static final String REMOTE_SERVICE_HOST = "serverless-workflow-greeting-quarkus.test.10.99.154.147.sslip.io";

    @KubernetesTestServer
    KubernetesServer mockServer;

    @Inject
    KnativeServiceDiscovery serviceDiscovery;

    static KnativeClient knativeClient;

    @BeforeEach
    void setup() {
        String remoteServiceUrl = "http://" + REMOTE_SERVICE_HOST;

        createServiceIfNotExists(mockServer, remoteServiceUrl, "knative/quarkus-greeting.yaml", "test", "serverless-workflow-greeting-quarkus")
                .ifPresent(newKnativeClient -> knativeClient = newKnativeClient);
    }

    @AfterAll
    static void tearDown() {
        knativeClient.close();
    }

    @Test
    void discoverSpecificNamespace() {
        Optional<KnativeServiceAddress> serviceAddress = serviceDiscovery.discover("test/serverless-workflow-greeting-quarkus");

        assertThat(serviceAddress).map(KnativeServiceAddress::getHost)
                .hasValue(REMOTE_SERVICE_HOST);

        assertThat(serviceAddress).map(KnativeServiceAddress::getPort)
                .hasValue(80);

        assertThat(serviceAddress).map(KnativeServiceAddress::isSsl)
                .hasValue(false);
    }

    @Test
    void discoverCurrentNamespace() {
        Optional<KnativeServiceAddress> serviceAddress = serviceDiscovery.discover("serverless-workflow-greeting-quarkus");

        assertThat(serviceAddress).map(KnativeServiceAddress::getHost)
                .hasValue(REMOTE_SERVICE_HOST);

        assertThat(serviceAddress).map(KnativeServiceAddress::getPort)
                .hasValue(80);

        assertThat(serviceAddress).map(KnativeServiceAddress::isSsl)
                .hasValue(false);
    }

    @Test
    void serviceInDifferentNamespaceShouldNotBeFound() {
        Optional<KnativeServiceAddress> serviceAddress = serviceDiscovery.discover("different_namespace/serverless-workflow-greeting-quarkus");
        assertThat(serviceAddress).isEmpty();
    }

    @Test
    void https() {
        String remoteServiceUrl = "https://" + REMOTE_SERVICE_HOST;
        createServiceIfNotExists(mockServer, remoteServiceUrl, "knative/quarkus-greeting-https.yaml", "test", "serverless-workflow-greeting-quarkus-https")
                .ifPresent(newKnativeClient -> knativeClient = newKnativeClient);

        Optional<KnativeServiceAddress> serviceAddress = serviceDiscovery.discover("serverless-workflow-greeting-quarkus-https");

        assertThat(serviceAddress).map(KnativeServiceAddress::getHost)
                .hasValue(REMOTE_SERVICE_HOST);

        assertThat(serviceAddress).map(KnativeServiceAddress::getPort)
                .hasValue(443);

        assertThat(serviceAddress).map(KnativeServiceAddress::isSsl)
                .hasValue(true);
    }
}
