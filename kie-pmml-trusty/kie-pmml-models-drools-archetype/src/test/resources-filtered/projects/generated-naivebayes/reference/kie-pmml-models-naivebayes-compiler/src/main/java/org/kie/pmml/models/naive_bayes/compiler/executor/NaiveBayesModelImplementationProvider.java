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
package org.kie.pmml.models.naive_bayes.compiler.executor;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.naive_bayes.NaiveBayesModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.naive_bayes.compiler.factories.KiePMMLNaiveBayesModelFactory;
import org.kie.pmml.models.naive_bayes.model.KiePMMLNaiveBayesModel;

import static org.kie.pmml.models.naive_bayes.model.KiePMMLNaiveBayesModel.PMML_MODEL_TYPE;

/**
 * Default <code>ModelImplementationProvider</code> for <b>NaiveBayes</b>
 */
public class NaiveBayesModelImplementationProvider implements ModelImplementationProvider<NaiveBayesModel,KiePMMLNaiveBayesModel>{

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL_TYPE;
    }

    @Override
    public KiePMMLNaiveBayesModel getKiePMMLModel(DataDictionary dataDictionary,NaiveBayesModel model, Object kBuilder) {
        return KiePMMLNaiveBayesModelFactory.getKiePMMLNaiveBayesModel(dataDictionary, model);
    }
}
