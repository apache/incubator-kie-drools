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

import java.io.InputStream;
import java.util.Optional;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;

final class KnativeServiceDiscoveryTestUtil {

    private KnativeServiceDiscoveryTestUtil() {
    }

    static Optional<KnativeClient> createServiceIfNotExists(KubernetesServer k8sServer, String remoteServiceUrl, String knativeYaml, String namespace, String serviceName) {
        if (k8sServer.getClient().services().inNamespace("test").withName(serviceName).get() != null) {
            return Optional.empty();
        }

        KnativeClient knativeClient = k8sServer.getClient().adapt(KnativeClient.class);

        Service service = knativeClient.services()
                .inNamespace(namespace)
                .load(getResourceAsStream(knativeYaml))
                .get();

        service.getStatus().setUrl(remoteServiceUrl);

        knativeClient.services().inNamespace(namespace).resource(service).create();

        return Optional.of(knativeClient);
    }

    private static InputStream getResourceAsStream(String knativeYaml) {
        return KnativeServiceDiscoveryTestUtil.class.getClassLoader().getResourceAsStream(knativeYaml);
    }
}
