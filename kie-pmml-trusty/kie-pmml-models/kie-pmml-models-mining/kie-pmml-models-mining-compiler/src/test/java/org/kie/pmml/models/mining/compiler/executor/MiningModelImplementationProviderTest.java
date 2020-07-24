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

import org.dmg.pmml.PMML;
import org.dmg.pmml.mining.MiningModel;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.junit.Test;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MiningModelImplementationProviderTest {

    private static final MiningModelImplementationProvider PROVIDER= new MiningModelImplementationProvider();
    private static final String SOURCE_REGRESSION = "MiningModel_Regression.pmml";
    private static final String SOURCE_TREE = "MiningModel_TreeModel.pmml";
    private static final String SOURCE_SCORECARD = "MiningModel_Scorecard.pmml";
    private static final String SOURCE_MIXED = "MiningModel_Mixed.pmml";

    @Test
    public void getPMMLModelType(){
        assertEquals(PMML_MODEL.MINING_MODEL,PROVIDER.getPMMLModelType());
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

    private void commonGetKiePMMLModel(String source) throws Exception {
        final PMML pmml = TestUtils.loadFromFile(source);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof MiningModel);
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final KiePMMLMiningModel kiePMMLModel = PROVIDER.getKiePMMLModel(pmml.getDataDictionary(), pmml.getTransformationDictionary(), (MiningModel) pmml.getModels().get(0), knowledgeBuilder);
        assertNotNull(kiePMMLModel);
    }
}