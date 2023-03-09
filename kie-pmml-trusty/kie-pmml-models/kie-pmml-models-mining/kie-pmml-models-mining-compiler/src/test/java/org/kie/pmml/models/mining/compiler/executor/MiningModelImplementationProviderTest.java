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
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.util.ClassUtils;
import org.junit.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.ExternalizableMock;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.kie.pmml.models.mining.compiler.HasKnowledgeBuilderMock;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.kie.pmml.models.mining.model.KiePMMLMiningModelWithSources;
import org.kie.test.util.filesystem.FileUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;

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
        assertThat(PROVIDER.getPMMLModelType()).isEqualTo(PMML_MODEL.MINING_MODEL);
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
    public void getKiePMMLModelWithSourcesRegression() throws Exception {
        commonGetKiePMMLModelWithSources(SOURCE_REGRESSION);
    }

    @Test
    public void getKiePMMLModelWithSourcesTree() throws Exception {
        commonGetKiePMMLModelWithSources(SOURCE_TREE);
    }

    @Test
    public void getKiePMMLModelWithSourcesScorecard() throws Exception {
        commonGetKiePMMLModelWithSources(SOURCE_SCORECARD);
    }

    @Test
    public void getKiePMMLModelWithSourcesMixed() throws Exception {
        commonGetKiePMMLModelWithSources(SOURCE_MIXED);
    }

    @Test
    public void populateMissingIds() throws Exception {
        commonVerifySegmentId(SOURCE_NO_SEGMENT_ID);
        commonVerifySegmentId(SOURCE_SEGMENT_ID);
    }

    private void commonVerifySegmentId(final String source) throws Exception {
        final PMML pmml = getPMML(source);
        final MiningModel miningModel = (MiningModel) pmml.getModels().get(0);
        commonVerifySegmentId(miningModel.getSegmentation().getSegments());
    }

    private void commonVerifySegmentId(final List<Segment> segments) {
        for (Segment segment : segments) {
            assertThat(segment.getId()).isNotNull();
            if (segment.getModel() instanceof MiningModel) {
                commonVerifySegmentId(((MiningModel) segment.getModel()).getSegmentation().getSegments());
            }
        }
    }

    private void commonGetKiePMMLModel(String source) throws Exception {
        final PMML pmml = getPMML(source);
        final KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final MiningModel miningmodel = (MiningModel) pmml.getModels().get(0);

        final CommonCompilationDTO<MiningModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       miningmodel,
                                                                       new HasKnowledgeBuilderMock(knowledgeBuilder));
        final KiePMMLMiningModel retrieved = PROVIDER.getKiePMMLModel(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(Serializable.class);
        commonVerifyIsDeepCloneable(retrieved);
    }

    private void commonGetKiePMMLModelWithSources(String source) throws Exception {
        final PMML pmml = getPMML(source);
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final MiningModel miningmodel = (MiningModel) pmml.getModels().get(0);
        final CommonCompilationDTO<MiningModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       miningmodel,
                                                                       new HasKnowledgeBuilderMock(knowledgeBuilder));
        final KiePMMLMiningModelWithSources retrieved =
                (KiePMMLMiningModelWithSources) PROVIDER.getKiePMMLModelWithSources(compilationDTO);
        assertThat(retrieved).isNotNull();
        commonVerifyIsDeepCloneable(retrieved);
        assertThat(retrieved.getNestedModels()).isNotNull();
        assertThat(retrieved.getNestedModels()).isNotEmpty();
        final Map<String, String> sourcesMap = new HashMap<>(retrieved.getSourcesMap());
        assertThat(sourcesMap).isNotEmpty();
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
            fail("Expecting compilation error without nested models sources");
        } catch (Exception e) {
            // Expected
        }
        retrieved.getNestedModels().forEach(nestedModel -> sourcesMap.putAll(((HasSourcesMap) nestedModel).getSourcesMap()));
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private PMML getPMML(String source) throws Exception {
        final FileInputStream fis = FileUtils.getFileInputStream(source);
        final PMML toReturn = KiePMMLUtil.load(fis, source);
        assertThat(toReturn).isNotNull();
        assertThat(toReturn.getModels()).hasSize(1);
        assertThat(toReturn.getModels().get(0)).isInstanceOf(MiningModel.class);
        return toReturn;
    }

    private void commonVerifyIsDeepCloneable(AbstractKiePMMLComponent toVerify) {
        assertThat(toVerify).isInstanceOf(Serializable.class);
        ExternalizableMock externalizableMock = new ExternalizableMock();
        externalizableMock.setKiePMMLComponent(toVerify);
        ClassUtils.deepClone(externalizableMock);
    }
}