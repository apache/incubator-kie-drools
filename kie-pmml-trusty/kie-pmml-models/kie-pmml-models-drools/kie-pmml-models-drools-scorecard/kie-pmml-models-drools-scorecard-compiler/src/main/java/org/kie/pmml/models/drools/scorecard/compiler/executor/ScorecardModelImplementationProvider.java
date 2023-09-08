/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.drools.scorecard.compiler.executor;

import java.util.List;
import java.util.Map;

import org.dmg.pmml.Field;
import org.dmg.pmml.scorecard.Scorecard;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.dto.DroolsCompilationDTO;
import org.kie.pmml.models.drools.provider.DroolsModelProvider;
import org.kie.pmml.models.drools.scorecard.compiler.factories.KiePMMLScorecardModelFactory;
import org.kie.pmml.models.drools.scorecard.model.KiePMMLScorecardModel;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.kie.pmml.models.drools.scorecard.model.KiePMMLScorecardModel.PMML_MODEL_TYPE;

/**
 * Default <code>DroolsModelProvider</code> for <b>Scorecard</b>
 */
public class ScorecardModelImplementationProvider extends DroolsModelProvider<Scorecard, KiePMMLScorecardModel> {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL_TYPE;
    }

    @Override
    public Class<KiePMMLScorecardModel> getKiePMMLModelClass() {
        return KiePMMLScorecardModel.class;
    }

    @Override
    public KiePMMLDroolsAST getKiePMMLDroolsAST(final List<Field<?>> fields,
                                                final Scorecard model,
                                                final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                final List<KiePMMLDroolsType> types) {
        return KiePMMLScorecardModelFactory.getKiePMMLDroolsAST(fields, model, fieldTypeMap, types);
    }

    @Override
    public Map<String, String> getKiePMMLDroolsModelSourcesMap(final DroolsCompilationDTO<Scorecard> compilationDTO) {
        return KiePMMLScorecardModelFactory.getKiePMMLScorecardModelSourcesMap(compilationDTO);
    }
}
