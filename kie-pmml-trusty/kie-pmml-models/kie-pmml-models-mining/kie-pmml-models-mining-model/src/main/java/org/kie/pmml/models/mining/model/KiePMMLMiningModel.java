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
package org.kie.pmml.models.mining.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegment;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegmentation;

/**
 * @see <a href=http://dmg.org/pmml/v4-3/MultipleModels.html>MiningModel</a>
 */
public class KiePMMLMiningModel extends KiePMMLModel implements HasNestedModels {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.MINING_MODEL;
    private static final long serialVersionUID = 1074200573309922605L;

    protected String algorithmName;
    protected boolean scorable = true;
    protected KiePMMLSegmentation segmentation;

    protected KiePMMLMiningModel(String fileName, String name, List<KiePMMLExtension> extensions) {
        super(fileName,  name, extensions);
    }

    public static Builder builder(String fileName, String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
        return new Builder(fileName, name, extensions, miningFunction);
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData, final PMMLRuntimeContext pmmlContext) {
        throw new KiePMMLException("KiePMMLMiningModel is not meant to be used for actual evaluation");
    }

    @Override
    public List<KiePMMLModel> getNestedModels() {
        return getSegmentation().getSegments().stream().map(KiePMMLSegment::getModel).collect(Collectors.toList());
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
        return "KiePMMLMiningModel{" + "miningFunction=" + miningFunction + ", algorithmName='" + algorithmName + '\'' + ", scorable=" + scorable + ", segmentation=" + segmentation + ", pmmlMODEL=" + pmmlMODEL + ", name='" + name + '\'' + ", id='" + id + '\'' + ", parentId='" + parentId + '\'' + '}';
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

        private Builder(String fileName, String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
            super("MiningModel-", PMML_MODEL_TYPE, miningFunction, () -> new KiePMMLMiningModel(fileName, name, extensions));
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