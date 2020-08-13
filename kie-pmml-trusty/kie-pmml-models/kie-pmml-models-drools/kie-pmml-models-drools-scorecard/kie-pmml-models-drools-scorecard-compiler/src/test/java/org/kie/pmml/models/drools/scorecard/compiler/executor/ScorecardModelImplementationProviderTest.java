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
package org.kie.pmml.models.drools.scorecard.compiler.executor;

import java.io.FileInputStream;

import org.dmg.pmml.PMML;
import org.dmg.pmml.scorecard.Scorecard;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.models.drools.scorecard.model.KiePMMLScorecardModel;
import org.kie.test.util.filesystem.FileUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScorecardModelImplementationProviderTest {

    private static final ScorecardModelImplementationProvider PROVIDER = new ScorecardModelImplementationProvider();
    private final static KnowledgeBuilder KNOWLEDGE_BUILDER = new KnowledgeBuilderImpl();
    private static final String SOURCE_1 = "ScorecardSample.pmml";

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.SCORECARD_MODEL, PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() throws Exception {
        final PMML pmml = getPMML(SOURCE_1);
        final KiePMMLScorecardModel kiePMMLModel = PROVIDER.getKiePMMLModel(pmml.getDataDictionary(),
                                                                            pmml.getTransformationDictionary(),
                                                                            (Scorecard) pmml.getModels().get(0),
                                                                            KNOWLEDGE_BUILDER);
        assertNotNull(kiePMMLModel);
    }

    @Test
    public void getKiePMMLModelFromPlugin() throws Exception {
        final PMML pmml = getPMML(SOURCE_1);
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final KiePMMLDroolsModel retrieved = PROVIDER.getKiePMMLModelFromPlugin("PACKAGE_NAME",
                                                                                pmml.getDataDictionary(),
                                                                                pmml.getTransformationDictionary(),
                                                                                (Scorecard) pmml.getModels().get(0),
                                                                                knowledgeBuilder);
        assertNotNull(retrieved);
    }

    private PMML getPMML(String source) throws Exception {
        final FileInputStream fis = FileUtils.getFileInputStream(source);
        final PMML toReturn = KiePMMLUtil.load(fis, source);
        assertNotNull(toReturn);
        assertEquals(1, toReturn.getModels().size());
        assertTrue(toReturn.getModels().get(0) instanceof Scorecard);
        return toReturn;
    }
}