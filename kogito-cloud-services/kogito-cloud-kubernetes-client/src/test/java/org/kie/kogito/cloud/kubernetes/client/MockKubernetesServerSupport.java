/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.cloud.kubernetes.client;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.api.model.LoadBalancerStatus;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.ServiceStatus;
import io.fabric8.kubernetes.client.dsl.RecreateFromServerGettable;
import io.fabric8.kubernetes.client.dsl.ServiceResource;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class to test use cases that need to query the API
 */
public abstract class MockKubernetesServerSupport {

    public static final String MOCK_NAMESPACE = "test";
    private KubernetesServer server;
    private KogitoKubeClient kubeClient;

    public MockKubernetesServerSupport() {
        this.initializeServer(true);
    }

    public MockKubernetesServerSupport(final boolean crudMode) {
        this.initializeServer(crudMode);
    }

    public KogitoKubeClient getKubeClient() {
        return this.kubeClient;
    }

    public KubernetesServer getServer() {
        return server;
    }

    /**
     * Override to setup a different kind of server
     */
    protected final void initializeServer(final boolean crudMode) {
        this.server = new KubernetesServer(false, crudMode);
    }

    @BeforeEach
    public void before() {
        server.before();
        this.kubeClient = new DefaultKogitoKubeClient().withConfig(new KogitoKubeConfig(server.getClient()));
    }

    @AfterEach
    public void after() {
        server.after();
    }

    /**
     * Creates a new mock service in the {@value #MOCK_NAMESPACE} with 127.0.0.1:8080 address
     */
    protected void createMockService() {
        this.createMockService("test", "127.0.0.1", Collections.singletonMap("service", "test"), MOCK_NAMESPACE);
    }

    /**
     * Same as {@link #createMockService()}, but let you choose the namespace. 
     * @param namespace null to not specify where.
     */
    protected void createMockService(final String namespace) {
        this.createMockService("test", "127.0.0.1", Collections.singletonMap("service", "test"), namespace);
    }

    /**
     * Creates a service based on an {@link InputStream} of a json service response.
     * 
     * @param mockJsonResponse
     */
    protected void createMockService(final InputStream mockJsonResponse, final String namespace) {
        final ServiceResource<Service, ?> serviceResource = this.server.getClient().inNamespace(namespace).services().load(mockJsonResponse);
        this.server.getClient().inNamespace(namespace).services().create(serviceResource.get());
    }

    /**
     * Creates a list of services based on a {@link InputStream} of a json servicelist response
     * 
     * @param mockJsonResponse
     * @param namespace
     */
    protected void createMockServices(final InputStream mockJsonResponse, final String namespace) {
        final RecreateFromServerGettable<KubernetesList, KubernetesList, ?> serviceResource = this.server.getClient().inNamespace(namespace).lists().load(mockJsonResponse);
        this.server.getClient().inNamespace(namespace).lists().create(serviceResource.get());
    }

    protected void createMockService(final String serviceName, final String ip, final Map<String, String> labels, final String namespace) {
        final ServiceSpec serviceSpec = new ServiceSpec();
        serviceSpec.setPorts(Collections.singletonList(new ServicePort("http", 0, 8080, "http", new IntOrString(8080))));
        serviceSpec.setClusterIP(ip);
        serviceSpec.setType("ClusterIP");
        serviceSpec.setSessionAffinity("ClientIP");

        final ObjectMeta metadata = new ObjectMeta();
        metadata.setName(serviceName);
        metadata.setNamespace(MOCK_NAMESPACE);
        metadata.setLabels(labels);

        final Service service = new Service("v1", "Service", metadata, serviceSpec, new ServiceStatus(new LoadBalancerStatus()));
        if (namespace != null) {
            this.server.getClient().inNamespace(namespace).services().create(service);
        } else {
            this.server.getClient().services().create(service);
        }

    }
}
