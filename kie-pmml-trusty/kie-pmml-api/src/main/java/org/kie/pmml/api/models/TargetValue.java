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
package org.kie.pmml.api.models;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * User-friendly representation of a <b>TargetValue</b>
 */
public class TargetValue implements Serializable {

    private static final long serialVersionUID = 4273827699580322524L;
    private final String name;
    private final String value;
    private final String displayValue;
    private final Double priorProbability; // double between 0.0 and 1.0, usually describing a probability.
    private final Double defaultValue;

    public TargetValue(String value, String displayValue, Number priorProbability, Number defaultValue) {
        this.name = UUID.randomUUID().toString();
        this.value = value;
        this.displayValue = displayValue;
        this.priorProbability = priorProbability != null ? priorProbability.doubleValue() : null;
        this.defaultValue = defaultValue != null ? defaultValue.doubleValue() : null;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public Double getPriorProbability() {
        return priorProbability;
    }

    public Double getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TargetValue that = (TargetValue) o;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value) && Objects.equals(displayValue,
                                                                                                      that.displayValue) && Objects.equals(priorProbability, that.priorProbability) && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, displayValue, priorProbability, defaultValue);
    }

    @Override
    public String toString() {
        return "TargetValue{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", displayValue='" + displayValue + '\'' +
                ", priorProbability=" + priorProbability +
                ", defaultValue=" + defaultValue +
                '}';
    }
}
