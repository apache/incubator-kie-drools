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

import java.util.Collections;
import java.util.Optional;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Output;
import org.dmg.pmml.PMML;
import org.junit.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.commons.mocks.TestModel;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModel;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModelWithSources;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiled;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getPMMLWithRandomTestModel;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomDataType;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomMiningSchema;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomOutput;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLModelRetrieverTest {

    private static final String MULTIPLE_TARGETS_SOURCE = "MultipleTargetsFieldSample.pmml";
    private static final String ONE_MINING_TARGET_SOURCE = "OneMiningTargetFieldSample.pmml";
    private static final String PACKAGE_NAME = "packagename";
    private PMML pmmlModel;

    @Test
    public void getFromCommonDataAndTransformationDictionaryAndModelWithProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(MULTIPLE_TARGETS_SOURCE), MULTIPLE_TARGETS_SOURCE);
        pmmlModel.getModels().set(0, new TestModel());
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndTransformationDictionaryAndModel(PACKAGE_NAME,
                                                                                                      pmmlModel.getDataDictionary(),
                                                                                                      pmmlModel.getTransformationDictionary(),
                                                                                                      pmmlModel.getModels().get(0), null);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        assertTrue(retrieved.get() instanceof KiePMMLTestingModel);
    }

    @Test
    public void getFromCommonDataAndTransformationDictionaryAndModelWithoutProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndTransformationDictionaryAndModel(PACKAGE_NAME,
                                                                                                      pmmlModel.getDataDictionary(),
                                                                                                      pmmlModel.getTransformationDictionary(),
                                                                                                      pmmlModel.getModels().get(0), null);
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
    }

    @Test
    public void getFromCommonDataAndTransformationDictionaryAndModelWithSourcesWithProvider() {
        pmmlModel = getPMMLWithRandomTestModel();
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndTransformationDictionaryAndModelWithSources(PACKAGE_NAME, pmmlModel.getDataDictionary(), pmmlModel.getTransformationDictionary(), pmmlModel.getModels().get(0), null);
        assertNotNull(retrieved);
    }

    @Test
    public void getFromDataDictionaryAndModelWithSourcesWithoutProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndTransformationDictionaryAndModelWithSources(PACKAGE_NAME, pmmlModel.getDataDictionary(), pmmlModel.getTransformationDictionary(), pmmlModel.getModels().get(0), null);
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
    }

    @Test
    public void getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiledWithProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final HasClassLoaderMock hasClassLoaderMock = new HasClassLoaderMock();
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiled(PACKAGE_NAME, pmmlModel.getDataDictionary(), pmmlModel.getTransformationDictionary(), pmmlModel.getModels().get(0), hasClassLoaderMock);
        assertNotNull(retrieved);
    }

    @Test
    public void getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiledWithoutProvider() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final HasClassLoaderMock hasClassLoaderMock = new HasClassLoaderMock();
        final Optional<KiePMMLModel> retrieved = getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiled(PACKAGE_NAME, pmmlModel.getDataDictionary(), pmmlModel.getTransformationDictionary(), pmmlModel.getModels().get(0), hasClassLoaderMock);
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
    }

    @Test
    public void getPopulatedWithPMMLModelFields() {
        KiePMMLTestingModel toPopulate = KiePMMLTestingModel.builder("TESTINGMODEL",
                                                                     Collections.emptyList(),
                                                                     MINING_FUNCTION.REGRESSION).build();
        assertTrue(toPopulate.getMiningFields().isEmpty());
        assertTrue(toPopulate.getOutputFields().isEmpty());
        final MiningSchema miningSchema = getRandomMiningSchema();
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(miningSchema.getMiningFields().stream()
                                             .map(miningField -> getDataField(miningField.getName().getValue(),
                                                                              miningField.getOpType(),
                                                                              getRandomDataType())).toArray(DataField[]::new));
        final Output output = getRandomOutput();
        KiePMMLTestingModel populated =
                (KiePMMLTestingModel) KiePMMLModelRetriever.getPopulatedWithPMMLModelFields(toPopulate,
                                                                                            dataDictionary,
                                                                                            miningSchema, output);
        assertEquals(miningSchema.getMiningFields().size(), populated.getMiningFields().size());
        assertEquals(output.getOutputFields().size(), populated.getOutputFields().size());
        toPopulate = KiePMMLTestingModel.builder("TESTINGMODEL",
                                                 Collections.emptyList(),
                                                 MINING_FUNCTION.REGRESSION).build();
        populated = (KiePMMLTestingModel) KiePMMLModelRetriever.getPopulatedWithPMMLModelFields(toPopulate,
                                                                                                dataDictionary,
                                                                                                miningSchema, null);
        assertEquals(miningSchema.getMiningFields().size(), populated.getMiningFields().size());
        assertTrue(populated.getOutputFields().isEmpty());
        toPopulate = KiePMMLTestingModel.builder("TESTINGMODEL",
                                                 Collections.emptyList(),
                                                 MINING_FUNCTION.REGRESSION).build();
        populated = (KiePMMLTestingModel) KiePMMLModelRetriever.getPopulatedWithPMMLModelFields(toPopulate,
                                                                                                dataDictionary, null,
                                                                                                output);
        assertTrue(populated.getMiningFields().isEmpty());
        assertEquals(output.getOutputFields().size(), populated.getOutputFields().size());
        toPopulate = KiePMMLTestingModel.builder("TESTINGMODEL",
                                                 Collections.emptyList(),
                                                 MINING_FUNCTION.REGRESSION).build();
        populated = (KiePMMLTestingModel) KiePMMLModelRetriever.getPopulatedWithPMMLModelFields(toPopulate,
                                                                                                dataDictionary, null,
                                                                                                null);
        assertTrue(populated.getMiningFields().isEmpty());
        assertTrue(populated.getOutputFields().isEmpty());
    }
}