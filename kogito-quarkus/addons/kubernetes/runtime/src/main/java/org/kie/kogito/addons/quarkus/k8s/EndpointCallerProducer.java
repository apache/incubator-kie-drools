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
package org.kie.kogito.addons.quarkus.k8s;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.kie.kogito.addons.k8s.EndpointDiscovery;
import org.kie.kogito.addons.quarkus.k8s.workitems.QuarkusDiscoveredEndpointCaller;

import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class EndpointCallerProducer {

    @Inject
    ObjectMapper objectMapper;

    @Produces
    @Singleton
    @Default
    public QuarkusDiscoveredEndpointCaller endpointCaller(EndpointDiscovery defaultQuarkusEndpointDiscovery) {
        final QuarkusDiscoveredEndpointCaller endpointCaller = new QuarkusDiscoveredEndpointCaller(objectMapper);
        endpointCaller.setEndpointDiscovery(defaultQuarkusEndpointDiscovery);
        return endpointCaller;
    }

}
