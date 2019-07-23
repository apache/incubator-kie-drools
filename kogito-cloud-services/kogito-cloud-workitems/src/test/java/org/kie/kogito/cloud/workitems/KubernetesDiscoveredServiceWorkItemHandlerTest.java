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

import java.io.IOException;
import java.util.Collections;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.LoadBalancerStatus;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.ServiceStatus;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


public class KubernetesDiscoveredServiceWorkItemHandlerTest extends BaseKubernetesDiscoveredServiceTest {

    @Test
    public void testGivenServiceExists() {
        final ServiceSpec serviceSpec = new ServiceSpec();
        serviceSpec.setPorts(Collections.singletonList(new ServicePort("http", 0, 8080, "http", new IntOrString(8080))));
        serviceSpec.setClusterIP("172.30.158.31");
        serviceSpec.setType("ClusterIP");
        serviceSpec.setSessionAffinity("ClientIP");

        final ObjectMeta metadata = new ObjectMeta();
        metadata.setName("test-kieserver");
        metadata.setNamespace(MOCK_NAMESPACE);
        metadata.setLabels(Collections.singletonMap("service", "test-kieserver"));

        final Service service = new Service("v1", "Service", metadata, serviceSpec, new ServiceStatus(new LoadBalancerStatus()));
        getClient().services().create(service);

        final DiscoveredServiceWorkItemHandler handler = new TestDiscoveredServiceWorkItemHandler(this);
        final ServiceInfo serviceInfo = handler.findEndpoint(MOCK_NAMESPACE, "test-kieserver");
        assertThat(serviceInfo, notNullValue());
        assertThat(serviceInfo.getUrl(), is("http://172.30.158.31:8080/test-kieserver"));
    }

    @Test
    public void testGivenServiceNotExists() throws IOException {
        final DiscoveredServiceWorkItemHandler handler = new TestDiscoveredServiceWorkItemHandler(this);
        try {
            handler.findEndpoint(MOCK_NAMESPACE, "test-kieserver");
            fail("Finding of non existing endpoint should throw RuntimeException.");
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage(), containsString("No endpoint found"));
        }
    }
}
