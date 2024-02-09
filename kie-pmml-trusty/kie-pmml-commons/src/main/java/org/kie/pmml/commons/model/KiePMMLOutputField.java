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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.commonEvaluate;

public class KiePMMLOutputField extends AbstractKiePMMLComponent {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLOutputField.class);
    private static final long serialVersionUID = 2408750585433339543L;
    private RESULT_FEATURE resultFeature = RESULT_FEATURE.PREDICTED_VALUE;
    private String targetField = null;
    private Integer rank;
    private DATA_TYPE dataType;
    private OP_TYPE opType;
    private Object value;
    private KiePMMLExpression kiePMMLExpression;

    private KiePMMLOutputField(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    static Optional<Object> getValueFromKiePMMLNameValuesByVariableName(final String variableName,
                                                                        final List<KiePMMLNameValue> kiePMMLNameValues) {
        return kiePMMLNameValues.stream()
                .filter(kiePMMLNameValue -> kiePMMLNameValue.getValue() != null &&
                        kiePMMLNameValue.getName().equals(variableName))
                .map(KiePMMLNameValue::getValue)
                .findFirst();
    }

    static Optional<Object> getValueFromPMMLResultByVariableName(final String variableName,
                                                                 final Map<String, Object> resultsVariables) {
        return Optional.ofNullable(resultsVariables.get(variableName));
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

    public KiePMMLExpression getKiePMMLExpression() {
        return kiePMMLExpression;
    }

    public DATA_TYPE getDataType() {
        return dataType;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    public Object evaluate(final ProcessingDTO processingDTO) {
        switch (resultFeature) {
            case PREDICTED_VALUE:
                return evaluatePredictedValue(processingDTO);
            case PROBABILITY:
                return evaluateProbabilityValue(processingDTO);
            case REASON_CODE:
                return evaluateReasonCodeValue(processingDTO);
            case TRANSFORMED_VALUE:
                return evaluateTransformedValue(processingDTO);
            case PREDICTED_DISPLAY_VALUE:
                return processingDTO.getPredictedDisplayValue();
            case ENTITY_ID:
            case CLUSTER_ID:
                return processingDTO.getEntityId();
            case AFFINITY:
            case ENTITY_AFFINITY:
            case CLUSTER_AFFINITY:
                return processingDTO.getAffinity();
            default:
                logger.warn("OutputField with feature \"{}\" is currently not implemented and will be ignored.", resultFeature.getName());
                return null;
        }
    }

    public Object evaluatePredictedValue(final ProcessingDTO processingDTO) {
        return commonEvaluate(getValueFromKiePMMLNameValuesByVariableName(targetField, processingDTO.getKiePMMLNameValues())
                                      .orElse(null), dataType);
    }

    public Object evaluateProbabilityValue(final ProcessingDTO processingDTO) {
        return processingDTO.getProbabilityMap() != null ? processingDTO.getProbabilityMap().get(value) : null;
    }

    public Object evaluateReasonCodeValue(final ProcessingDTO processingDTO) {
        final List<String> orderedReasonCodes = processingDTO.getOrderedReasonCodes();
        if (rank != null) {
            int index = rank - 1;
            String resultCode = null;
            if (index < orderedReasonCodes.size()) {
                resultCode = orderedReasonCodes.get(index);
            }
            return commonEvaluate(resultCode, dataType);
        } else {
            return null;
        }
    }

    public Object evaluateTransformedValue(final ProcessingDTO processingDTO) {
        Object toReturn = kiePMMLExpression != null ? kiePMMLExpression.evaluate(processingDTO) : null;
        return commonEvaluate(toReturn, dataType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLOutputField.class.getSimpleName() + "[", "]")
                .add("resultFeature=" + resultFeature)
                .add("targetField='" + targetField + "'")
                .add("rank=" + rank)
                .add("value=" + value)
                .add("name='" + name + "'")
                .add("kiePMMLExpression='" + kiePMMLExpression + "'")
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
            if (resultFeature != null) {
                toBuild.resultFeature = resultFeature;
            }
            return this;
        }

        public Builder withTargetField(String targetField) {
            if (targetField != null) {
                toBuild.targetField = targetField;
            }
            return this;
        }

        public Builder withValue(Object value) {
            toBuild.value = value;
            return this;
        }

        public Builder withDataType(DATA_TYPE dataType) {
            if (dataType != null) {
                toBuild.dataType = dataType;
            }
            return this;
        }

        public Builder withOpType(OP_TYPE opType) {
            if (opType != null) {
                toBuild.opType = opType;
            }
            return this;
        }

        public Builder withRank(Integer rank) {
            if (rank != null) {
                toBuild.rank = rank;
            }
            return this;
        }

        public Builder withKiePMMLExpression(KiePMMLExpression kiePMMLExpression) {
            if (kiePMMLExpression != null) {
                toBuild.kiePMMLExpression = kiePMMLExpression;
            }
            return this;
        }
    }
}
