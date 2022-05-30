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
package org.kie.kogito.services.event.correlation;

import java.util.Objects;
import java.util.Optional;

import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.correlation.CorrelationResolver;
import org.kie.kogito.correlation.SimpleCorrelation;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleAttributeCorrelationResolver implements CorrelationResolver {

    private String referenceKey;
    private ObjectMapper objectMapper = ObjectMapperFactory.get();
    private Optional<Class<?>> type;

    public SimpleAttributeCorrelationResolver(String referenceKey) {
        this(referenceKey, null);
    }

    public SimpleAttributeCorrelationResolver(String referenceKey, Class<?> type) {
        this.referenceKey = Objects.requireNonNull(referenceKey, "referenceKey should not be null");
        this.type = Optional.ofNullable(type);
    }

    @Override
    public Correlation<?> resolve(Object data) {
        final JsonNode jsonNode = objectMapper.valueToTree(data);
        final JsonNode correlationValue = Optional.ofNullable(jsonNode.get(referenceKey))
                .orElseGet(() -> Optional.ofNullable(jsonNode.get(CloudEventExtensionConstants.EXTENSION_ATTRIBUTES))
                        .map(node -> node.get(referenceKey))
                        .orElse(null));

        if (correlationValue == null) {
            return new SimpleCorrelation(referenceKey, null);
        }

        if (correlationValue.isTextual()) {
            return new SimpleCorrelation(referenceKey, correlationValue.textValue());
        }

        return type.map(t -> objectMapper.convertValue(correlationValue, t))
                .map(v -> new SimpleCorrelation(referenceKey, v))
                .orElse(new SimpleCorrelation(referenceKey, correlationValue));
    }

    public static SimpleAttributeCorrelationResolver forAttribute(String attributeName) {
        return new SimpleAttributeCorrelationResolver(attributeName);
    }
}
