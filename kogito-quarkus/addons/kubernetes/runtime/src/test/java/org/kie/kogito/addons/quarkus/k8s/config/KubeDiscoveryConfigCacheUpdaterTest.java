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
package org.kie.kogito.addons.quarkus.k8s.config;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.addons.k8s.resource.catalog.KubernetesServiceCatalog;

import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.addons.quarkus.k8s.test.utils.KnativeResourceDiscoveryTestUtil.createServiceIfNotExists;

@QuarkusTest
@WithKubernetesTestServer
class KubeDiscoveryConfigCacheUpdaterTest {

    @KubernetesTestServer
    static KubernetesServer mockServer;

    KubeDiscoveryConfigCacheUpdater kubeDiscoveryConfigCacheUpdater;

    final String remoteServiceUrl = "http://serverless-workflow-greeting-quarkus.test.10.99.154.147.sslip.io";

    @Inject
    KubernetesServiceCatalog kubernetesServiceCatalog;

    @BeforeEach
    void beforeEach() {
        createServiceIfNotExists(mockServer, "knative/quarkus-greeting.yaml", "test", "serverless-workflow-greeting-quarkus", remoteServiceUrl);
        kubeDiscoveryConfigCacheUpdater = new KubeDiscoveryConfigCacheUpdater(kubernetesServiceCatalog);
    }

    @Test
    void knativeService() {
        assertThat(kubeDiscoveryConfigCacheUpdater.update("knative:test/serverless-workflow-greeting-quarkus"))
                .hasValue(URI.create(remoteServiceUrl));
    }

    @Test
    void knativeResource() {
        assertThat(kubeDiscoveryConfigCacheUpdater.update("knative:services.v1.serving.knative.dev/test/serverless-workflow-greeting-quarkus"))
                .hasValue(URI.create(remoteServiceUrl));
    }
}
