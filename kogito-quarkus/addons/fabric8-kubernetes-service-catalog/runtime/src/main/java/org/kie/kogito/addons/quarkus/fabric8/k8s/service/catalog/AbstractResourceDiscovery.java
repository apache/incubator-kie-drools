/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;

abstract class AbstractResourceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(AbstractResourceDiscovery.class);

    protected final void logDefaultNamespace(String defaultNamespace) {
        logger.warn("Namespace is not set, setting namespace to the current context [{}].", defaultNamespace);
    }

    protected final void logConnection(KubernetesClient client, String resourceName) {
        logger.info("Connected to kubernetes cluster {}, current namespace is {}. Resource name for discovery is {}",
                client.getKubernetesVersion().getGitVersion(),
                client.getNamespace(),
                resourceName);
    }
}
