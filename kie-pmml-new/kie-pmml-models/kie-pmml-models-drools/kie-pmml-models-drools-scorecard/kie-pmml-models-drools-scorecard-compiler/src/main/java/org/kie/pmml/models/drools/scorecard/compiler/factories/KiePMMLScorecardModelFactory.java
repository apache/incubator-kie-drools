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
package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.scorecard.Scorecard;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.scorecard.model.KiePMMLScorecardModel;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.commons.factories.KiePMMLOutputFieldFactory.getOutputFields;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.getBaseDescr;
import static org.kie.pmml.models.drools.scorecard.compiler.factories.KiePMMLScorecardModelASTFactory.getKiePMMLDroolsAST;

/**
 * Class used to generate <code>KiePMMLScorecard</code> out of a <code>DataDictionary</code> and a <code>ScorecardModel</code>
 */
public class KiePMMLScorecardModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLScorecardModelFactory.class.getName());

    private KiePMMLScorecardModelFactory() {
        // Avoid instantiation
    }

    public static KiePMMLScorecardModel getKiePMMLScorecardModel(DataDictionary dataDictionary, Scorecard model) {
        logger.trace("getKiePMMLScorecardModel {}", model);
        String name = model.getModelName();
        Optional<String> targetFieldName = getTargetFieldName(dataDictionary, model);
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap);
        String packageName = name.replace(" ", "_").toLowerCase();
        final PackageDescr baseDescr = getBaseDescr(kiePMMLDroolsAST, packageName);
        final List<KiePMMLOutputField> outputFields = getOutputFields(model);
        return KiePMMLScorecardModel.builder(name, Collections.emptyList(), MINING_FUNCTION.byName(model.getMiningFunction().value()))
                .withOutputFields(outputFields)
                .withPackageDescr(baseDescr)
                .withFieldTypeMap(fieldTypeMap)
                .withTargetField(targetFieldName.orElse(null))
                .build();
    }
}
