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

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.junit.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.kie.pmml.models.mining.model.KiePMMLMiningModelWithSources;
import org.kie.test.util.filesystem.FileUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MiningModelImplementationProviderTest {

    private static final MiningModelImplementationProvider PROVIDER = new MiningModelImplementationProvider();
    private static final String SOURCE_REGRESSION = "MiningModel_Regression.pmml";
    private static final String SOURCE_TREE = "MiningModel_TreeModel.pmml";
    private static final String SOURCE_SCORECARD = "MiningModel_Scorecard.pmml";
    private static final String SOURCE_MIXED = "MiningModel_Mixed.pmml";
    private static final String SOURCE_NO_SEGMENT_ID = "MiningModel_NoSegmentId.pmml";
    private static final String SOURCE_SEGMENT_ID = "MiningModel_SegmentId.pmml";

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.MINING_MODEL, PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModelRegression() throws Exception {
        commonGetKiePMMLModel(SOURCE_REGRESSION);
    }

    @Test
    public void getKiePMMLModelTree() throws Exception {
        commonGetKiePMMLModel(SOURCE_TREE);
    }

    @Test
    public void getKiePMMLModelScorecard() throws Exception {
        commonGetKiePMMLModel(SOURCE_SCORECARD);
    }

    @Test
    public void getKiePMMLModelMixed() throws Exception {
        commonGetKiePMMLModel(SOURCE_MIXED);
    }

    @Test
    public void getKiePMMLModelFromPluginRegression() throws Exception {
        commonGetKiePMMLModelFromPlugin(SOURCE_REGRESSION);
    }

    @Test
    public void getKiePMMLModelFromPluginTree() throws Exception {
        commonGetKiePMMLModelFromPlugin(SOURCE_TREE);
    }

    @Test
    public void getKiePMMLModelFromPluginScorecard() throws Exception {
        commonGetKiePMMLModelFromPlugin(SOURCE_SCORECARD);
    }

    @Test
    public void getKiePMMLModelFromPluginMixed() throws Exception {
        commonGetKiePMMLModelFromPlugin(SOURCE_MIXED);
    }

    @Test
    public void populateMissingIds() throws Exception {
        commonVerifySegmentId(SOURCE_NO_SEGMENT_ID);
        commonVerifySegmentId(SOURCE_SEGMENT_ID);
    }

    private void commonVerifySegmentId(final String source) throws Exception{
        final PMML pmml = getPMML(source);
        final MiningModel miningModel = (MiningModel) pmml.getModels().get(0);
        commonVerifySegmentId(miningModel.getSegmentation().getSegments());
    }

    private void commonVerifySegmentId(final List<Segment> segments) {
        for (Segment segment : segments) {
            assertNotNull(segment.getId());
            if (segment.getModel() instanceof MiningModel) {
                commonVerifySegmentId(((MiningModel) segment.getModel()).getSegmentation().getSegments());
            }
        }
    }

    private void commonGetKiePMMLModel(String source) throws Exception {
        final PMML pmml = getPMML(source);
        final KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final KiePMMLMiningModel retrieved = PROVIDER.getKiePMMLModel(pmml.getDataDictionary(),
                                                                         pmml.getTransformationDictionary(),
                                                                         (MiningModel) pmml.getModels().get(0),
                                                                         knowledgeBuilder);
        assertNotNull(retrieved);
    }

    private void commonGetKiePMMLModelFromPlugin(String source) throws Exception {
        final PMML pmml = getPMML(source);
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final KiePMMLMiningModel retrieved = PROVIDER.getKiePMMLModelFromPlugin("PACKAGE_NAME",
                                                                          pmml.getDataDictionary(),
                                                                          pmml.getTransformationDictionary(),
                                                                          (MiningModel) pmml.getModels().get(0),
                                                                          knowledgeBuilder);
        assertNotNull(retrieved);
        assertNotNull(retrieved.getNestedModels());
        assertFalse(retrieved.getNestedModels().isEmpty());
        assertTrue(retrieved instanceof KiePMMLMiningModelWithSources);
        final Map<String, String> sourcesMap = new HashMap<>(((KiePMMLMiningModelWithSources)retrieved).getSourcesMap());
        assertFalse(sourcesMap.isEmpty());
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
            fail("Expecting compilation error without nested models sources");
        } catch (Exception e) {
            // Expected
        }
        retrieved.getNestedModels().forEach(nestedModel -> sourcesMap.putAll(((HasSourcesMap)nestedModel).getSourcesMap()));
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private PMML getPMML(String source) throws Exception {
        final FileInputStream fis = FileUtils.getFileInputStream(source);
        final PMML toReturn = KiePMMLUtil.load(fis, source);
        assertNotNull(toReturn);
        assertEquals(1, toReturn.getModels().size());
        assertTrue(toReturn.getModels().get(0) instanceof MiningModel);
        return toReturn;
    }
}