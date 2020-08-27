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

import org.dmg.pmml.PMML;
import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModel;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModelFromPlugin;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLModelRetrieverTest {

    private static final String MULTIPLE_TARGETS_SOURCE = "MultipleTargetsFieldSample.pmml";
    private static final String ONE_MINING_TARGET_SOURCE = "OneMiningTargetFieldSample.pmml";
    private static final String PACKAGE_NAME = "packagename";
    private PMML pmmlModel;

    @Test
    public void getFromCommonDataAndTransformationDictionaryAndModelWithProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(MULTIPLE_TARGETS_SOURCE), MULTIPLE_TARGETS_SOURCE);
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndTransformationDictionaryAndModel(pmmlModel.getDataDictionary(), pmmlModel.getTransformationDictionary(), pmmlModel.getModels().get(0), null);
        assertNotNull(retrieved);
    }

    @Test
    public void getFromCommonDataAndTransformationDictionaryAndModelWithoutProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndTransformationDictionaryAndModel(pmmlModel.getDataDictionary(), pmmlModel.getTransformationDictionary(), pmmlModel.getModels().get(0), null);
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
    }

    @Test
    public void getFromCommonDataAndTransformationDictionaryAndModelFromPluginWithProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(MULTIPLE_TARGETS_SOURCE), MULTIPLE_TARGETS_SOURCE);
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndTransformationDictionaryAndModelFromPlugin(PACKAGE_NAME, pmmlModel.getDataDictionary(), pmmlModel.getTransformationDictionary(), pmmlModel.getModels().get(0), null);
        assertNotNull(retrieved);
    }

    @Test
    public void getFromDataDictionaryAndModelWithoutProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndTransformationDictionaryAndModelFromPlugin(PACKAGE_NAME, pmmlModel.getDataDictionary(), pmmlModel.getTransformationDictionary(), pmmlModel.getModels().get(0), null);
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
    }
}