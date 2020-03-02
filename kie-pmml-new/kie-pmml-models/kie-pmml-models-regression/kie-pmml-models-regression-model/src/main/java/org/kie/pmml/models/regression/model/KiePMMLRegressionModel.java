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
package org.kie.pmml.models.regression.model;

import java.util.Map;

import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.regression.model.codegen.AbstractKiePMMLRegressionTable;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Regression.html>Regression</a>
 */
public abstract class KiePMMLRegressionModel extends KiePMMLModel {

    protected AbstractKiePMMLRegressionTable regressionTable;

    public KiePMMLRegressionModel(String modelName) {
        super(modelName);
    }

    @Override
    public Object evaluate(Map<String, Object> requestData) {
        Object toReturn = regressionTable.evaluateRegression(requestData);
        outputFieldsMap.putAll(regressionTable.getOutputFieldsMap());
        return toReturn;
    }

    //    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.REGRESSION_MODEL;
//
//    private final List<KiePMMLRegressionTable> regressionTables;
//
//    private REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod = REGRESSION_NORMALIZATION_METHOD.NONE;
//    private OP_TYPE targetOpType;
//    private boolean isScorable = true;
//    private String algorithmName = null;
//    private MODEL_TYPE modelType = null;
//    private List<Object> targetValues = null;
//    private List<KiePMMLOutputField> outputFields = null;
//
//    private KiePMMLRegressionModel(String name, List<KiePMMLExtension> extensions, List<KiePMMLRegressionTable> regressionTables) {
//        super(name, extensions);
//        this.regressionTables = regressionTables;
//    }
//
//    public static Builder builder(String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction, List<KiePMMLRegressionTable> regressionTables, OP_TYPE targetOpType) {
//        return new Builder(name, extensions, miningFunction, regressionTables, targetOpType);
//    }
//
//    public static PMML_MODEL getPmmlModelType() {
//        return PMML_MODEL_TYPE;
//    }
//
//    /**
//     * @return <b>unmodifiable</b> <code>List&lt;KiePMMLRegressionTable&gt;</code>
//     */
//    public List<KiePMMLRegressionTable> getRegressionTables() {
//        return Collections.unmodifiableList(regressionTables);
//    }
//
//    public Optional<String> getAlgorithmName() {
//        return Optional.ofNullable(algorithmName);
//    }
//
//    public Optional<MODEL_TYPE> getModelType() {
//        return Optional.ofNullable(modelType);
//    }
//
//    public OP_TYPE getTargetOpType() {
//        return targetOpType;
//    }
//
//    public REGRESSION_NORMALIZATION_METHOD getRegressionNormalizationMethod() {
//        return regressionNormalizationMethod;
//    }
//
//    public boolean isScorable() {
//        return isScorable;
//    }
//
//    /**
//     * @return <code>Optional</code> of <b>unmodifiable</b> <code>List&lt;Object&gt;</code>
//     */
//    public Optional<List<Object>> getTargetValues() {
//        return targetValues != null ? Optional.of(Collections.unmodifiableList(targetValues)) : Optional.empty();
//    }
//
//    /**
//     * @return <code>Optional</code> of <b>unmodifiable</b> <code>List&lt;KiePMMLOutputField&gt;</code>
//     */
//    public Optional<List<KiePMMLOutputField>> getOutputFields() {
//        return outputFields != null ? Optional.of(Collections.unmodifiableList(outputFields)) : Optional.empty();
//    }
//
//    public boolean isRegression() {
//        return Objects.equals(MINING_FUNCTION.REGRESSION, miningFunction) && (targetField == null || Objects.equals(OP_TYPE.CONTINUOUS, targetOpType));
//    }
//
//    public boolean isBinary() {
//        return Objects.equals(OP_TYPE.CATEGORICAL, targetOpType) && (targetValues != null && targetValues.size() == 2);
//    }
//
//    @Override
//    public String toString() {
//        return "KiePMMLRegressionModel{" +
//                "regressionTables=" + regressionTables +
//                ", algorithmName='" + algorithmName + '\'' +
//                ", modelType=" + modelType +
//                ", targetOpType=" + targetOpType +
//                ", regressionNormalizationMethod=" + regressionNormalizationMethod +
//                ", isScorable=" + isScorable +
//                ", targetValues=" + targetValues +
//                ", outputFields=" + outputFields +
//                ", pmmlMODEL=" + pmmlMODEL +
//                ", miningFunction=" + miningFunction +
//                ", targetField='" + targetField + '\'' +
//                ", name='" + name + '\'' +
//                ", id='" + id + '\'' +
//                ", parentId='" + parentId + '\'' +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        if (!super.equals(o)) {
//            return false;
//        }
//        KiePMMLRegressionModel that = (KiePMMLRegressionModel) o;
//        return isScorable == that.isScorable &&
//                Objects.equals(regressionTables, that.regressionTables) &&
//                Objects.equals(algorithmName, that.algorithmName) &&
//                modelType == that.modelType &&
//                targetOpType == that.targetOpType &&
//                regressionNormalizationMethod == that.regressionNormalizationMethod &&
//                Objects.equals(targetValues, that.targetValues) &&
//                Objects.equals(outputFields, that.outputFields);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(super.hashCode(), regressionTables, miningFunction, algorithmName, modelType, targetField, targetOpType, regressionNormalizationMethod, isScorable);
//    }
//
//    public static class Builder extends KiePMMLModel.Builder<KiePMMLRegressionModel> {
//
//        private Builder(String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction, List<KiePMMLRegressionTable> regressionTables, OP_TYPE targetOpType) {
//            super("RegressionModel-", PMML_MODEL_TYPE, miningFunction, () -> new KiePMMLRegressionModel(name, extensions, regressionTables));
//            toBuild.targetOpType = targetOpType;
//        }
//
//        public Builder withAlgorithmName(String algorithmName) {
//            toBuild.algorithmName = algorithmName;
//            return this;
//        }
//
//        public Builder withModelType(MODEL_TYPE modelType) {
//            toBuild.modelType = modelType;
//            return this;
//        }
//
//        public Builder withTargetValues(List<Object> targetValues) {
//            toBuild.targetValues = targetValues;
//            return this;
//        }
//
//        public Builder withRegressionNormalizationMethod(REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod) {
//            toBuild.regressionNormalizationMethod = regressionNormalizationMethod;
//            return this;
//        }
//
//        public Builder withScorable(boolean scorable) {
//            toBuild.isScorable = scorable;
//            return this;
//        }
//
//        public Builder withOutputFields(List<KiePMMLOutputField> outputFields) {
//            toBuild.outputFields = outputFields;
//            return this;
//        }
//
//        @Override
//        public Builder withTargetField(String targetField) {
//            return (Builder) super.withTargetField(targetField);
//        }
//    }
}
