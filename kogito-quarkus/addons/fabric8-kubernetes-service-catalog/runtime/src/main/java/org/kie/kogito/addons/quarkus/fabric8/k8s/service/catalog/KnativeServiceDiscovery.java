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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class KnativeServiceDiscovery extends AbstractResourceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(KnativeServiceDiscovery.class);

    private final KnativeClient knativeClient;

    @Inject
    KnativeServiceDiscovery(KnativeClient knativeClient) {
        this.knativeClient = knativeClient;
    }

    Optional<URI> query(KnativeServiceUri knativeServiceUri) {
        logConnection(knativeClient, knativeServiceUri.getResourceName());

        final String namespace;

        if (knativeServiceUri.getNamespace() == null) {
            namespace = knativeClient.getNamespace();
            logDefaultNamespace(namespace);
        } else {
            namespace = knativeServiceUri.getNamespace();
        }

        Service service = knativeClient.services().inNamespace(namespace).withName(knativeServiceUri.getResourceName()).get();
        if (null == service) {
            logger.error("Knative {} service not found on the {} namespace.", knativeServiceUri.getResourceName(), namespace);
            return Optional.empty();
        }
        logger.debug("Found Knative endpoint at {}", service.getStatus().getUrl());
        return Optional.of(URI.create(service.getStatus().getUrl()));
    }

    private void logConnection(KnativeClient client, String resourceName) {
        logger.info("Connected to Knative, current namespace is {}. Resource name for discovery is {}",
                client.getNamespace(),
                resourceName);
    }
}
