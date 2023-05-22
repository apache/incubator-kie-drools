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
package org.kie.kogito.addons.quarkus.k8s.config;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class KubeDiscoveryConfigCacheTest {

    @ParameterizedTest
    @ValueSource(strings = { "a_non_valid_uri", "knative", "kubernetes", "openshift" })
    void nonValidURIShouldNotBeCached(String nonValidURI) {
        KubeDiscoveryConfigCache kubeDiscoveryConfigCache = new KubeDiscoveryConfigCache(null);
        Optional<String> cachedValue = kubeDiscoveryConfigCache.get("my_config", nonValidURI);

        assertThat(cachedValue).hasValue(nonValidURI);
    }

    @ParameterizedTest
    @ValueSource(strings = { "kubernetes:services.v1/myservice", "openshift:services.v1/myservice" })
    void validURIShouldBeCached(String validURI) {
        String cachedUri = "cached_uri";
        KubeDiscoveryConfigCacheUpdater kResource = new ConstantKubeDiscoveryConfigCacheUpdater(cachedUri);
        KubeDiscoveryConfigCache kubeDiscoveryConfigCache = new KubeDiscoveryConfigCache(kResource);
        Optional<String> cachedValue = kubeDiscoveryConfigCache.get("my_config", validURI);

        assertThat(cachedValue).hasValue(cachedUri);
    }

    /**
     * Class that always returns the same value.
     */
    private static class ConstantKubeDiscoveryConfigCacheUpdater extends KubeDiscoveryConfigCacheUpdater {

        private final String alwaysReturnedValue;

        public ConstantKubeDiscoveryConfigCacheUpdater(String alwaysReturnedValue) {
            super(null);
            this.alwaysReturnedValue = alwaysReturnedValue;
        }

        @Override
        public Optional<URI> update(String configValue) {
            return Optional.of(URI.create(alwaysReturnedValue));
        }
    }
}
