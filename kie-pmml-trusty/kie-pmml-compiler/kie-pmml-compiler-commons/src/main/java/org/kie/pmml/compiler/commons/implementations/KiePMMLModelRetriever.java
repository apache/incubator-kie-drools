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
package org.kie.pmml.compiler.commons.implementations;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.dmg.pmml.Field;
import org.dmg.pmml.Model;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.compiler.api.provider.ModelImplementationProviderFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLModelRetriever {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLModelRetriever.class.getName());
    private static final ModelImplementationProviderFinder modelImplementationProviderFinder =
            new ModelImplementationProviderFinderImpl();

    private KiePMMLModelRetriever() {
    }

    public static Optional<Map<String, String>> getSourcesMapFromCommonDataAndTransformationDictionaryAndModel(final CompilationDTO compilationDTO) {
        logger.trace("getSourcesMapFromCommonDataAndTransformationDictionaryAndModel {}", compilationDTO);
        return getModelImplementationProviderStream(compilationDTO.getPMML_MODEL())
                .map(implementation -> implementation.getSourcesMap((CompilationDTO<Model>) compilationDTO))
                .findFirst();
    }

    /**
     * Read the given <code>CompilationDTO</code> to return an <code>Optional&lt;
     * KiePMMLModel&gt;</code>
     *
     * @param compilationDTO
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     */
    public static Optional<KiePMMLModel> getFromCommonDataAndTransformationDictionaryAndModelWithSources(final CompilationDTO compilationDTO) {
        logger.trace("getFromCommonDataAndTransformationDictionaryAndModelWithSources {}", compilationDTO);
        final Function<ModelImplementationProvider<Model, KiePMMLModel>, KiePMMLModel> modelFunction =
                implementation -> implementation.getKiePMMLModelWithSources(compilationDTO);
        return getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCommon(compilationDTO.getFields(),
                                                                                     compilationDTO.getModel(),
                                                                                     modelFunction);
    }

    /**
     * Read the given <code>CompilationDTO</code> to return an <code>Optional&lt;
     * KiePMMLModel&gt;</code>
     * Method provided only to have <b>drools</b> models working when invoked by a <code>KiePMMLMiningModel</code>
     *
     * @param compilationDTO
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     */
    public static Optional<KiePMMLModel> getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiled(final CompilationDTO compilationDTO) {
        logger.trace("getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiled {}", compilationDTO);
        final Function<ModelImplementationProvider<Model, KiePMMLModel>, KiePMMLModel> modelFunction =
                implementation -> implementation.getKiePMMLModelWithSourcesCompiled(compilationDTO);
        return getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCommon(compilationDTO.getFields(),
                                                                                     compilationDTO.getModel(),
                                                                                     modelFunction);
    }

    static Optional<KiePMMLModel> getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCommon(final List<Field<?>> fields,
                                                                                                        final Model model,
                                                                                                        final Function<ModelImplementationProvider<Model, KiePMMLModel>, KiePMMLModel> modelFunction) {
        logger.trace("getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCommon {}", model);
        final PMML_MODEL pmmlMODEL = PMML_MODEL.byName(model.getClass().getSimpleName());
        logger.debug("pmmlModelType {}", pmmlMODEL);
        return getModelImplementationProviderStream(pmmlMODEL)
                .map(modelFunction)
                .findFirst();
    }

    /**
     * Returns a <code>Stream</code> with <code>ModelImplementationProvider</code> targeting the given
     * <code>PMML_MODEL</code>
     *
     * @param pmmlMODEL
     * @return
     */
    private static <T extends Model, E extends KiePMMLModel> Stream<ModelImplementationProvider<T, E>> getModelImplementationProviderStream(final PMML_MODEL pmmlMODEL) {
        final List<ModelImplementationProvider<T, E>> implementations =
                modelImplementationProviderFinder.getImplementations(false);
        return implementations
                .stream()
                .filter(implementation -> pmmlMODEL.equals(implementation.getPMMLModelType()));
    }
}
