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

import java.io.InputStream;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;

public final class KnativeResourceDiscoveryTestUtil {

    private KnativeResourceDiscoveryTestUtil() {
    }

    public static void createServiceIfNotExists(KubernetesServer k8sServer, String knativeYaml, String namespace, String serviceName) {
        createServiceIfNotExists(k8sServer, knativeYaml, namespace, serviceName, null);
    }

    @SuppressWarnings("deprecation") // Quarkus LTS 2.13 compatibility
    public static void createServiceIfNotExists(KubernetesServer k8sServer, String knativeYaml, String namespace, String serviceName, String remoteServiceUrl) {
        if (k8sServer.getClient().services().inNamespace(namespace).withName(serviceName).get() == null) {
            KnativeClient knativeClient = k8sServer.getClient().adapt(KnativeClient.class);

            Service service = knativeClient.services()
                    .inNamespace(namespace)
                    .load(getResourceAsStream(knativeYaml))
                    .item();

            if (remoteServiceUrl != null) {
                service.getStatus().setUrl(remoteServiceUrl);
            }

            // ItemWritableOperation#create is deprecated. However, we can't use the new method while Quarkus LTS is not greater than 2.16.
            knativeClient.services().inNamespace(namespace).create(service);
        }
    }

    private static InputStream getResourceAsStream(String knativeYaml) {
        return KnativeResourceDiscoveryTestUtil.class.getClassLoader().getResourceAsStream(knativeYaml);
    }
}
