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
package org.kie.kogito.addons.k8s;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Entry point interface for the {@link Endpoint} discovery engine.
 */
public interface EndpointDiscovery {

    /**
     * Finds an endpoint by Kubernetes Namespace and Name
     *
     * @param namespace kubernetes namespace
     * @param name kubernetes name
     * @return an {@link Optional} endpoint
     */
    Optional<Endpoint> findEndpoint(String namespace, String name);

    /**
     * Finds an endpoint by its labels. Implementations should define the target object. For example a Service or a Knative Service.
     *
     * @param labels map containing the labels of the object.
     * @param namespace kubernetes namespace
     * @return a {@link Set} of discovered endpoints. Kubernetes objects can have the same label. The caller should know how to distinguish the endpoint.
     */
    List<Endpoint> findEndpoint(String namespace, Map<String, String> labels);
}
