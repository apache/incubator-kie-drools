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
package  org.kie.pmml.models.naive_bayes.model;

import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;

public class KiePMMLNaiveBayesModel extends KiePMMLModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.NAIVEBAYES_MODEL;


    public static Builder builder(String name, MINING_FUNCTION miningFunction) {
        return new Builder(name, miningFunction);
    }

    public static PMML_MODEL getPmmlModelType() {
        return PMML_MODEL_TYPE;
    }

    private KiePMMLNaiveBayesModel() {
    }


    public static class Builder extends KiePMMLModel.Builder<KiePMMLNaiveBayesModel>{

        private Builder(String name,MINING_FUNCTION miningFunction){
            super(name, "NaiveBayes-", PMML_MODEL_TYPE, miningFunction, KiePMMLNaiveBayesModel::new);
        }

        @Override
        public Builder withTargetField(String targetField){
            return (Builder)super.withTargetField(targetField);
        }
    }

}
