/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
final class KnativeServiceRegistry {

    private final Map<String, KnativeServiceAddress> services = new ConcurrentHashMap<>();

    private final KnativeServiceDiscovery knativeServiceDiscovery;

    @Inject
    KnativeServiceRegistry(KnativeServiceDiscovery knativeServiceDiscovery) {
        this.knativeServiceDiscovery = knativeServiceDiscovery;
    }

    private Optional<KnativeServiceAddress> addService(String serviceName) {
        Optional<KnativeServiceAddress> serviceAddress = knativeServiceDiscovery.discover(serviceName);
        services.put(serviceName, serviceAddress.orElse(null));
        return serviceAddress;
    }

    Optional<KnativeServiceAddress> getServiceAddress(String serviceName) {
        if (services.containsKey(serviceName)) {
            return Optional.ofNullable(services.get(serviceName));
        } else {
            return addService(serviceName);
        }
    }
}
