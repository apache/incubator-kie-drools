/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.Optional;
import java.util.StringJoiner;

import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.api.enums.RESULT_FEATURE;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Output.html#xsdElement_OutputField>OutputField</a>
 */
public class KiePMMLOutputField extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = 2408750585433339543L;
    private RESULT_FEATURE resultFeature = RESULT_FEATURE.PREDICTED_VALUE;
    private String targetField = null;
    private Integer rank;
    private Object value;

    private KiePMMLOutputField(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    public RESULT_FEATURE getResultFeature() {
        return resultFeature;
    }

    public Optional<String> getTargetField() {
        return Optional.ofNullable(targetField);
    }

    public Object getValue() {
        return value;
    }

    public Integer getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLOutputField.class.getSimpleName() + "[", "]")
                .add("resultFeature=" + resultFeature)
                .add("targetField='" + targetField + "'")
                .add("rank=" + rank)
                .add("value=" + value)
                .add("name='" + name + "'")
                .add("extensions=" + extensions)
                .add("id='" + id + "'")
                .add("parentId='" + parentId + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        KiePMMLOutputField that = (KiePMMLOutputField) o;
        return resultFeature == that.resultFeature &&
                Objects.equals(targetField, that.targetField) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resultFeature, targetField, value);
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLOutputField> {

        private Builder(String name, List<KiePMMLExtension> extensions) {
            super("OutputField-", () -> new KiePMMLOutputField(name, extensions));
        }

        public Builder withResultFeature(RESULT_FEATURE resultFeature) {
            toBuild.resultFeature = resultFeature;
            return this;
        }

        public Builder withTargetField(String targetField) {
            toBuild.targetField = targetField;
            return this;
        }

        public Builder withValue(Object value) {
            toBuild.value = value;
            return this;
        }

        public Builder withRank(Integer rank) {
            toBuild.rank = rank;
            return this;
        }
    }
}
