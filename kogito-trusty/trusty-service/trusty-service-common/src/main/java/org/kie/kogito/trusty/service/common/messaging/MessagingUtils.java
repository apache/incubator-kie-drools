/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.trusty.service.common.messaging;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MessagingUtils {

    private MessagingUtils() {
    }

    public static org.kie.kogito.tracing.typedvalue.TypedValue modelToTracingTypedValue(org.kie.kogito.trusty.storage.api.model.TypedVariable value) {
        if (value == null) {
            return null;
        }
        switch (value.getKind()) {
            case UNIT:
                return new org.kie.kogito.tracing.typedvalue.UnitValue(value.getTypeRef(), null, value.getValue());
            case COLLECTION:
                return new org.kie.kogito.tracing.typedvalue.CollectionValue(value.getTypeRef(), modelToTracingTypedValueCollection(value.getComponents()));
            case STRUCTURE:
                return new org.kie.kogito.tracing.typedvalue.StructureValue(value.getTypeRef(), modelToTracingTypedValueMap(value.getComponents()));
        }
        throw new IllegalStateException("Can't convert org.kie.kogito.trusty.storage.api.model.TypedVariable of kind " + value.getKind() + " to TypedValue");
    }

    public static Collection<org.kie.kogito.tracing.typedvalue.TypedValue> modelToTracingTypedValueCollection(Collection<org.kie.kogito.trusty.storage.api.model.TypedVariable> input) {
        if (input == null) {
            return null;
        }
        return input.stream().map(MessagingUtils::modelToTracingTypedValue).collect(Collectors.toList());
    }

    public static Map<String, org.kie.kogito.tracing.typedvalue.TypedValue> modelToTracingTypedValueMap(Collection<org.kie.kogito.trusty.storage.api.model.TypedVariable> input) {
        if (input == null) {
            return null;
        }
        return input.stream()
                .filter(m -> m.getName() != null)
                .collect(HashMap::new, (m, v) -> m.put(v.getName(), modelToTracingTypedValue(v)), HashMap::putAll);
    }
}
