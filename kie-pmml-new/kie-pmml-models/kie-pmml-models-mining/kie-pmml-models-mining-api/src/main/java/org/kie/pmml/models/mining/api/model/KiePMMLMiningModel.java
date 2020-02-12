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
package org.kie.pmml.models.mining.api.model;

import java.util.Objects;

import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.models.mining.api.model.segmentation.KiePMMLSegmentation;

/**
 * @see <a href=http://dmg.org/pmml/v4-3/MultipleModels.html>MiningModel</a>
 */
public class KiePMMLMiningModel extends KiePMMLModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.MINING_MODEL;

    private static final long serialVersionUID = -6388156831035274833L;

    private String algorithmName;
    private boolean scorable = true;
    private KiePMMLSegmentation segmentation;

    public static Builder builder(String name, MINING_FUNCTION miningFunction) {
        return new Builder(name, miningFunction);
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public boolean isScorable() {
        return scorable;
    }

    public KiePMMLSegmentation getSegmentation() {
        return segmentation;
    }

    @Override
    public String toString() {
        return "KiePMMLMiningModel{" +
                "miningFunction=" + miningFunction +
                ", algorithmName='" + algorithmName + '\'' +
                ", scorable=" + scorable +
                ", segmentation=" + segmentation +
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

        KiePMMLMiningModel that = (KiePMMLMiningModel) o;

        if (scorable != that.scorable) {
            return false;
        }
        if (miningFunction != that.miningFunction) {
            return false;
        }
        if (!Objects.equals(algorithmName, that.algorithmName)) {
            return false;
        }
        return Objects.equals(segmentation, that.segmentation);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (miningFunction != null ? miningFunction.hashCode() : 0);
        result = 31 * result + (algorithmName != null ? algorithmName.hashCode() : 0);
        result = 31 * result + (scorable ? 1 : 0);
        result = 31 * result + (segmentation != null ? segmentation.hashCode() : 0);
        return result;
    }

    public static class Builder extends KiePMMLModel.Builder<KiePMMLMiningModel> {

        private Builder(String name, MINING_FUNCTION miningFunction) {
            super(name, "MiningModel-", PMML_MODEL_TYPE, miningFunction, KiePMMLMiningModel::new);
        }

        public Builder withAlgorithmName(String algorithmName) {
            toBuild.algorithmName = algorithmName;
            return this;
        }

        public Builder withScorable(boolean scorable) {
            toBuild.scorable = scorable;
            return this;
        }

        public Builder withSegmentation(KiePMMLSegmentation segmentation) {
            toBuild.segmentation = segmentation;
            return this;
        }

        @Override
        public Builder withTargetField(String targetField) {
            return (Builder) super.withTargetField(targetField);
        }
    }
}
