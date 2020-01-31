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
package org.kie.pmml.models.tree.factories;

import java.util.Optional;
import java.util.logging.Logger;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.enums.MINING_FUNCTION;
import org.kie.pmml.api.model.tree.KiePMMLTreeModel;

import static org.kie.pmml.models.tree.factories.KiePMMLNodeFactory.getNode;

public class KiePMMLTreeModelFactory {

    private static final Logger log = Logger.getLogger(KiePMMLTreeModelFactory.class.getName());

    private KiePMMLTreeModelFactory() {
    }

    public static KiePMMLTreeModel getKiePMMLTreeModel(DataDictionary dataDictionary, TreeModel model) throws KiePMMLException {
        log.info("getKiePMMLModel " + model);
        String name = model.getModelName();
        // TODO {gcardosi} convert DataDictionary "enum" values to a map of field-name/valid-values
        Optional<String> targetFieldName = model.getMiningSchema().getMiningFields().stream()
                .filter(miningField -> MiningField.UsageType.TARGET.equals(miningField.getUsageType()))
                .map(miningField -> miningField.getName().getValue())
                .findFirst();
        return KiePMMLTreeModel.builder(name, MINING_FUNCTION.byName(model.getMiningFunction().value()))
                .withAlgorithmName(model.getAlgorithmName())
                .withNode(getNode(model.getNode(), dataDictionary))
                .withTargetFieldName(targetFieldName.orElse(null))
                .build();
    }
}
