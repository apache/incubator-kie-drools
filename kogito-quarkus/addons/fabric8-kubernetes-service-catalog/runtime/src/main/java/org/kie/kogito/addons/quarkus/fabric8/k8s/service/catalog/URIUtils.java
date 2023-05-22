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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class URIUtils {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private URIUtils() {
    }

    static Optional<URI> builder(String scheme, int port, String host) {
        try {
            logger.debug("Using scheme [{}], port[{}] and host[{}] to build the target service uri.",
                    scheme, port, host);
            return Optional.of(new URI(scheme, null, host, port, null, null, null));
        } catch (Exception e) {
            logger.warn("Failed to parser URI {}", e.getMessage());
        }
        return Optional.empty();
    }

}
