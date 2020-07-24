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
package org.kie.pmml.compiler.commons.mocks;

import java.util.Collections;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.regression.RegressionModel;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;

import static org.kie.pmml.compiler.commons.mocks.KiePMMLTestingModel.PMML_MODEL_TYPE;

/**
 * <b>Fake</b> <code>ModelImplementationProvider</code> used for testing. It is mapped to <code>PMML_MODEL.REGRESSION_MODEL</code>
 */
public class TestingModelImplementationProvider implements ModelImplementationProvider<RegressionModel, KiePMMLTestingModel> {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL_TYPE;
    }

    @Override
    public KiePMMLTestingModel getKiePMMLModel(final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final RegressionModel model, Object kBuilder) {
        return new KiePMMLTestingModel("TEST_MODEL", Collections.emptyList());
    }

    @Override
    public KiePMMLTestingModel getKiePMMLModelFromPlugin(String packageName, final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final RegressionModel model, Object kBuilder) {
        return getKiePMMLModel(dataDictionary, transformationDictionary, model, kBuilder);
    }
}
