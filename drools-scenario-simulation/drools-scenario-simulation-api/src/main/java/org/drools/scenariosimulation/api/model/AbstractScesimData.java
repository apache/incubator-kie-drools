/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.scenariosimulation.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Abstract class representing the values inside <code>AbstractScesimModel</code>, to be extended by concrete implementations
 */
public abstract class AbstractScesimData {

    /**
     * List of values to be used to test this scenario
     */
    protected final List<FactMappingValue> factMappingValues = new ArrayList<>();

    /**
     * Returns an <b>unmodifiable</b> list wrapping the backed one
     * <p>
     * NOTE: list order could not be aligned to factMapping order.
     */
    public List<FactMappingValue> getUnmodifiableFactMappingValues() {
        return Collections.unmodifiableList(factMappingValues);
    }

    public void removeFactMappingValueByIdentifiers(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        getFactMappingValue(factIdentifier, expressionIdentifier).ifPresent(factMappingValues::remove);
    }

    public void removeFactMappingValue(FactMappingValue toRemove) {
        factMappingValues.remove(toRemove);
    }

    public FactMappingValue addMappingValue(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier, Object value) {
        String factName = factIdentifier.getName();
        if (getFactMappingValue(factIdentifier, expressionIdentifier).isPresent()) {
            throw new IllegalArgumentException(
                    new StringBuilder().append("A fact value for expression '").append(expressionIdentifier.getName())
                            .append("' and fact '").append(factName).append("' already exist").toString());
        }
        FactMappingValue factMappingValue = new FactMappingValue(factIdentifier, expressionIdentifier, value);
        factMappingValues.add(factMappingValue);
        return factMappingValue;
    }

    public FactMappingValue addOrUpdateMappingValue(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier, Object value) {
        return getFactMappingValue(factIdentifier, expressionIdentifier).map(e -> {
            e.setRawValue(value);
            return e;
        }).orElseGet(() -> addMappingValue(factIdentifier, expressionIdentifier, value));
    }

    public Optional<FactMappingValue> getFactMappingValue(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        return factMappingValues.stream().filter(e -> Objects.equals(e.getFactIdentifier(), factIdentifier) &&
                Objects.equals(e.getExpressionIdentifier(), expressionIdentifier)).findFirst();
    }

    public Optional<FactMappingValue> getFactMappingValue(FactMapping factMapping) {
        return getFactMappingValue(factMapping.getFactIdentifier(), factMapping.getExpressionIdentifier());
    }

    public List<FactMappingValue> getFactMappingValuesByFactIdentifier(FactIdentifier factIdentifier) {
        return factMappingValues.stream().filter(e -> Objects.equals(e.getFactIdentifier(), factIdentifier)).collect(toList());
    }

    public String getDescription() {
        return factMappingValues.stream()
                .filter(e -> Objects.equals(e.getExpressionIdentifier(), ExpressionIdentifier.DESCRIPTION) &&
                        Objects.equals(e.getFactIdentifier(), FactIdentifier.DESCRIPTION) &&
                        e.getRawValue() != null)
                .map(e -> (String) e.getRawValue())
                .findFirst().orElse("");
    }

    public void setDescription(String name) {
        addOrUpdateMappingValue(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION, name);
    }

    public Collection<String> getFactNames() {
        return factMappingValues.stream().map(e -> e.getFactIdentifier().getName()).collect(toSet());
    }

    public void resetErrors() {
        factMappingValues.forEach(FactMappingValue::resetStatus);
    }

    abstract AbstractScesimData cloneInstance();
}