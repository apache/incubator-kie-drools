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
package org.kie.pmml.compiler.commons.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.Output;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.compiler.api.provider.ModelImplementationProviderFinder;
import org.kie.pmml.compiler.commons.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLModelRetriever {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLModelRetriever.class.getName());
    private static final ModelImplementationProviderFinder modelImplementationProviderFinder =
            new ModelImplementationProviderFinderImpl();

    private KiePMMLModelRetriever() {
    }

    /**
     * Read the given <code>DataDictionary</code> and <code>Model</code>> to return an <code>Optional&lt;
     * KiePMMLModel&gt;</code>
     * @param dataDictionary
     * @param transformationDictionary
     * @param model
     * @param hasClassloader Using <code>HasClassloader</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     */
    public static Optional<KiePMMLModel> getFromCommonDataAndTransformationDictionaryAndModel(final DataDictionary dataDictionary,
                                                                                              final TransformationDictionary transformationDictionary,
                                                                                              final Model model,
                                                                                              final HasClassLoader hasClassloader) {
        logger.trace("getFromCommonDataAndTransformationDictionaryAndModel {}", model);
        final PMML_MODEL pmmlMODEL = PMML_MODEL.byName(model.getClass().getSimpleName());
        logger.debug("pmmlModelType {}", pmmlMODEL);
        return getModelImplementationProviderStream(model)
                .map(implementation -> implementation.getKiePMMLModel(dataDictionary, transformationDictionary, model
                        , hasClassloader))
                .map(kiePMMLModel -> getPopulatedWithPMMLModelFields(kiePMMLModel, model.getMiningSchema(), model.getOutput()))
                .findFirst();
    }

    /**
     * Read the given <code>DataDictionary</code> and <code>Model</code>> to return an <code>Optional&lt;
     * KiePMMLModel&gt;</code>
     * @param packageName the package into which put all the generated classes out of the given <code>InputStream</code>
     * @param dataDictionary
     * @param transformationDictionary
     * @param model
     * @param hasClassloader Using <code>HasClassloader</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     */
    public static Optional<KiePMMLModel> getFromCommonDataAndTransformationDictionaryAndModelWithSources(final String packageName,
                                                                                                         final DataDictionary dataDictionary,
                                                                                                         final TransformationDictionary transformationDictionary,
                                                                                                         final Model model,
                                                                                                         final HasClassLoader hasClassloader) {
        logger.trace("getFromCommonDataAndTransformationDictionaryAndModelWithSources {}", model);
        final PMML_MODEL pmmlMODEL = PMML_MODEL.byName(model.getClass().getSimpleName());
        logger.debug("pmmlModelType {}", pmmlMODEL);
        return getModelImplementationProviderStream(model)
                .map(implementation -> implementation.getKiePMMLModelWithSources(packageName, dataDictionary,
                                                                                 transformationDictionary, model,
                                                                                 hasClassloader)).findFirst();
    }

    static KiePMMLModel getPopulatedWithPMMLModelFields(final KiePMMLModel toPopulate, final MiningSchema miningSchema,
                                                        final Output output) {
        if (miningSchema != null) {
            final List<MiningField> miningFields = miningSchema.getMiningFields()
                    .stream()
                    .map(ModelUtils::convertToKieMiningField)
                    .collect(Collectors.toList());
            toPopulate.setMiningFields(miningFields);
        }
        if (output != null) {
            final List<OutputField> outputFields = output.getOutputFields()
                    .stream()
                    .map(ModelUtils::convertToKieOutputField)
                    .collect(Collectors.toList());
            toPopulate.setOutputFields(outputFields);
        }
        return toPopulate;
    }

    /**
     * Returns a <code>Stream</code> with <code>ModelImplementationProvider</code> targeting the given
     * <code>Model</code>
     * @param model
     * @return
     */
    private static Stream<ModelImplementationProvider<Model, KiePMMLModel>> getModelImplementationProviderStream(final Model model) {
        final PMML_MODEL pmmlMODEL = PMML_MODEL.byName(model.getClass().getSimpleName());
        return modelImplementationProviderFinder.getImplementations(false)
                .stream()
                .filter(implementation -> pmmlMODEL.equals(implementation.getPMMLModelType()));
    }
}
