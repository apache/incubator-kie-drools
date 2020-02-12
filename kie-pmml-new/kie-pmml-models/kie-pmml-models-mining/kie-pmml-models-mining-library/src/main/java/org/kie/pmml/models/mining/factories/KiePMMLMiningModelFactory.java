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
package org.kie.pmml.models.mining.factories;

import java.util.Optional;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.enums.MINING_FUNCTION;
import org.kie.pmml.models.mining.api.model.KiePMMLMiningModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.library.commons.utils.ModelUtils.getTargetField;
import static org.kie.pmml.models.mining.factories.KiePMMLSegmentationFactory.getSegmentation;

public class KiePMMLMiningModelFactory {

    private static final Logger log = LoggerFactory.getLogger(KiePMMLMiningModelFactory.class.getName());

    private KiePMMLMiningModelFactory() {
    }

    /**
     *
     * @param dataDictionary
     * @param model
     * @param kBuilder Using <code>Object</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLException
     */
    public static KiePMMLMiningModel getKiePMMLMiningModel(DataDictionary dataDictionary, MiningModel model, Object kBuilder) throws KiePMMLException {
        log.info("getKiePMMLModel {}", model);
        String name = model.getModelName();
        Optional<String> targetFieldName = getTargetField(model);
        return KiePMMLMiningModel.builder(name, MINING_FUNCTION.byName(model.getMiningFunction().value()))
                .withAlgorithmName(model.getAlgorithmName())
                .withScorable(model.isScorable())
                .withSegmentation(getSegmentation(model.getSegmentation(), dataDictionary, kBuilder))
                .withTargetField(targetFieldName.orElse(null))
                .build();
    }
}
