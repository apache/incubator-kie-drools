/*
 * Copyright ${year} Red Hat, Inc. and/or its affiliates.
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
package  org.kie.pmml.models.tree.model;

import java.util.List;
import java.util.Map;

import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;

public class KiePMMLTreeModel extends KiePMMLDroolsModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.TREE_MODEL;

    protected KiePMMLTreeModel(String modelName, List<KiePMMLExtension> extensions) {
        super(modelName, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
        return new Builder(name, extensions, miningFunction);
    }

    public static PMML_MODEL getPmmlModelType() {
        return PMML_MODEL_TYPE;
    }

    public static class Builder extends KiePMMLDroolsModel.Builder<KiePMMLTreeModel>{

        private Builder(String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction){
            super("Tree-", PMML_MODEL_TYPE, miningFunction, () -> new KiePMMLTreeModel(name, extensions));
        }
    }

}
