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
package org.kie.pmml.models.mining.compiler.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.mining.MiningModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.kie.dependencies.HasKnowledgeBuilder;
import org.kie.pmml.models.mining.compiler.dto.MiningModelCompilationDTO;
import org.kie.pmml.models.mining.compiler.factories.KiePMMLMiningModelFactory;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.kie.pmml.models.mining.model.KiePMMLMiningModelWithSources;

import static org.kie.pmml.commons.Constants.EXPECTING_HAS_KNOWLEDGEBUILDER_TEMPLATE;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLMiningModelFactory.getKiePMMLMiningModel;
import static org.kie.pmml.models.mining.model.KiePMMLMiningModel.PMML_MODEL_TYPE;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Mining</b>
 */
public class MiningModelImplementationProvider implements ModelImplementationProvider<MiningModel, KiePMMLMiningModel> {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL_TYPE;
    }

    @Override
    public KiePMMLMiningModel getKiePMMLModel(final CompilationDTO<MiningModel> compilationDTO) {
        if (!(compilationDTO.getHasClassloader() instanceof HasKnowledgeBuilder)) {
            throw new KiePMMLException(String.format(EXPECTING_HAS_KNOWLEDGEBUILDER_TEMPLATE,
                                                     compilationDTO.getHasClassloader().getClass().getName()));
        }
        return getKiePMMLMiningModel(MiningModelCompilationDTO.fromCompilationDTO(compilationDTO));
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<MiningModel> compilationDTO) {
        throw new KiePMMLException("MiningModelImplementationProvider.getSourcesMap is not meant to be invoked");
    }

    @Override
    public KiePMMLModelWithSources getKiePMMLModelWithSources(final CompilationDTO<MiningModel> compilationDTO) {
        if (!(compilationDTO.getHasClassloader() instanceof HasKnowledgeBuilder)) {
            throw new KiePMMLException(String.format(EXPECTING_HAS_KNOWLEDGEBUILDER_TEMPLATE,
                                                     compilationDTO.getHasClassloader().getClass().getName()));
        }
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final Map<String, String> sourcesMap =
                KiePMMLMiningModelFactory.getKiePMMLMiningModelSourcesMap(MiningModelCompilationDTO.fromCompilationDTO(compilationDTO),
                                                                          nestedModels);
        return new KiePMMLMiningModelWithSources(compilationDTO.getModelName(),
                                                 compilationDTO.getPackageName(),
                                                 compilationDTO.getKieMiningFields(),
                                                 compilationDTO.getKieOutputFields(),
                                                 compilationDTO.getKieTargetFields(),
                                                 sourcesMap,
                                                 nestedModels);
    }
}
