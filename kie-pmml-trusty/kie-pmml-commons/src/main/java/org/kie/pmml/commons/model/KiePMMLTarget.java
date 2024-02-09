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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

public class KiePMMLTarget extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -6336733489238275499L;

    private final TargetField targetField;
    private final List<KiePMMLTargetValue> targetValues;

    private KiePMMLTarget(String name, List<KiePMMLExtension> extensions, TargetField targetField) {
        super(name, extensions);
        this.targetField = targetField;
        if (targetField.getTargetValues() != null) {
            targetValues = targetField.getTargetValues()
                    .stream()
                    .map(targetValue -> KiePMMLTargetValue.builder(UUID.randomUUID().toString(),
                                                                   Collections.emptyList(), targetValue)
                            .build())
                    .collect(Collectors.toList());
        } else {
            targetValues = Collections.emptyList();
        }
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions, TargetField targetField) {
        return new Builder(name, extensions, targetField);
    }

    public Object modifyPrediction(Object prediction) {
        if (!(prediction instanceof Number)) {
            // TODO DROOLS-6345 TargetValue currently unimplemented - only direct number operations allowed
            return prediction;
        }
        double predictionDouble = (double) prediction;
        Number toReturn = applyMin(predictionDouble);
        toReturn = applyMax((double) toReturn);
        toReturn = applyRescaleFactor((double)toReturn);
        toReturn = applyRescaleConstant((double)toReturn);
        toReturn = applyCastInteger((double)toReturn);
        // TODO DROOLS-6345 TargetValue currently unimplemented
        return toReturn;
    }

    Double applyMin(double predictionDouble) {
        return targetField.getMin() != null ? Math.max(targetField.getMin(), predictionDouble) : predictionDouble;
    }

    Double applyMax(double predictionDouble) {
        return targetField.getMax() != null ? Math.min(targetField.getMax(), predictionDouble) : predictionDouble;
    }

    Double applyRescaleFactor(double predictionDouble) {
        return predictionDouble * targetField.getRescaleFactor();
    }

    Double applyRescaleConstant(double predictionDouble) {
        return predictionDouble + targetField.getRescaleConstant();
    }

    Number applyCastInteger(double predictionDouble) {
        return targetField.getCastInteger() != null ? targetField.getCastInteger().getScaledValue(predictionDouble) :
                predictionDouble;
    }

    public String getField() {
        return targetField.getField();
    }

    public List<KiePMMLTargetValue> getTargetValues() {
        return Collections.unmodifiableList(targetValues);
    }

    public OP_TYPE getOpType() {
        return targetField.getOpType();
    }

    public CAST_INTEGER getCastInteger() {
        return targetField.getCastInteger();
    }

    public Double getMin() {
        return targetField.getMin();
    }

    public Double getMax() {
        return targetField.getMax();
    }

    public double getRescaleConstant() {
        return targetField.getRescaleConstant();
    }

    public double getRescaleFactor() {
        return targetField.getRescaleFactor();
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
        return Objects.equals(targetField, that.targetField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetField);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLTarget.class.getSimpleName() + "[", "]")
                .add("targetField=" + targetField)
                .add("name='" + name + "'")
                .add("extensions=" + extensions)
                .add("id='" + id + "'")
                .add("parentId='" + parentId + "'")
                .toString();
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLTarget> {

        private Builder(String name, List<KiePMMLExtension> extensions, TargetField targetField) {
            super("Target-", () -> new KiePMMLTarget(name, extensions, targetField));
        }
    }
}
