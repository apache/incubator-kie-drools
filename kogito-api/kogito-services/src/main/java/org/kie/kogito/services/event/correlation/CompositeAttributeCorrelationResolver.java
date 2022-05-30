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

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.correlation.CorrelationResolver;

public class CompositeAttributeCorrelationResolver implements CorrelationResolver {

    private Map<String, CorrelationResolver> attributes;

    public CompositeAttributeCorrelationResolver(Set<String> attributeNames) {
        Objects.requireNonNull(attributeNames, "attributeNames should not be null");
        attributes = attributeNames.stream().collect(Collectors.toMap(name -> name, SimpleAttributeCorrelationResolver::new));
    }

    @Override
    public CompositeCorrelation resolve(Object data) {
        Set<Correlation<?>> correlations = attributes.entrySet().stream()
                .map(e -> e.getValue().resolve(data))
                .collect(Collectors.toSet());
        return new CompositeCorrelation(correlations);
    }
}
