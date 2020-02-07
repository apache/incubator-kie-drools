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
package org.kie.pmml.runtime.regression.executor;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.regression.KiePMMLRegressionModel;
import org.kie.pmml.runtime.api.exceptions.KiePMMLModelException;
import org.kie.pmml.runtime.api.executor.PMMLContext;
import org.kie.pmml.runtime.core.executor.PMMLModelExecutor;

import static org.kie.pmml.runtime.regression.executor.PMMLIsNotRegresssionModelExecutor.evaluateNotRegression;
import static org.kie.pmml.runtime.regression.executor.PMMLIsRegresssionModelExecutor.evaluateRegression;

public class PMMLRegressionModelExecutor implements PMMLModelExecutor {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.REGRESSION_MODEL;
    }

    @Override
    public PMML4Result evaluate(KiePMMLModel model, PMMLContext pmmlContext) throws KiePMMLException {
        if (!(model instanceof KiePMMLRegressionModel)) {
            throw new KiePMMLModelException("Expected a KiePMMLRegressionModel, received a " + model.getClass().getName());
        }
        final KiePMMLRegressionModel regressionModel = (KiePMMLRegressionModel) model;
        return (regressionModel).isRegression() ? evaluateRegression(regressionModel, pmmlContext) : evaluateNotRegression(regressionModel, pmmlContext);
    }


}
