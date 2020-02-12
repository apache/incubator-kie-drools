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
package org.kie.pmml.models.regression.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.models.regression.api.model.enums.MODEL_TYPE;
import org.kie.pmml.models.regression.api.model.enums.REGRESSION_NORMALIZATION_METHOD;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Regression.html>Regression</a>
 */
public class KiePMMLRegressionModel extends KiePMMLModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.REGRESSION_MODEL;
    private static final long serialVersionUID = 2690863539104500649L;

    private List<KiePMMLRegressionTable> regressionTables = new ArrayList<>();

    private String algorithmName;
    private MODEL_TYPE modelType;
    private OP_TYPE targetOpType;
    private REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod = null;
    private boolean isScorable = true;
    private List<Serializable> targetValues;

    public static Builder builder(String name, MINING_FUNCTION miningFunction) {
        return new Builder(name, miningFunction);
    }

    public static PMML_MODEL getPmmlModelType() {
        return PMML_MODEL_TYPE;
    }

    public List<KiePMMLRegressionTable> getRegressionTables() {
        return regressionTables;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public MODEL_TYPE getModelType() {
        return modelType;
    }

    public OP_TYPE getTargetOpType() {
        return targetOpType;
    }

    public REGRESSION_NORMALIZATION_METHOD getRegressionNormalizationMethod() {
        return regressionNormalizationMethod;
    }

    public boolean isScorable() {
        return isScorable;
    }

    public List<Serializable> getTargetValues() {
        return targetValues;
    }

    public boolean isRegression() {
        return Objects.equals(MINING_FUNCTION.REGRESSION, miningFunction) && (targetField == null || Objects.equals(OP_TYPE.CONTINUOUS, targetOpType));
    }

    public boolean isBinary() {
        return Objects.equals(OP_TYPE.CATEGORICAL, targetOpType) && (targetValues != null && targetValues.size() == 2);
    }

    @Override
    public String toString() {
        return "KiePMMLRegressionModel{" +
                "regressionTables=" + regressionTables +
                ", miningFunction=" + miningFunction +
                ", algorithmName='" + algorithmName + '\'' +
                ", modelType=" + modelType +
                ", targetField='" + targetField + '\'' +
                ", targetOpType=" + targetOpType +
                ", regressionNormalizationMethod=" + regressionNormalizationMethod +
                ", isScorable=" + isScorable +
                ", pmmlMODEL=" + pmmlMODEL +
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
        KiePMMLRegressionModel that = (KiePMMLRegressionModel) o;
        return isScorable == that.isScorable &&
                Objects.equals(regressionTables, that.regressionTables) &&
                miningFunction == that.miningFunction &&
                Objects.equals(algorithmName, that.algorithmName) &&
                modelType == that.modelType &&
                Objects.equals(targetField, that.targetField) &&
                targetOpType == that.targetOpType &&
                regressionNormalizationMethod == that.regressionNormalizationMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), regressionTables, miningFunction, algorithmName, modelType, targetField, targetOpType, regressionNormalizationMethod, isScorable);
    }

    protected KiePMMLRegressionModel() {
    }

    public static class Builder extends KiePMMLModel.Builder<KiePMMLRegressionModel> {

        private Builder(String name, MINING_FUNCTION miningFunction) {
            super(name, "RegressionModel-", PMML_MODEL_TYPE, miningFunction, KiePMMLRegressionModel::new);
        }

        public Builder withRegressionTables(List<KiePMMLRegressionTable> regressionTables) {
            toBuild.regressionTables = regressionTables;
            return this;
        }

        public Builder withAlgorithmName(String algorithmName) {
            toBuild.algorithmName = algorithmName;
            return this;
        }

        public Builder withModelType(MODEL_TYPE modelType) {
            toBuild.modelType = modelType;
            return this;
        }

        public Builder withTargetOpType(OP_TYPE targetOpType) {
            toBuild.targetOpType = targetOpType;
            return this;
        }

        public Builder withTargetValues(List<Serializable> targetValues) {
            toBuild.targetValues = targetValues;
            return this;
        }

        public Builder withRegressionNormalizationMethod(REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod) {
            toBuild.regressionNormalizationMethod = regressionNormalizationMethod;
            return this;
        }

        public Builder withScorable(boolean scorable) {
            toBuild.isScorable = scorable;
            return this;
        }

        @Override
        public Builder withTargetField(String targetField) {
            return (Builder) super.withTargetField(targetField);
        }
    }
}
