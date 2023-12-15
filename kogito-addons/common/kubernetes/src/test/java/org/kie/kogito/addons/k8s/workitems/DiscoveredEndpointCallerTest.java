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
package org.kie.kogito.addons.k8s.workitems;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.process.workitems.impl.KogitoWorkItemImpl;

import jakarta.ws.rs.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(TargetEndpointsMockServerExtension.class)
public class DiscoveredEndpointCallerTest {

    AbstractDiscoveredEndpointCaller endpointCaller;

    public DiscoveredEndpointCallerTest(final String endpointURL) {
        this.endpointCaller = new MockDiscoveredEndpointCaller(endpointURL);
    }

    @Test
    void testDiscoveryAndCall() {
        final KogitoWorkItemImpl workItem = new KogitoWorkItemImpl();
        workItem.setParameter("discovery", "app");
        final Map<String, Object> response = this.endpointCaller.discoverAndCall(workItem, MockDiscoveredEndpointCaller.NAMESPACE, "discovery", HttpMethod.GET);
        assertThat(response).isNotNull()
                .containsEntry("response", "OK");
    }

}
