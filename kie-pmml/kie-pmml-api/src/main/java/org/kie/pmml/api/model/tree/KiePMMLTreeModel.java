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
package org.kie.pmml.api.model.tree;

import java.util.Objects;

import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.MINING_FUNCTION;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.regression.KiePMMLRegressionModel;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html>Tree</a>
 */
public class KiePMMLTreeModel extends KiePMMLModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.TREE_MODEL;

    private static final long serialVersionUID = 3107205976845585067L;

    private KiePMMLNode node;

    private String algorithmName;


    public KiePMMLNode getNode() {
        return node;
    }

    public static Builder builder(String name, MINING_FUNCTION miningFunction) {
        return new Builder(name, miningFunction);
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    @Override
    public String toString() {
        return "KiePMMLTreeModel{" +
                "node=" + node +
                ", algorithmName='" + algorithmName + '\'' +
                ", pmmlMODEL=" + pmmlMODEL +
                ", miningFunction=" + miningFunction +
                ", targetField='" + targetField + '\'' +
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
        KiePMMLTreeModel treeModel = (KiePMMLTreeModel) o;
        return Objects.equals(node, treeModel.node) &&
                Objects.equals(algorithmName, treeModel.algorithmName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), node, algorithmName);
    }

    public static class Builder extends KiePMMLModel.Builder<KiePMMLTreeModel> {

        private Builder(String name, MINING_FUNCTION miningFunction) {
            super(name, "TreeModel-", PMML_MODEL_TYPE, miningFunction, KiePMMLTreeModel::new);
        }


        public Builder withAlgorithmName(String algorithmName) {
            toBuild.algorithmName = algorithmName;
            return this;
        }

        public Builder withNode(KiePMMLNode node) {
            toBuild.node = node;
            return this;
        }

        @Override
        public Builder withTargetField(String targetField) {
            return (Builder) super.withTargetField(targetField);
        }

    }
}
