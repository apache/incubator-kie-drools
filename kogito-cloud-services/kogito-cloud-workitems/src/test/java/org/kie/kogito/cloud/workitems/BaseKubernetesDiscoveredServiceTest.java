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

package org.kie.kogito.cloud.workitems;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ServiceResource;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.After;
import org.junit.Before;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.kogito.cloud.kubernetes.client.DefaultKogitoKubeClient;
import org.kie.kogito.cloud.kubernetes.client.KogitoKubeConfig;

/**
 * Base class for tests with Kubernetes API. In this scenario, nor Istio or KNative is available.
 */
public abstract class BaseKubernetesDiscoveredServiceTest {

    public KubernetesServer server = new KubernetesServer(true, true);

    public static final String MOCK_NAMESPACE = "mock-namespace";

    private boolean enableIstio;
    private boolean istioEnabled;

    public BaseKubernetesDiscoveredServiceTest() {
        this.enableIstio = false;
    }

    public BaseKubernetesDiscoveredServiceTest(final boolean enableIstio) {
        this.enableIstio = enableIstio;
    }

    // will be changed to junit5 extensions once migrated
    @Before
    public void before() {
        server.before();
        if (this.enableIstio) {
            this.createsIstioIngressGateway();
        }
    }

    // will be changed to junit5 extensions once migrated
    @After
    public void after() {
        server.after();
    }

    public boolean isIstioEnabled() {
        return istioEnabled;
    }

    protected KubernetesClient getClient() {
        return server.getClient().inNamespace(MOCK_NAMESPACE);
    }

    /**
     * Enables Istio in the test environment.
     */
    private void createsIstioIngressGateway() {
        final ServiceResource<Service, ?> serviceResource =
                this.server.getClient()
                           .inNamespace(KogitoKubeConfig.KNATIVE_ISTIO_NAMESPACE)
                           .services()
                           .load(this.getClass().getResource("/mock/responses/ocp4.x/istio/services-istio-ingressgateway.json"));
        this.server.getClient()
                   .inNamespace(KogitoKubeConfig.KNATIVE_ISTIO_NAMESPACE)
                   .services().create(serviceResource.get());
        this.istioEnabled = true;
    }

    protected static class TestDiscoveredServiceWorkItemHandler extends DiscoveredServiceWorkItemHandler {

        public TestDiscoveredServiceWorkItemHandler(BaseKubernetesDiscoveredServiceTest testCase) {
            super(new DefaultKogitoKubeClient().withConfig(new KogitoKubeConfig(testCase.getClient())));
        }

        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {}

        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {}

    }

}
