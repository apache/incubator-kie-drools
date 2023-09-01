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
package org.kie.pmml.models.scorecard.compiler.executor;

import java.util.Map;

import org.dmg.pmml.scorecard.Scorecard;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.scorecard.compiler.ScorecardCompilationDTO;
import org.kie.pmml.models.scorecard.compiler.factories.KiePMMLScorecardModelFactory;
import org.kie.pmml.models.scorecard.model.KiePMMLScorecardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Scorecard</b>
 */
public class ScorecardModelImplementationProvider implements ModelImplementationProvider<Scorecard,
        KiePMMLScorecardModel> {

    private static final Logger logger = LoggerFactory.getLogger(ScorecardModelImplementationProvider.class.getName());

    @Override
    public PMML_MODEL getPMMLModelType() {
        logger.trace("getPMMLModelType");
        return PMML_MODEL.SCORECARD_MODEL;
    }

    @Override
    public Class<KiePMMLScorecardModel> getKiePMMLModelClass() {
        return KiePMMLScorecardModel.class;
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<Scorecard> compilationDTO) {
        logger.trace("getKiePMMLModelWithSources {} {} {} {}", compilationDTO.getPackageName(),
                     compilationDTO.getFields(),
                     compilationDTO.getModel(),
                     compilationDTO.getPmmlContext());
        try {
            ScorecardCompilationDTO scorecardCompilationDTO =
                    ScorecardCompilationDTO.fromCompilationDTO(compilationDTO);
            return
                    KiePMMLScorecardModelFactory.getKiePMMLScorecardModelSourcesMap(scorecardCompilationDTO);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }
}
