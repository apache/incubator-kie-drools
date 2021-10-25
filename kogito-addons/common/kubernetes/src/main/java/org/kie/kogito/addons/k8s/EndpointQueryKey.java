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

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Data Holder for the discovery query key terms.
 */
public class EndpointQueryKey implements Serializable {

    private String name;
    private String namespace;
    private Map<String, String> labels;

    public EndpointQueryKey(final String namespace, final String name) {
        this.name = name;
        this.namespace = namespace;
    }

    public EndpointQueryKey(final String namespace, final Map<String, String> labels) {
        this.namespace = namespace;
        this.labels = labels;
    }

    public Map<String, String> getLabels() {
        return Collections.unmodifiableMap(labels);
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EndpointQueryKey that = (EndpointQueryKey) o;
        return Objects.equals(name, that.name) && Objects.equals(namespace, that.namespace) && Objects.equals(labels, that.labels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, namespace, labels);
    }
}
