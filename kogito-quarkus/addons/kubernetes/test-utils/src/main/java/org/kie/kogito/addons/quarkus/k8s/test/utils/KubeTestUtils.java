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
import java.lang.reflect.Method;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.Resource;

public final class KubeTestUtils {

    private KubeTestUtils() {
    }

    public static <T extends HasMetadata> T createWithStatusPreserved(KubernetesClient client, T resource, String namespace, Class<T> resourceType) {
        T created = null;
        try {
            created = client.resources(resourceType).inNamespace(namespace).resource(resource).createOr(existing -> {
                // Reload latest version to avoid 409 conflict
                T latest = client.resources(resourceType).inNamespace(namespace).withName(resource.getMetadata().getName()).get();

                if (latest != null) {
                    // Overwrite with user-defined spec but preserve server-assigned fields
                    resource.getMetadata().setResourceVersion(latest.getMetadata().getResourceVersion());
                    return client.resources(resourceType).inNamespace(namespace).resource(resource).update();
                }
                return resource; // fallback
            });
        } catch (KubernetesClientException e) {
            throw new RuntimeException("Unable to create or update resource: " + resource.getMetadata().getName(), e);
        }

        Object status = extractStatus(resource);
        if (status != null) {
            applyStatus(client, created, namespace, resourceType, status);
        }

        return created;
    }

    private static <T extends HasMetadata> void applyStatus(KubernetesClient client, T resource, String namespace, Class<T> resourceType, Object status) {
        try {
            Resource<T> resourceHandle = client.resources(resourceType).inNamespace(namespace).withName(resource.getMetadata().getName());

            T latest = resourceHandle.get();
            if (latest != null && status != null) {
                Method setStatusMethod = latest.getClass().getMethod("setStatus", status.getClass());
                setStatusMethod.invoke(latest, status);
                client.resources(resourceType).inNamespace(namespace).resource(latest).patchStatus();
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to set status on resource", e);
        }
    }

    private static <T> Object extractStatus(T resource) {
        try {
            return resource.getClass().getMethod("getStatus").invoke(resource);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get status from resource", e);
        }
    }

    public static void createKnativeServiceIfNotExists(KubernetesClient client, String yamlPath, String namespace, String serviceName) {
        createKnativeServiceIfNotExists(client, yamlPath, namespace, serviceName, null);
    }

    public static void createKnativeServiceIfNotExists(KubernetesClient client, String yamlPath, String namespace, String serviceName, String remoteServiceUrl) {
        if (client.services().inNamespace(namespace).withName(serviceName).get() == null) {
            KnativeClient knativeClient = client.adapt(KnativeClient.class);

            Service service = knativeClient.services().inNamespace(namespace).load(getResourceAsStream(yamlPath)).item();

            if (remoteServiceUrl != null) {
                service.getStatus().setUrl(remoteServiceUrl);
            }

            knativeClient.services().inNamespace(namespace).resource(service).createOr(existing -> knativeClient.services().inNamespace(namespace).resource(service).update());

            Object status = extractStatus(service);
            if (status != null) {
                try {
                    Service latest = knativeClient.services().inNamespace(namespace).withName(service.getMetadata().getName()).get();

                    if (latest != null && latest.getStatus() == null) {
                        latest.getClass().getMethod("setStatus", status.getClass()).invoke(latest, status);
                        knativeClient.services().inNamespace(namespace).resource(latest).patchStatus();
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Unable to patch Knative service status", e);
                }
            }
        }
    }

    private static InputStream getResourceAsStream(String yamlPath) {
        return KubeTestUtils.class.getClassLoader().getResourceAsStream(yamlPath);
    }
}
