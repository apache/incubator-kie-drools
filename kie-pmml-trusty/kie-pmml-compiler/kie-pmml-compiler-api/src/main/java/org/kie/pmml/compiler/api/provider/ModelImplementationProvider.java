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

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.Model;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLModel;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

/**
 * API for actual PMML model implementations
 */
public interface ModelImplementationProvider<T extends Model, E extends KiePMMLModel> {

    PMML_MODEL getPMMLModelType();

    /**
     * Method to be called for a <b>runtime</b> compilation
     *
     * @param packageName the package into which put all the generated classes out of the given <code>Model</code>
     * @param dataDictionary
     * @param transformationDictionary
     * @param model
     * @param hasClassloader Using <code>HasClassloader</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLInternalException
     */
    E getKiePMMLModel(final String packageName, final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final T model, final HasClassLoader hasClassloader);

    /**
     * Method to be called following a <b>kie-maven-plugin</b> invocation
     *
     * @param packageName the package into which put all the generated classes out of the given <code>Model</code>
     * @param dataDictionary
     * @param transformationDictionary
     * @param model
     * @param hasClassloader Using <code>HasClassloader</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLInternalException
     */
    E getKiePMMLModelWithSources(final String packageName, final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final T model, final HasClassLoader hasClassloader);

    /**
     * Method provided only to have <b>drools</b> models working when invoked by a <code>KiePMMLMiningModel</code>
     * Default implementation provided for <b>not-drools</b> models.
     *
     * @param packageName the package into which put all the generated classes out of the given <code>Model</code>
     * @param dataDictionary
     * @param transformationDictionary
     * @param model
     * @param hasClassloader Using <code>HasClassloader</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLInternalException
     */
    default E getKiePMMLModelWithSourcesCompiled(final String packageName, final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final T model, final HasClassLoader hasClassloader) {
        E toReturn = getKiePMMLModelWithSources(packageName, dataDictionary, transformationDictionary, model, hasClassloader);
        final Map<String, String> sourcesMap = ((HasSourcesMap)toReturn).getSourcesMap();
        String className = getSanitizedClassName(model.getModelName());
        String fullClassName = packageName + "." + className;
        try {
            hasClassloader.compileAndLoadClass(sourcesMap, fullClassName);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
        return toReturn;
    }
}
