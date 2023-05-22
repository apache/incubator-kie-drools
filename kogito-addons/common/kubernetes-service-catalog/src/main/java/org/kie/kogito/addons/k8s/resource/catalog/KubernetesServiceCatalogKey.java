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
package org.kie.kogito.addons.k8s.resource.catalog;

import java.util.Objects;

public final class KubernetesServiceCatalogKey {

    private final KubernetesProtocol protocol;

    private final String coordinates;

    public KubernetesServiceCatalogKey(KubernetesProtocol protocol, String coordinates) {
        this.protocol = protocol;
        this.coordinates = coordinates;
    }

    public KubernetesProtocol getProtocol() {
        return protocol;
    }

    public String getCoordinates() {
        return coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KubernetesServiceCatalogKey that = (KubernetesServiceCatalogKey) o;
        return protocol == that.protocol && Objects.equals(coordinates, that.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, coordinates);
    }
}
