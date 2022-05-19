/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.scorecard.compiler.executor;

import java.util.Map;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.scorecard.Scorecard;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.models.scorecard.model.KiePMMLScorecardModel;

import static org.assertj.core.api.Assertions.assertThat;

public class ScorecardModelImplementationProviderTest {

    private static final String BASIC_COMPLEX_PARTIAL_SCORE_SOURCE = "BasicComplexPartialScore.pmml";
    private static final String PACKAGE_NAME = "packagename";
    private static final ScorecardModelImplementationProvider provider = new ScorecardModelImplementationProvider();
    private static final ScorecardModelImplementationProvider PROVIDER = new ScorecardModelImplementationProvider();
    private static PMML basicComplexPartialScorePmml;
    private static DataDictionary basicComplexPartialScoreDataDictionary;
    private static TransformationDictionary basicComplexPartialScoreTransformationDictionary;
    private static Scorecard basicComplexPartialScore;

    @BeforeClass
    public static void setupClass() throws Exception {
        basicComplexPartialScorePmml = TestUtils.loadFromFile(BASIC_COMPLEX_PARTIAL_SCORE_SOURCE);
        basicComplexPartialScoreDataDictionary = basicComplexPartialScorePmml.getDataDictionary();
        basicComplexPartialScoreTransformationDictionary = basicComplexPartialScorePmml.getTransformationDictionary();
        basicComplexPartialScore = ((Scorecard) basicComplexPartialScorePmml.getModels().get(0));
    }

    @Test
    public void getPMMLModelType() {
        assertThat(PROVIDER.getPMMLModelType()).isEqualTo(PMML_MODEL.SCORECARD_MODEL);
    }

    @Test
    public void getKiePMMLModel() {
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       basicComplexPartialScorePmml,
                                                                       basicComplexPartialScore,
                                                                       new HasClassLoaderMock());
        KiePMMLScorecardModel retrieved = provider.getKiePMMLModel(compilationDTO);
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void getKiePMMLModelWithSources() {
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       basicComplexPartialScorePmml,
                                                                       basicComplexPartialScore,
                                                                       new HasClassLoaderMock());
        KiePMMLModelWithSources retrieved = provider.getKiePMMLModelWithSources(compilationDTO);
        assertThat(retrieved).isNotNull();
        Map<String, String> retrievedSourcesMap = retrieved.getSourcesMap();
        assertThat(retrievedSourcesMap).isNotNull();
        assertThat(retrievedSourcesMap).isNotEmpty();
    }
}