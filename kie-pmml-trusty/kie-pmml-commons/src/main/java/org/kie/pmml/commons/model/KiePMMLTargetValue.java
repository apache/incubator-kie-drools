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
package org.kie.pmml.commons.model;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.kie.pmml.api.models.TargetValue;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

public class KiePMMLTargetValue extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -4948552909458142415L;
    private final TargetValue targetValue;

    private KiePMMLTargetValue(String name, List<KiePMMLExtension> extensions, TargetValue targetValue) {
        super(name, extensions);
        this.targetValue = targetValue;
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions, TargetValue targetValue) {
        return new Builder(name, extensions, targetValue);
    }

    public String getValue() {
        return targetValue.getValue();
    }

    public String getDisplayValue() {
        return targetValue.getDisplayValue();
    }

    public Double getPriorProbability() {
        return targetValue.getPriorProbability();
    }

    public Double getDefaultValue() {
        return targetValue.getDefaultValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLTargetValue that = (KiePMMLTargetValue) o;
        return Objects.equals(targetValue, that.targetValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetValue);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLTargetValue.class.getSimpleName() + "[", "]")
                .add("targetValue='" + targetValue + "'")
                .add("name='" + name + "'")
                .add("extensions=" + extensions)
                .add("id='" + id + "'")
                .add("parentId='" + parentId + "'")
                .toString();
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLTargetValue> {

        private Builder(String name, List<KiePMMLExtension> extensions, TargetValue targetValue) {
            super("TargetValue-", () -> new KiePMMLTargetValue(name, extensions, targetValue));
        }
    }
}
