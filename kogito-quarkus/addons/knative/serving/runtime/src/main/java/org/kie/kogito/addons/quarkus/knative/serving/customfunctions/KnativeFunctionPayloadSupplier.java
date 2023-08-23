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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeWorkItemHandler.PAYLOAD_FIELDS_PROPERTY_NAME;

final class KnativeFunctionPayloadSupplier {

    private KnativeFunctionPayloadSupplier() {
    }

    static Map<String, Object> getPayload(Map<String, Object> parameters) {
        return getPayloadFields(parameters).stream()
                .collect(toMap(Function.identity(), parameters::get));
    }

    private static List<String> getPayloadFields(Map<String, Object> parameters) {
        @SuppressWarnings("unchecked")
        List<String> payloadFields = (List<String>) parameters.remove(PAYLOAD_FIELDS_PROPERTY_NAME);
        return Objects.requireNonNullElseGet(payloadFields, List::of);
    }
}
