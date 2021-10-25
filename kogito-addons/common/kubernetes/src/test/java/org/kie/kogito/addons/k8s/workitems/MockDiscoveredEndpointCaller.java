/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.k8s.workitems;

import java.util.Collections;
import java.util.Map;

import org.kie.kogito.addons.k8s.Endpoint;
import org.kie.kogito.addons.k8s.EndpointDiscovery;
import org.kie.kogito.addons.k8s.EndpointQueryKey;
import org.kie.kogito.addons.k8s.LocalEndpointDiscovery;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MockDiscoveredEndpointCaller extends AbstractDiscoveredEndpointCaller {

    private final LocalEndpointDiscovery endpointDiscovery = new LocalEndpointDiscovery();

    public final static Map<String, String> SERVICE_LABELS = Collections.singletonMap("app", null);
    public final static String NAMESPACE = "test";

    public MockDiscoveredEndpointCaller(final String endpointURL) {
        super(new ObjectMapper());
        this.endpointDiscovery.addCache(new EndpointQueryKey(NAMESPACE, SERVICE_LABELS), new Endpoint(endpointURL));
    }

    @Override
    protected EndpointDiscovery getEndpointDiscovery() {
        return endpointDiscovery;
    }
}
