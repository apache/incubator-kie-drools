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
package  org.kie.pmml.models.naive_bayes.evaluator;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelExecutor;
import org.kie.pmml.models.naive_bayes.model.KiePMMLNaiveBayesModel;

/**
 * Default <code>PMMLModelExecutor</code> for <b>NaiveBayes</b>
 */
public class PMMLNaiveBayesModelEvaluator implements PMMLModelExecutor {

    @Override
    public PMML_MODEL getPMMLModelType(){
        return PMML_MODEL.NAIVEBAYES_MODEL;
    }

    @Override
    public PMML4Result evaluate(KiePMMLModel model, PMMLContext context, String releaseId) {
        if(!(model instanceof KiePMMLNaiveBayesModel)){
            throw new KiePMMLModelException("Expected a KiePMMLNaiveBayesModel, received a "+ model.getClass().getName());
        }
        // TODO
        throw new UnsupportedOperationException();
    }
}
