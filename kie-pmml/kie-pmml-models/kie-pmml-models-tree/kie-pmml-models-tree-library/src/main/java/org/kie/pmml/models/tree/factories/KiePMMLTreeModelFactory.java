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

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.enums.MINING_FUNCTION;
import org.kie.pmml.models.tree.api.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.library.commons.utils.ModelUtils.getTargetField;
import static org.kie.pmml.models.tree.factories.KiePMMLDescrFactory.getBaseDescr;

public class KiePMMLTreeModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelFactory.class.getName());

    private KiePMMLTreeModelFactory() {
    }

    public static KiePMMLTreeModel getKiePMMLTreeModel(DataDictionary dataDictionary, TreeModel model) throws KiePMMLException {
        logger.info("getKiePMMLModel {}",  model);
        String name = model.getModelName();
        // TODO {gcardosi} convert DataDictionary "enum" values to a map of field-name/valid-values
        Optional<String> targetFieldName = getTargetField(model);
        String packageName = "to.be.fixed"; // TODO {gcardosi} - how to retrieve/generate package name?
        return KiePMMLTreeModel.builder(name, MINING_FUNCTION.byName(model.getMiningFunction().value()))
                .withAlgorithmName(model.getAlgorithmName())
                .withContent(getBaseDescr(dataDictionary, model, packageName))
                .withTargetField(targetFieldName.orElse(null))
                .build();
    }
}
