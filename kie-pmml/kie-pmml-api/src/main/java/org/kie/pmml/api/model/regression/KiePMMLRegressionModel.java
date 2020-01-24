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
package org.kie.pmml.api.model.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.MINING_FUNCTION;
import org.kie.pmml.api.model.enums.OP_TYPE;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.regression.enums.MODEL_TYPE;
import org.kie.pmml.api.model.regression.enums.REGRESSION_NORMALIZATION_METHOD;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Regression.html>Regression</a>
 */
public class KiePMMLRegressionModel extends KiePMMLModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.REGRESSION_MODEL;
    private static final long serialVersionUID = 2690863539104500649L;

    private List<KiePMMLRegressionTable> regressionTables = new ArrayList<>();

    private final MINING_FUNCTION miningFunction;
    private String algorithmName;
    private MODEL_TYPE modelType;
    private String targetFieldName;
    private OP_TYPE targetOpType;
    private REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod = null;
    private boolean isScorable = true;

    public static Builder builder(String name, MINING_FUNCTION miningFunction) {
        return new Builder(name, miningFunction);
    }

    public static PMML_MODEL getPmmlModelType() {
        return PMML_MODEL_TYPE;
    }

    public List<KiePMMLRegressionTable> getRegressionTables() {
        return regressionTables;
    }

    public MINING_FUNCTION getMiningFunction() {
        return miningFunction;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public MODEL_TYPE getModelType() {
        return modelType;
    }

    public String getTargetFieldName() {
        return targetFieldName;
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

    public boolean isRegression() {
        return Objects.equals(MINING_FUNCTION.REGRESSION, miningFunction) && (targetFieldName == null || Objects.equals(OP_TYPE.CONTINUOUS, targetOpType));
    }

    @Override
    public String toString() {
        return "KiePMMLRegressionModel{" +
                "regressionTables=" + regressionTables +
                ", miningFunction=" + miningFunction +
                ", algorithmName='" + algorithmName + '\'' +
                ", modelType=" + modelType +
                ", targetFieldName='" + targetFieldName + '\'' +
                ", targetOpType=" + targetOpType +
                ", regressionNormalizationMethod=" + regressionNormalizationMethod +
                ", isScorable=" + isScorable +
                '}';
    }

    private KiePMMLRegressionModel(String modelName, MINING_FUNCTION miningFunction) {
        super(modelName, PMML_MODEL_TYPE);
        this.miningFunction = miningFunction;
    }

    public static class Builder {

        private KiePMMLRegressionModel toBuild;

        private Builder(String name, MINING_FUNCTION miningFunction) {
            this.toBuild = new KiePMMLRegressionModel(name, miningFunction);
        }

        public KiePMMLRegressionModel build() {
            return toBuild;
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

        public Builder withTargetFieldName(String targetFieldName) {
            toBuild.targetFieldName = targetFieldName;
            return this;
        }

        public Builder withTargetOpType(OP_TYPE targetOpType) {
            toBuild.targetOpType = targetOpType;
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
    }
}
