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
package org.kie.pmml.models.tree.compiler.factories;

import java.util.Collections;
import java.util.Optional;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetField;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLDescrFactory.getBaseDescr;

public class KiePMMLTreeModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelFactory.class.getName());

    private KiePMMLTreeModelFactory() {
    }

    public static KiePMMLTreeModel getKiePMMLTreeModel(DataDictionary dataDictionary, TreeModel model) {
        logger.info("getKiePMMLModel {}", model);
        String name = model.getModelName();
        // TODO {gcardosi} convert DataDictionary "enum" values to a map of field-name/valid-values
        Optional<String> targetFieldName = getTargetField(dataDictionary, model);
        String packageName = "to.be.fixed"; // TODO {gcardosi} - how to retrieve/generate package name?
        return KiePMMLTreeModel.builder(name, Collections.emptyList(), MINING_FUNCTION.byName(model.getMiningFunction().value()), model.getAlgorithmName())
                .withPackageDescr(getBaseDescr(dataDictionary, model, packageName))
                .withTargetField(targetFieldName.orElse(null))
                .build();
    }
}
