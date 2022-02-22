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
package org.kie.pmml.compiler.api.provider;

import java.util.Map;

import org.dmg.pmml.Model;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.api.dto.CompilationDTO;

/**
 * API for actual PMML model implementations
 */
public interface ModelImplementationProvider<T extends Model, E extends KiePMMLModel> {

    PMML_MODEL getPMMLModelType();

    /**
     * Method to be called for a <b>runtime</b> compilation
     * @param compilationDTO
     * @return
     * @throws KiePMMLInternalException
     */
    E getKiePMMLModel(final CompilationDTO<T> compilationDTO);

    /**
     * Method to be called following a <b>kie-maven-plugin</b> invocation
     * @param compilationDTO
     * @return
     * @throws KiePMMLInternalException
     */
    default KiePMMLModelWithSources getKiePMMLModelWithSources(final CompilationDTO<T> compilationDTO) {
        final Map<String, String> sourcesMap = getSourcesMap(compilationDTO);
        return new KiePMMLModelWithSources(compilationDTO.getModelName(),
                                           compilationDTO.getPackageName(),
                                           compilationDTO.getKieMiningFields(),
                                           compilationDTO.getKieOutputFields(),
                                           compilationDTO.getKieTargetFields(),
                                           sourcesMap,
                                           this.isInterpreted());
    }

    Map<String, String> getSourcesMap(final CompilationDTO<T> compilationDTO);

    /**
     * Method provided only to have <b>drools</b> models working when invoked by a <code>MiningModel</code>
     * Default implementation provided for <b>not-drools</b> models.
     * @param compilationDTO
     * @return
     * @throws KiePMMLInternalException
     */
    default KiePMMLModelWithSources getKiePMMLModelWithSourcesCompiled(final CompilationDTO<T> compilationDTO) {
        KiePMMLModelWithSources toReturn = getKiePMMLModelWithSources(compilationDTO);
        final Map<String, String> sourcesMap = ((HasSourcesMap) toReturn).getSourcesMap();
        try {
            compilationDTO.compileAndLoadClass(sourcesMap);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
        return toReturn;
    }

    default boolean isInterpreted() {
        return false;
    }
}
