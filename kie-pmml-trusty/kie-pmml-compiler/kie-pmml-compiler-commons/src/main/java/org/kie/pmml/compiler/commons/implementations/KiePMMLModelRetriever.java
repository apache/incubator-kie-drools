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

import java.util.Optional;
import java.util.stream.Stream;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.Model;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.compiler.api.provider.ModelImplementationProviderFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLModelRetriever {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLModelRetriever.class.getName());
    private static final ModelImplementationProviderFinder modelImplementationProviderFinder = new ModelImplementationProviderFinderImpl();

    private KiePMMLModelRetriever() {
    }

    /**
     * Read the given <code>DataDictionary</code> and <code>Model</code>> to return an <code>Optional&lt;KiePMMLModel&gt;</code>
     * @param dataDictionary
     * @param transformationDictionary
     * @param model
     * @param kBuilder Using <code>Object</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     */
    public static Optional<KiePMMLModel> getFromCommonDataAndTransformationDictionaryAndModel(final DataDictionary dataDictionary,
                                                                                              final TransformationDictionary transformationDictionary,
                                                                                              final Model model,
                                                                                              final Object kBuilder) {
        logger.trace("getFromCommonDataAndTransformationDictionaryAndModel {}", model);
        final PMML_MODEL pmmlMODEL = PMML_MODEL.byName(model.getClass().getSimpleName());
        logger.debug("pmmlModelType {}", pmmlMODEL);
        return getModelImplementationProviderStream(model)
                .map(implementation -> implementation.getKiePMMLModel(dataDictionary, transformationDictionary, model, kBuilder))
                .findFirst();
    }

    /**
     * Read the given <code>DataDictionary</code> and <code>Model</code>> to return an <code>Optional&lt;KiePMMLModel&gt;</code>
     * @param packageName the package into which put all the generated classes out of the given <code>InputStream</code>
     * @param dataDictionary
     * @param transformationDictionary
     * @param model
     * @param kBuilder Using <code>Object</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     */
    public static Optional<KiePMMLModel> getFromCommonDataAndTransformationDictionaryAndModelFromPlugin(final String packageName,
                                                                                                        final DataDictionary dataDictionary,
                                                                                                        final TransformationDictionary transformationDictionary,
                                                                                                        final Model model,
                                                                                                        final Object kBuilder) {
        logger.trace("getFromCommonDataAndTransformationDictionaryAndModelFromPlugin {}", model);
        final PMML_MODEL pmmlMODEL = PMML_MODEL.byName(model.getClass().getSimpleName());
        logger.debug("pmmlModelType {}", pmmlMODEL);
        return getModelImplementationProviderStream(model)
                .map(implementation -> implementation.getKiePMMLModelFromPlugin(packageName, dataDictionary, transformationDictionary, model, kBuilder))
                .findFirst();
    }

    /**
     * Returns a <code>Stream</code> with <code>ModelImplementationProvider</code> targeting the given <code>Model</code>
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
