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

import java.net.URL;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.addons.quarkus.k8s.KnativeResourceDiscovery;

@ApplicationScoped
final class KnativeServiceDiscovery {

    private final KnativeResourceDiscovery knativeResourceDiscovery;

    private final String currentContext;

    @Inject
    KnativeServiceDiscovery(KnativeResourceDiscovery knativeResourceDiscovery) {
        this.knativeResourceDiscovery = knativeResourceDiscovery;
        this.currentContext = knativeResourceDiscovery.getCurrentContext();
    }

    Optional<KnativeServiceAddress> discover(String serviceName) {
        KnativeServiceIdentifier serviceIdentifier = new KnativeServiceIdentifier(serviceName);

        return knativeResourceDiscovery.queryService(serviceIdentifier.getNamespace().orElse(currentContext), serviceIdentifier.getName())
                .map(url -> {
                    if (isSsl(url)) {
                        return new KnativeServiceAddress(true, url.getHost(), url.getPort() == -1 ? 443 : url.getPort());
                    } else {
                        return new KnativeServiceAddress(false, url.getHost(), url.getPort() == -1 ? 80 : url.getPort());
                    }
                });
    }

    private static boolean isSsl(URL url) {
        return "https".equals(url.getProtocol());
    }
}
