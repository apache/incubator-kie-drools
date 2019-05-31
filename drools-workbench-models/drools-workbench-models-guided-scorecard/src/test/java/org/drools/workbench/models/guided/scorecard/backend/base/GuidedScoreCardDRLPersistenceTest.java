/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.guided.scorecard.backend.base;

import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.drools.scorecards.StringUtil;
import org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardDRLPersistence;
import org.drools.workbench.models.guided.scorecard.shared.Attribute;
import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;

import static org.junit.Assert.*;

public class GuidedScoreCardDRLPersistenceTest {

    @Test
    public void testEmptyModel() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName("test");

        final String drl = GuidedScoreCardDRLPersistence.marshal(model);
        assertNotNull(drl);

        assertEquals(16,
                     StringUtil.countMatches(drl,
                                             "rule \""));

        assertEquals(13,
                     StringUtil.countMatches(drl,
                                             "import "));
    }

    @Test
    public void testEmptyModelEmptyStringPackageName() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName("test");
        model.setPackageName("");

        final String drl = GuidedScoreCardDRLPersistence.marshal(model);
        assertNotNull(drl);

        assertEquals(16,
                     StringUtil.countMatches(drl,
                                             "rule \""));

        assertEquals(13,
                     StringUtil.countMatches(drl,
                                             "import "));
    }

    @Test
    public void testEmptyModelInPackage() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName("test");
        model.setPackageName("org.drools.workbench.models.guided.scorecard.backend");

        final String drl = GuidedScoreCardDRLPersistence.marshal(model);
        assertNotNull(drl);

        assertTrue(drl.contains("package org.drools.workbench.models.guided.scorecard.backend"));
        assertEquals(16,
                     StringUtil.countMatches(drl,
                                             "rule \""));

        assertEquals(13,
                     StringUtil.countMatches(drl,
                                             "import "));
    }

    @Test
    public void testModelWithImports() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName("test");
        model.setPackageName("org.drools.workbench.models.guided.scorecard.backend");
        model.getImports().addImport(new Import("org.smurf.Pupa"));

        final String drl = GuidedScoreCardDRLPersistence.marshal(model);
        assertNotNull(drl);

        assertTrue(drl.contains("package org.drools.workbench.models.guided.scorecard.backend"));
        assertEquals(16,
                     StringUtil.countMatches(drl,
                                             "rule \""));

        assertEquals(14,
                     StringUtil.countMatches(drl,
                                             "import "));
    }

    @Test
    public void testModelWithImportsAndFactName() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName("test");
        model.setPackageName("org.drools.workbench.models.guided.scorecard.backend");
        model.getImports().addImport(new Import("org.smurf.Pupa"));
        model.setFactName("org.drools.MoreCheese");

        PMML pmmlDocument = GuidedScoreCardDRLPersistence.createPMMLDocument(model);
        final String drl = GuidedScoreCardDRLPersistence.marshal(model);
        assertNotNull(drl);

        assertTrue(drl.contains("package org.drools.workbench.models.guided.scorecard.backend"));

        assertTrue(drl.contains("import org.smurf.Pupa"));
        assertTrue(drl.contains("import org.drools.MoreCheese"));
    }

    @Test
    public void testModelWithImportsAndFactNameDuplicatingExplicitImport() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName("test");
        model.setPackageName("org.drools.workbench.models.guided.scorecard.backend");
        model.getImports().addImport(new Import("org.smurf.Pupa"));
        model.setFactName("org.smurf.Pupa");

        final String drl = GuidedScoreCardDRLPersistence.marshal(model);
        assertNotNull(drl);

        assertTrue(drl.contains("package org.drools.workbench.models.guided.scorecard.backend"));
        assertEquals(16,
                     StringUtil.countMatches(drl,
                                             "rule \""));

        assertEquals(14,
                     StringUtil.countMatches(drl,
                                             "import "));
    }

    @Test
    public void testBasicModel() {

        final ScoreCardModel model = new ScoreCardModel();
        model.setName("test");

        model.setPackageName("org.drools.workbench.models.guided.scorecard.backend");
        model.getImports().addImport(new Import("org.drools.workbench.models.guided.scorecard.backend.test1.Applicant"));
        model.setReasonCodesAlgorithm("none");
        model.setBaselineScore(0.0);
        model.setInitialScore(0.0);

        model.setFactName("org.drools.workbench.models.guided.scorecard.backend.test1.Applicant");
        model.setFieldName("score");
        model.setUseReasonCodes(false);
        model.setReasonCodeField("");

        final Characteristic c = new Characteristic();
        c.setName("c1");
        c.setFact("org.drools.workbench.models.guided.scorecard.backend.test1.Applicant");
        c.setDataType("Double");
        c.setField("age");
        c.setBaselineScore(0.0);
        c.setReasonCode("");

        final Attribute a = new Attribute();
        a.setOperator("=");
        a.setValue("10");
        a.setPartialScore(0.1);
        a.setReasonCode("");

        c.getAttributes().add(a);
        model.getCharacteristics().add(c);

        final String drl1 = GuidedScoreCardDRLPersistence.marshal(model);
        assertNotNull(drl1);

        final String drl2 = GuidedScoreCardDRLPersistence.marshal(model);
        assertNotNull(drl2);
    }
}
