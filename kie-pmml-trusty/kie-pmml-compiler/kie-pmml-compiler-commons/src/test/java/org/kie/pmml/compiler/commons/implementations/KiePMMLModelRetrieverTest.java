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
import org.kie.pmml.compiler.commons.mocks.KiePMMLTestingModel;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndModel;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLModelRetrieverTest {

    private static final String MULTIPLE_TARGETS_SOURCE = "MultipleTargetsFieldSample.pmml";
    private static final String ONE_MINING_TARGET_SOURCE = "OneMiningTargetFieldSample.pmml";
    private PMML pmmlModel;

    @Test
    public void getFromDataDictionaryAndModelWithProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(MULTIPLE_TARGETS_SOURCE), MULTIPLE_TARGETS_SOURCE);
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndModel(pmmlModel.getDataDictionary(), pmmlModel.getTransformationDictionary(), pmmlModel.getModels().get(0), null);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        assertTrue(retrieved.get() instanceof KiePMMLTestingModel);
    }

    @Test
    public void getFromDataDictionaryAndModelWithoutProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndModel(pmmlModel.getDataDictionary(), pmmlModel.getTransformationDictionary(), pmmlModel.getModels().get(0), null);
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
    }
}