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
package org.kie.kogito.addons.quarkus.k8s;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.kie.kogito.addons.quarkus.k8s.parser.KubeURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.kubernetes.client.KubernetesClient;

public class KnativeResourceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private KnativeClient knativeClient;

    public KnativeResourceDiscovery(final KubernetesClient kubernetesClient) {
        logger.debug("Trying to adapt kubernetes client to knative");
        this.knativeClient = kubernetesClient.adapt(KnativeClient.class);
    }

    public Optional<URL> queryServiceByName(KubeURI kubeURI) {
        Service service = knativeClient.services().inNamespace(kubeURI.getNamespace()).withName(kubeURI.getResourceName()).get();
        if (null == service) {
            logger.error("Knative {} service not found on the {} namespace.", kubeURI.getResourceName(), kubeURI.getNamespace());
            return Optional.empty();
        }
        logger.debug("Found Knative endpoint at {}", service.getStatus().getUrl());
        try {
            return Optional.of(new URL(service.getStatus().getUrl()));
        } catch (MalformedURLException e) {
            logger.error("Failed to query Knative service", e);
            return Optional.empty();
        }
    }
}
