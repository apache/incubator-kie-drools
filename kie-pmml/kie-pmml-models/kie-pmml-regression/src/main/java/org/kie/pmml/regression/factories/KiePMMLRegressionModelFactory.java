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
package org.kie.pmml.regression.factories;

import java.util.logging.Logger;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.regression.RegressionModel;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.enums.MINING_FUNCTION;
import org.kie.pmml.api.model.regression.KiePMMLRegressionModel;
import org.kie.pmml.api.model.regression.enums.MODEL_TYPE;
import org.kie.pmml.api.model.regression.enums.REGRESSION_NORMALIZATION_METHOD;

import static org.kie.pmml.models.core.factories.KiePMMLDataFieldFactory.getKiePMMLDataFields;
import static org.kie.pmml.models.core.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.models.core.factories.KiePMMLMiningFieldFactory.getKiePMMLMiningFields;
import static org.kie.pmml.regression.factories.KiePMMLRegressionTableFactory.getRegressionTables;

public class KiePMMLRegressionModelFactory {

    private static final Logger log = Logger.getLogger(KiePMMLRegressionModelFactory.class.getName());

    public static KiePMMLRegressionModel getKiePMMLRegressionModel(DataDictionary dataDictionary, RegressionModel model) throws KiePMMLException {
        log.info("getKiePMMLModel " + dataDictionary + " " + model);
        // TODO {gcardosi} verify given name is correct
        String name = model.getModelName();
        return KiePMMLRegressionModel.builder(name)
                .withAlgorithmName(model.getAlgorithmName())
//                .withDataFields(getKiePMMLDataFields(dataDictionary.getDataFields()))
//                .withExtensions(getKiePMMLExtensions(model.getExtensions()))
//                .withMiningFields(getKiePMMLMiningFields(model.getMiningSchema().getMiningFields()))
                .withMiningFunction(MINING_FUNCTION.byName(model.getMiningFunction().value()))
                .withModelType(MODEL_TYPE.byName(model.getModelType().value()))
//                .withOutputFields()
                .withRegressionNormalizationMethod(REGRESSION_NORMALIZATION_METHOD.byName(model.getNormalizationMethod().value()))
                .withRegressionTables(getRegressionTables(model.getRegressionTables()))
                .withScorable(model.isScorable())
                .withTargetFieldName(model.getTargetField().getValue())
                .build();
    }



    private KiePMMLRegressionModelFactory() {
    }
}
