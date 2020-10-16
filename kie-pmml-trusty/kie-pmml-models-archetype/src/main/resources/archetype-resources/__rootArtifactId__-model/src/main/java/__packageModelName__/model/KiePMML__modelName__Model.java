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
package  ${package}.${packageModelName}.model;

import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;

public class KiePMML${modelName}Model extends KiePMMLModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.${modelNameUppercase}_MODEL;


    public static Builder builder(String name, MINING_FUNCTION miningFunction) {
        return new Builder(name, miningFunction);
    }

    public static PMML_MODEL getPmmlModelType() {
        return PMML_MODEL_TYPE;
    }

    private KiePMML${modelName}Model(String modelName) {
        super(modelName, Collections.emptyList());
    }

    @Override
    public Object evaluate(Map<String, Object> requestData) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public static class Builder extends KiePMMLModel.Builder<KiePMML${modelName}Model>{

        private Builder(String name,MINING_FUNCTION miningFunction){
            super("${modelName}-", PMML_MODEL_TYPE, miningFunction, () -> new KiePMML${modelName}(name));
        }

        @Override
        public Builder withTargetField(String targetField){
            return (Builder)super.withTargetField(targetField);
        }
    }

}
