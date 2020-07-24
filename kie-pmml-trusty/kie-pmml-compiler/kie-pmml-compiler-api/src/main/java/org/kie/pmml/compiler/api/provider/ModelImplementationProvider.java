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

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.Model;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.PMML_MODEL;

/**
 * API for actual PMML model implementations
 */
public interface ModelImplementationProvider<T extends Model, E extends KiePMMLModel> {

    PMML_MODEL getPMMLModelType();

    /**
     *
     * @param dataDictionary
     * @param transformationDictionary
     * @param model
     * @param kBuilder Using <code>Object</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLInternalException
     */
    E getKiePMMLModel(final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final T model, final Object kBuilder);

    /**
     * Method to be called following a <b>kie-maven-plugin</b> invocation
     *
     * @param packageName the package into which put all the generated classes out of the given <code>Model</code>
     * @param dataDictionary
     * @param transformationDictionary
     * @param model
     * @param kBuilder Using <code>Object</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLInternalException
     */
    E getKiePMMLModelFromPlugin(final String packageName, final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final T model, final Object kBuilder);
}
