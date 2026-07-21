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
package org.kie.kogito.addons.quarkus.k8s.test.utils;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesCrudDispatcher;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.mockwebserver.MockWebServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Quarkus test resource that provides a Fabric8 Kubernetes mock server with CRUD support.
 */
public class KubernetesMockServerTestResource implements QuarkusTestResourceLifecycleManager {

    private static final String TEST_NAMESPACE = "serverless-workflow-greeting-quarkus";
    private KubernetesMockServer server;
    private KubernetesClient client;
    private String previousMasterUrl;

    @Override
    public Map<String, String> start() {
        server = new KubernetesMockServer(
                new io.fabric8.mockwebserver.Context(),
                new MockWebServer(),
                new HashMap<>(),
                new KubernetesCrudDispatcher(),
                false);
        server.init();

        client = server.createClient();
        String mockServerUrl = client.getConfiguration().getMasterUrl();

        previousMasterUrl = System.getProperty(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY);
        System.setProperty(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY, mockServerUrl);

        Map<String, String> config = new HashMap<>();
        config.put("quarkus.kubernetes-client.master-url", mockServerUrl);
        config.put("quarkus.kubernetes-client.namespace", TEST_NAMESPACE);
        config.put("quarkus.kubernetes-client.trust-certs", "true");
        return config;
    }

    @Override
    public void stop() {
        if (previousMasterUrl != null) {
            System.setProperty(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY, previousMasterUrl);
        } else {
            System.clearProperty(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY);
        }
        if (client != null) {
            client.close();
        }
        if (server != null) {
            server.destroy();
            server = null;
        }
    }

    public KubernetesMockServer getServer() {
        return server;
    }

    public KubernetesClient getClient() {
        return client;
    }
}
