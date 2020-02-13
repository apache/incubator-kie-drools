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

import java.util.Objects;

import org.kie.pmml.commons.model.abstracts.KiePMMLIDedNamed;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Output.html#xsdElement_OutputField>OutputField</a>
 */
public class KiePMMLOutputField extends KiePMMLIDedNamed {



    private RESULT_FEATURE resultFeature = RESULT_FEATURE.PREDICTED_VALUE;
    private String targetField;
    private Object value;


    public static Builder builder(String name) {
        return new Builder(name);
    }

    public RESULT_FEATURE getResultFeature() {
        return resultFeature;
    }

    public String getTargetField() {
        return targetField;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "KiePMMLOutputField{" +
                "resultFeature=" + resultFeature +
                ", targetField='" + targetField + '\'' +
                ", value=" + value +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
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

    public static class Builder extends KiePMMLIDedNamed.Builder<KiePMMLOutputField> {

        private Builder(String name) {
            super(name, "OutputField-", KiePMMLOutputField::new);
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
    }
}
