import java.util.Objects;

#set($symbol_pound='#')
        #set($symbol_dollar='$')
        #set($symbol_escape='\' )
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
        package ${package}.models.tree.api.model;
        {package}.api.model.KiePMMLDrooledModel;
        {package}.api.model.enums.MINING_FUNCTION;{package}.api.model.enums.PMML_MODEL;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html>Tree</a>
 */
public class KiePMMLTreeModel extends KiePMMLDrooledModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.TREE_MODEL;

    private static final long serialVersionUID = 3107205976845585067L;

    private String algorithmName;

    public static Builder builder(String name, MINING_FUNCTION miningFunction) {
        return new Builder(name, miningFunction);
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    @Override
    public String toString() {
        return "KiePMMLTreeModel{" +
                "content=" + droolContent +
                ", algorithmName='" + algorithmName + '${symbol_escape}' ' +
        ", pmmlMODEL=" + pmmlMODEL +
                ", miningFunction=" + miningFunction +
                ", targetField='" + targetField + '${symbol_escape}' ' +
        ", name='" + name + '${symbol_escape}' ' +
        ", id='" + id + '${symbol_escape}' ' +
        ", parentId='" + parentId + '${symbol_escape}' ' +
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
        return Objects.equals(droolContent, treeModel.droolContent) &&
                Objects.equals(algorithmName, treeModel.algorithmName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), droolContent, algorithmName);
    }

    public static class Builder extends KiePMMLDrooledModel.Builder<KiePMMLTreeModel> {

        private Builder(String name, MINING_FUNCTION miningFunction) {
            super(name, "TreeModel-", PMML_MODEL_TYPE, miningFunction, KiePMMLTreeModel::new);
        }

        public Builder withAlgorithmName(String algorithmName) {
            toBuild.algorithmName = algorithmName;
            return this;
        }

        @Override
        public Builder withContent(Object content) {
            return (Builder) super.withContent(content);
        }

        @Override
        public Builder withTargetField(String targetField) {
            return (Builder) super.withTargetField(targetField);
        }
    }
}
