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
package org.kie.pmml.models.tree.api.model;

import java.util.Objects;

import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.pmml.api.model.KiePMMLDrooledModel;
import org.kie.pmml.api.model.enums.MINING_FUNCTION;
import org.kie.pmml.api.model.enums.PMML_MODEL;

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



    public static class Builder extends KiePMMLDrooledModel.Builder<KiePMMLTreeModel> {

        private Builder(String name, MINING_FUNCTION miningFunction) {
            super(name, "TreeModel-", PMML_MODEL_TYPE, miningFunction, KiePMMLTreeModel::new);
        }


        public Builder withAlgorithmName(String algorithmName) {
            toBuild.algorithmName = algorithmName;
            return this;
        }

        @Override
        public Builder withTargetField(String targetField) {
            return (Builder) super.withTargetField(targetField);
        }

        @Override
        public Builder withPackageDescr(PackageDescr packageDescr) {
            return (Builder) super.withPackageDescr(packageDescr);
        }


    }
}
