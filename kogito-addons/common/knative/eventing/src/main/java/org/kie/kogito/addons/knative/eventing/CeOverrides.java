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
package org.kie.kogito.addons.knative.eventing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Representation of @{@link KnativeEventingMessagePayloadDecorator#K_CE_OVERRIDES} value
 * 
 * @see <a href="https://knative.dev/docs/developer/eventing/sources/sinkbinding/reference/#cloudevent-overrides">Knative Eventing SinkBinding - CloudEvent Overrides</a>
 */
public class CeOverrides {
    private Map<String, Object> extensions;

    public CeOverrides() {
        this.extensions = new HashMap<>();
    }

    public Map<String, Object> getExtensions() {
        return Collections.unmodifiableMap(this.extensions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CeOverrides that = (CeOverrides) o;
        return Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensions);
    }
}
