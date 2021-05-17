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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Targets.html#xsdElement_Target>Target</a>
 */
public class KiePMMLTarget extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -6336733489238275499L;
    private List<KiePMMLTargetValue> targetValues = null;
    private OP_TYPE opType;
    private String field;
    private CAST_INTEGER castInteger;
    private Double min = null;
    private Double max = null;
    private double rescaleConstant = 0;
    private double rescaleFactor = 1;

    private KiePMMLTarget(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    public Object modifyPrediction(Object prediction) {
        if (!(prediction instanceof Number)) {
            // TODO DROOLS-6345 TargetValue currently unimplemented - only direct number operations allowed
            return prediction;
        }
        double predictionDouble = (double) prediction;
        Number toReturn = applyMin(predictionDouble);
        toReturn = applyMax((double)toReturn);
        toReturn = applyRescaleFactor((double)toReturn);
        toReturn = applyRescaleConstant((double)toReturn);
        toReturn = applyCastInteger((double)toReturn);
        // TODO DROOLS-6345 TargetValue currently unimplemented
        return toReturn;
    }

    Double applyMin(double predictionDouble) {
        return  min != null ? Math.max(min, predictionDouble) : predictionDouble;
    }

    Double applyMax(double predictionDouble) {
        return  max != null ? Math.min(max, predictionDouble) : predictionDouble;
    }

    Double applyRescaleFactor(double predictionDouble) {
        return  predictionDouble * rescaleFactor;
    }

    Double applyRescaleConstant(double predictionDouble) {
        return  predictionDouble + rescaleConstant;
    }

    Number applyCastInteger(double predictionDouble) {
        return  castInteger != null ? castInteger.getScaledValue(predictionDouble) : predictionDouble;
    }

    public String getField() {
        return field;
    }

    public List<KiePMMLTargetValue> getTargetValues() {
        return targetValues;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    public CAST_INTEGER getCastInteger() {
        return castInteger;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public double getRescaleConstant() {
        return rescaleConstant;
    }

    public double getRescaleFactor() {
        return rescaleFactor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLTarget that = (KiePMMLTarget) o;
        return Double.compare(that.rescaleConstant, rescaleConstant) == 0 && Double.compare(that.rescaleFactor,
                                                                                            rescaleFactor) == 0 && Objects.equals(targetValues, that.targetValues) && opType == that.opType && Objects.equals(field, that.field) && castInteger == that.castInteger && Objects.equals(min, that.min) && Objects.equals(max, that.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetValues, opType, field, castInteger, min, max, rescaleConstant, rescaleFactor);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLTarget.class.getSimpleName() + "[", "]")
                .add("targetValues=" + targetValues)
                .add("opType=" + opType)
                .add("field='" + field + "'")
                .add("castInteger=" + castInteger)
                .add("min=" + min)
                .add("max=" + max)
                .add("rescaleConstant=" + rescaleConstant)
                .add("rescaleFactor=" + rescaleFactor)
                .add("name='" + name + "'")
                .add("extensions=" + extensions)
                .add("id='" + id + "'")
                .add("parentId='" + parentId + "'")
                .toString();
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLTarget> {

        private Builder(String name, List<KiePMMLExtension> extensions) {
            super("Target-", () -> new KiePMMLTarget(name, extensions));
        }

        public Builder withTargetValues(List<KiePMMLTargetValue> targetValues) {
            toBuild.targetValues = Collections.unmodifiableList(targetValues);
            return this;
        }

        public Builder withOpType(OP_TYPE opType) {
            toBuild.opType = opType;
            return this;
        }

        public Builder withField(String field) {
            toBuild.field = field;
            return this;
        }

        public Builder withCastInteger(CAST_INTEGER castInteger) {
            toBuild.castInteger = castInteger;
            return this;
        }

        public Builder withMin(double min) {
            toBuild.min = min;
            return this;
        }

        public Builder withMax(double max) {
            toBuild.max = max;
            return this;
        }

        public Builder withRescaleConstant(double rescaleConstant) {
            toBuild.rescaleConstant = rescaleConstant;
            return this;
        }

        public Builder withRescaleFactor(double rescaleFactor) {
            toBuild.rescaleFactor = rescaleFactor;
            return this;
        }

    }
}
