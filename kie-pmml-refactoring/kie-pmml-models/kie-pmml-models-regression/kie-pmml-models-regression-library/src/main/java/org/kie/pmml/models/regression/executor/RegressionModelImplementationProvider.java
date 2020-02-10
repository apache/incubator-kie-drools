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
package org.kie.pmml.models.regression.executor;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.regression.RegressionModel;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.library.api.implementations.ModelImplementationProvider;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.factories.KiePMMLRegressionModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel.PMML_MODEL_TYPE;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Regression</b>
 */
public class RegressionModelImplementationProvider implements ModelImplementationProvider<RegressionModel, KiePMMLRegressionModel> {

    private static final Logger log = LoggerFactory.getLogger(RegressionModelImplementationProvider.class.getName());

    @Override
    public PMML_MODEL getPMMLModelType() {
        log.info("getPMMLModelType");
        return PMML_MODEL_TYPE;
    }

    @Override
    public KiePMMLRegressionModel getKiePMMLModel(DataDictionary dataDictionary, RegressionModel model, Object kBuilder) throws KiePMMLException {
        log.info("getKiePMMLModel {} {}", dataDictionary, model);
        return KiePMMLRegressionModelFactory.getKiePMMLRegressionModel(dataDictionary, model);
    }
}
