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
import org.kie.pmml.api.enums.PMML_MODEL;
import org.junit.Test;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.scorecard.model.KiePMMLScorecardModel;
import org.kie.pmml.models.scorecard.model.KiePMMLScorecardModelWithSources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ScorecardModelImplementationProviderTest {

    private static final String BASIC_COMPLEX_PARTIAL_SCORE_SOURCE = "BasicComplexPartialScore.pmml";
    private static final String PACKAGE_NAME = "packagename";
    private static PMML basicComplexPartialScorePmml;
    private static DataDictionary basicComplexPartialScoreDataDictionary;
    private static TransformationDictionary basicComplexPartialScoreTransformationDictionary;
    private static Scorecard basicComplexPartialScore;
    private static final ScorecardModelImplementationProvider provider = new ScorecardModelImplementationProvider();

    private static final ScorecardModelImplementationProvider PROVIDER= new ScorecardModelImplementationProvider();

    @BeforeClass
    public static void setupClass() throws Exception {
        basicComplexPartialScorePmml = TestUtils.loadFromFile(BASIC_COMPLEX_PARTIAL_SCORE_SOURCE);
        basicComplexPartialScoreDataDictionary = basicComplexPartialScorePmml.getDataDictionary();
        basicComplexPartialScoreTransformationDictionary = basicComplexPartialScorePmml.getTransformationDictionary();
        basicComplexPartialScore = ((Scorecard) basicComplexPartialScorePmml.getModels().get(0));
    }

    @Test
    public void getPMMLModelType(){
        assertEquals(PMML_MODEL.SCORECARD_MODEL,PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() {
        KiePMMLScorecardModel retrieved = provider.getKiePMMLModel(PACKAGE_NAME,
                                                                   basicComplexPartialScoreDataDictionary,
                                                                   basicComplexPartialScoreTransformationDictionary,
                                                                   basicComplexPartialScore,
                                                                   new HasClassLoaderMock());
        assertNotNull(retrieved);
    }

    @Test
    public void getKiePMMLModelWithSources() {
        KiePMMLScorecardModel retrieved = provider.getKiePMMLModelWithSources(PACKAGE_NAME,
                                                                   basicComplexPartialScoreDataDictionary,
                                                                   basicComplexPartialScoreTransformationDictionary,
                                                                   basicComplexPartialScore,
                                                                   new HasClassLoaderMock());
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof KiePMMLScorecardModelWithSources);
        Map<String, String> retrievedSourcesMap = ((KiePMMLScorecardModelWithSources)retrieved).getSourcesMap();
        assertNotNull(retrievedSourcesMap);
        assertFalse(retrievedSourcesMap.isEmpty());
    }
}