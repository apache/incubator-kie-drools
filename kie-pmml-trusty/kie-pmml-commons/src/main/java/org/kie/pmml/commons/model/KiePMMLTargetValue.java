/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.commons.model;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Targets.html#xsdElement_TargetValue>TargetValue</a>
 */
public class KiePMMLTargetValue extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -4948552909458142415L;
    private String value;
    private String displayValue;
    private Double priorProbability; // double between 0.0 and 1.0, usually describing a probability.
    private Double defaultValue;

    private KiePMMLTargetValue(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
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
        KiePMMLTargetValue that = (KiePMMLTargetValue) o;
        return Objects.equals(value, that.value) && Objects.equals(displayValue, that.displayValue) && Objects.equals(priorProbability, that.priorProbability) && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, displayValue, priorProbability, defaultValue);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLTargetValue.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .add("displayValue='" + displayValue + "'")
                .add("priorProbability=" + priorProbability)
                .add("defaultValue=" + defaultValue)
                .add("name='" + name + "'")
                .add("extensions=" + extensions)
                .add("id='" + id + "'")
                .add("parentId='" + parentId + "'")
                .toString();
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLTargetValue> {

        private Builder(String name, List<KiePMMLExtension> extensions) {
            super("TargetValue-", () -> new KiePMMLTargetValue(name, extensions));
        }

        public Builder withValue(String value) {
            if (value != null) {
                toBuild.value = value;
            }
            return this;
        }

        public Builder withDisplayValue(String displayValue) {
            if (displayValue != null) {
                toBuild.displayValue = displayValue;
            }
            return this;
        }

        public Builder withPriorProbability(Number priorProbability) {
            if (priorProbability != null) {
                toBuild.priorProbability =  priorProbability.doubleValue();
            }
            return this;
        }

        public Builder withDefaultValue(Number defaultValue) {
            if (defaultValue != null) {
                toBuild.defaultValue = defaultValue.doubleValue();
            }
            return this;
        }
    }
}
