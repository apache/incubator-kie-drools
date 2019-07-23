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

import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.client.dsl.RecreateFromServerGettable;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class KNativeDiscoveredServiceWorkItemHandlerTest extends BaseKubernetesDiscoveredServiceTest {

    public KNativeDiscoveredServiceWorkItemHandlerTest() {
        super(true);
    }

    @Test
    public void whenExistsAServiceWithKNative() {
        final RecreateFromServerGettable<KubernetesList, KubernetesList, ?> serviceResource =
                this.getClient().lists().load(this.getClass().getResource("/mock/responses/ocp4.x/knative/serving.knative.dev-services.json"));
        this.getClient().lists().create(serviceResource.get());

        final DiscoveredServiceWorkItemHandler handler = new TestDiscoveredServiceWorkItemHandler(this);
        final ServiceInfo serviceInfo = handler.findEndpoint(MOCK_NAMESPACE, "employeeValidation");
        assertThat(serviceInfo, notNullValue());
        assertThat(serviceInfo.getUrl(), is("http://172.30.101.218:80/employeeValidation"));
        assertThat(serviceInfo.getHeaders().get("HOST"), is("onboarding-hr.test.apps.example.com"));
    }

}
