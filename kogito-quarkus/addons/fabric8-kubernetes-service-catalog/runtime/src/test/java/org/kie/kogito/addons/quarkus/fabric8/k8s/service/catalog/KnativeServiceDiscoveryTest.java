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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.kogito.addons.quarkus.k8s.test.utils.KubeTestUtils;
import org.kie.kogito.addons.quarkus.k8s.test.utils.KubernetesMockServerTestResource;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(KubernetesMockServerTestResource.class)
class KnativeServiceDiscoveryTest {

    private static final String REMOTE_SERVICE_HOST = "serverless-workflow-greeting-quarkus.default.10.99.154.147.sslip.io";
    private static final String NAMESPACE = "default";

    @Inject
    KubernetesClient client;

    @Inject
    KnativeServiceDiscovery knativeServiceDiscovery;

    @Test
    void queryService() {
        String remoteServiceUrl = "http://" + REMOTE_SERVICE_HOST;

        KubeTestUtils.createKnativeServiceIfNotExists(client, "knative/quarkus-greeting.yaml", NAMESPACE, "serverless-workflow-greeting-quarkus", remoteServiceUrl);

        Optional<URI> uri = knativeServiceDiscovery.query(new KnativeServiceUri(NAMESPACE, "serverless-workflow-greeting-quarkus"));

        assertThat(uri).map(URI::getHost).hasValue(REMOTE_SERVICE_HOST);
        assertThat(uri).map(URI::getPort).hasValue(-1);
        assertThat(uri).map(URI::getScheme).hasValue("http");
    }

    @Test
    void https() {
        String remoteServiceUrl = "https://" + REMOTE_SERVICE_HOST;

        KubeTestUtils.createKnativeServiceIfNotExists(client, "knative/quarkus-greeting-https.yaml", NAMESPACE, "serverless-workflow-greeting-quarkus-https", remoteServiceUrl);

        Optional<URI> url = knativeServiceDiscovery.query(new KnativeServiceUri(NAMESPACE, "serverless-workflow-greeting-quarkus-https"));

        assertThat(url).map(URI::getHost).hasValue(REMOTE_SERVICE_HOST);
        assertThat(url).map(URI::getPort).hasValue(-1);
        assertThat(url).map(URI::getScheme).hasValue("https");
    }
}
