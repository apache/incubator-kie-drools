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
package org.kie.pmml.models.drools.tree.compiler.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.tree.TreeModel;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.drools.tree.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.getBaseDescr;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;
import static org.kie.pmml.models.drools.tree.compiler.factories.KiePMMLTreeModelASTFactory.getKiePMMLDroolsAST;

/**
 * Class used to generate <code>KiePMMLTreeModel</code> out of a <code>DataDictionary</code> and a <code>TreeModel</code>
 */
public class KiePMMLTreeModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelFactory.class.getName());

    private KiePMMLTreeModelFactory() {
    }

    public static KiePMMLTreeModel getKiePMMLTreeModel(DataDictionary dataDictionary, TreeModel model) {
        logger.trace("getKiePMMLTreeModel {}", model);
        String name = model.getModelName();
        Optional<String> targetFieldName = getTargetFieldName(dataDictionary, model);
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap);
        final PackageDescr baseDescr = getBaseDescr(kiePMMLDroolsAST, name.toLowerCase());
        return KiePMMLTreeModel.builder(name, Collections.emptyList(), MINING_FUNCTION.byName(model.getMiningFunction().value()), model.getAlgorithmName())
                .withPackageDescr(baseDescr)
                .withFieldTypeMap(fieldTypeMap)
                .withTargetField(targetFieldName.orElse(null))
                .build();
    }
}
