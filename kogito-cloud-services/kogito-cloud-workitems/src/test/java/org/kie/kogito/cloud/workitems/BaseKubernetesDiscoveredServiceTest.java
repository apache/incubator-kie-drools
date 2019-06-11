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

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.After;
import org.junit.Before;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * Base class for tests with Kubernetes API. In this scenario, nor Istio or KNative is available.
 */
public abstract class BaseKubernetesDiscoveredServiceTest {

    public KubernetesServer server = new KubernetesServer(true, true);

    public static final String MOCK_NAMESPACE = "mock-namespace";

    // will be changed to junit5 extensions once migrated
    @Before
    public void before() {
        server.before();
    }

    // will be changed to junit5 extensions once migrated
    @After
    public void after() {
        server.after();
    }

    protected KubernetesClient getClient() {

        return server.getClient().inNamespace(MOCK_NAMESPACE);
    }

    protected static class TestDiscoveredServiceWorkItemHandler extends DiscoveredServiceWorkItemHandler {

        private final BaseKubernetesDiscoveredServiceTest testCase;

        public TestDiscoveredServiceWorkItemHandler(BaseKubernetesDiscoveredServiceTest testCase) {
            super();
            this.testCase = testCase;
        }

        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {}

        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {}

        @Override
        protected KubernetesClient getKubeClient() {
            this.istionGatewayClusterIp = null;
            return this.testCase.getClient();
        }

    }

}
