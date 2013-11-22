package org.drools.scorecards;

import org.junit.Assert;
import org.dmg.pmml.pmml_4_1.descr.Attribute;
import org.dmg.pmml.pmml_4_1.descr.Characteristic;
import org.dmg.pmml.pmml_4_1.descr.Characteristics;
import org.dmg.pmml.pmml_4_1.descr.PMML;
import org.dmg.pmml.pmml_4_1.descr.Scorecard;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.definition.type.FactType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.drools.scorecards.ScorecardCompiler.DrlType.INTERNAL_DECLARED_TYPES;

public class ScorecardReasonCodeTest {
    private static PMML pmmlDocument;
    private static String drl;
    private static ScorecardCompiler scorecardCompiler;
    @Before
    public void setUp() throws Exception {
        scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"));
        if (!compileResult) {
            for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
                System.out.println("setup :"+error.getErrorLocation()+"->"+error.getErrorMessage());
            }
        }
        drl = scorecardCompiler.getDRL();
        Assert.assertNotNull(drl);
        assertTrue(drl.length() > 0);

        pmmlDocument = scorecardCompiler.getPMMLDocument();
    }

    @Test
    public void testPMMLDocument() throws Exception {
        Assert.assertNotNull(pmmlDocument);

        String pmml = scorecardCompiler.getPMML();
        Assert.assertNotNull(pmml);
        assertTrue(pmml.length() > 0);
        //System.out.println(pmml);
    }

    @Test
    public void testAbsenceOfReasonCodes() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_c.xls"));
        PMML pmml = scorecardCompiler.getPMMLDocument();
        for (Object serializable : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                assertFalse(((Scorecard) serializable).isUseReasonCodes());
            }
        }
    }

    @Test
    public void testUseReasonCodes() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                assertTrue(((Scorecard)serializable).isUseReasonCodes());
                assertEquals(100.0, ((Scorecard)serializable).getInitialScore(), 0.0);
                assertEquals("pointsBelow",((Scorecard)serializable).getReasonCodeAlgorithm());
            }
        }
    }

    @Test
    public void testReasonCodes() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(4, characteristics.getCharacteristics().size());
                        for (Characteristic characteristic : characteristics.getCharacteristics()){
                            for (Attribute attribute : characteristic.getAttributes()){
                                assertNotNull(attribute.getReasonCode());
                            }
                        }
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testBaselineScores() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(4, characteristics.getCharacteristics().size());
                        assertEquals(10.0, characteristics.getCharacteristics().get(0).getBaselineScore(), 0.0);
                        assertEquals(99.0, characteristics.getCharacteristics().get(1).getBaselineScore(), 0.0);
                        assertEquals(12.0, characteristics.getCharacteristics().get(2).getBaselineScore(), 0.0);
                        assertEquals(15.0, characteristics.getCharacteristics().get(3).getBaselineScore(), 0.0);
                        assertEquals(25.0, ((Scorecard)serializable).getBaselineScore(), 0.0);
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testMissingReasonCodes() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_reason_error");
        assertEquals(3, scorecardCompiler.getScorecardParseErrors().size());
        assertEquals("$F$13", scorecardCompiler.getScorecardParseErrors().get(0).getErrorLocation());
        assertEquals("$F$22", scorecardCompiler.getScorecardParseErrors().get(1).getErrorLocation());
    }

    @Test
    public void testMissingBaselineScores() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_reason_error");
        assertEquals(3, scorecardCompiler.getScorecardParseErrors().size());
        assertEquals("$D$30", scorecardCompiler.getScorecardParseErrors().get(2).getErrorLocation());
    }

    @Test
    public void testReasonCodesCombinations() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_char_reasoncode");
        assertEquals(0, scorecardCompiler.getScorecardParseErrors().size());
        String drl = scorecardCompiler.getDRL();
        assertNotNull(drl);
        //System.out.println(drl);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        for (KnowledgeBuilderError error : kbuilder.getErrors()){
            System.out.println(error.getMessage());
        }
        assertFalse( kbuilder.hasErrors() );

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );

        DroolsScorecard scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        //age = 30, validLicence -1
        assertTrue(29 == scorecard.getCalculatedScore());
        //age-reasoncode=AGE02, license-reasoncode=VL002
        assertEquals(2, scorecard.getReasonCodes().size());
        //AGE02 - should be knocked out as we are using the pointsBelow Algorithm.
        assertFalse(scorecard.getReasonCodes().contains("AGE02"));
        assertTrue(scorecard.getReasonCodes().contains("VL099"));

        session = kbase.newStatefulKnowledgeSession();
        scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 0);
        scorecardType.set(scorecard, "occupation", "SKYDIVER");
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertTrue(-1 == scorecard.getCalculatedScore());
        assertEquals(3, scorecard.getReasonCodes().size());
        //[AGE01, VL002, OCC01]
        assertTrue(scorecard.getReasonCodes().contains("AGE01"));
        assertTrue(scorecard.getReasonCodes().contains("VL099"));
        assertTrue(scorecard.getReasonCodes().contains("OCC99"));

        session = kbase.newStatefulKnowledgeSession();
        scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 20);
        scorecardType.set(scorecard, "occupation", "TEACHER");
        scorecardType.set(scorecard, "residenceState", "AP");
        scorecardType.set(scorecard, "validLicense", true);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals(41.0,scorecard.getCalculatedScore(), 0.0);
        //[OCC02, AGE03, VL001, RS001]
        assertEquals(4, scorecard.getReasonCodes().size());
        assertTrue(scorecard.getReasonCodes().contains("OCC99"));
        assertFalse(scorecard.getReasonCodes().contains("AGE03"));
        assertTrue(scorecard.getReasonCodes().contains("VL001"));
        assertTrue(scorecard.getReasonCodes().contains("RS001"));
    }

    @Test
    public void testDRLExecution() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        for (KnowledgeBuilderError error : kbuilder.getErrors()){
            System.out.println(error.getMessage());
        }
        assertFalse(kbuilder.hasErrors());
        //System.out.println(drl);

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );

        DroolsScorecard scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        //age = 30, validLicence -1, initialScore = 100;
        assertTrue(129 == scorecard.getCalculatedScore());
        //age-reasoncode=AGE02, license-reasoncode=VL002
        assertEquals(2, scorecard.getReasonCodes().size());
        //AGE02 - should be knocked out as we are using the pointsBelow Algorithm.
        assertEquals(-1, scorecard.getReasonCodes().indexOf("AGE02"));
        assertEquals(0, scorecard.getReasonCodes().indexOf("VL002"));
        assertEquals(1, scorecard.getReasonCodes().lastIndexOf("VL002"));

        session = kbase.newStatefulKnowledgeSession();
        scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 0);
        scorecardType.set(scorecard, "occupation", "SKYDIVER");
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1, initialScore = 100;
        assertEquals(99.0, scorecard.getCalculatedScore(), 0.0);

        assertEquals(3, scorecard.getReasonCodes().size());
        //[AGE01, VL002, OCC01]
        assertTrue(scorecard.getReasonCodes().contains("AGE01"));
        assertTrue(scorecard.getReasonCodes().contains("VL002"));
        assertTrue(scorecard.getReasonCodes().contains("OCC01"));

        session = kbase.newStatefulKnowledgeSession();
        scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 20);
        scorecardType.set(scorecard, "occupation", "TEACHER");
        scorecardType.set(scorecard, "residenceState", "AP");
        scorecardType.set(scorecard, "validLicense", true);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1, initialScore = 100;
        assertEquals(141.0,scorecard.getCalculatedScore(), 0.0);
        //[OCC02, AGE03, VL001, RS001]
        assertEquals(4, scorecard.getReasonCodes().size());
        assertTrue(scorecard.getReasonCodes().contains("OCC02"));
        assertFalse(scorecard.getReasonCodes().contains("AGE03"));
        assertTrue(scorecard.getReasonCodes().contains("VL001"));
        assertTrue(scorecard.getReasonCodes().contains("RS001"));
    }

    @Test
    public void testPointsAbove() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_pointsAbove");
        assertEquals(0, scorecardCompiler.getScorecardParseErrors().size());
        String drl = scorecardCompiler.getDRL();
        assertNotNull(drl);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        for (KnowledgeBuilderError error : kbuilder.getErrors()){
            System.out.println(error.getMessage());
        }
        assertFalse( kbuilder.hasErrors() );

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );

        DroolsScorecard scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        //age = 30, validLicence -1
        assertEquals(29.0, scorecard.getCalculatedScore(), 0.0);
        //age-reasoncode=AGE02, license-reasoncode=VL002
        assertEquals(2, scorecard.getReasonCodes().size());
        assertEquals(0, scorecard.getReasonCodes().indexOf("VL002"));
        //AGE02 - should be knocked out as we are using the pointsAbove Algorithm.
        assertEquals(-1, scorecard.getReasonCodes().indexOf("AGE02"));
        assertEquals(1, scorecard.getReasonCodes().lastIndexOf("VL002"));

        session = kbase.newStatefulKnowledgeSession();
        scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 0);
        scorecardType.set(scorecard, "occupation", "SKYDIVER");
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertTrue(-1 == scorecard.getCalculatedScore());
        assertEquals(3, scorecard.getReasonCodes().size());
        //[AGE01, VL002, OCC01]
        assertEquals(0, scorecard.getReasonCodes().indexOf("OCC01"));
        assertTrue("VL002".equalsIgnoreCase(scorecard.getReasonCodes().get(1)));
        assertTrue("VL002".equalsIgnoreCase(scorecard.getReasonCodes().get(2)));
        //AGE01 - should be knocked out as we are using the pointsAbove Algorithm.
        assertEquals(-1, scorecard.getReasonCodes().indexOf("AGE01"));

        session = kbase.newStatefulKnowledgeSession();
        scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 20);
        scorecardType.set(scorecard, "occupation", "TEACHER");
        scorecardType.set(scorecard, "residenceState", "AP");
        scorecardType.set(scorecard, "validLicense", true);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals(41.0,scorecard.getCalculatedScore(), 0.0);
        //[OCC02, AGE03, VL001, RS001]
        assertEquals(4, scorecard.getReasonCodes().size());
        assertEquals(-1, scorecard.getReasonCodes().indexOf("OCC02"));
        assertEquals(-1, scorecard.getReasonCodes().indexOf("AGE03"));
        assertEquals(-1, scorecard.getReasonCodes().indexOf("VL001"));
        assertEquals("RS001", scorecard.getReasonCodes().get(0));
        assertEquals("RS001", scorecard.getReasonCodes().get(1));
        assertEquals("RS001", scorecard.getReasonCodes().get(2));
        assertEquals("RS001", scorecard.getReasonCodes().get(3));
    }

    @Test
    public void testPointsBelow() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_pointsBelow");
        assertEquals(0, scorecardCompiler.getScorecardParseErrors().size());
        String drl = scorecardCompiler.getDRL();
        assertNotNull(drl);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        for (KnowledgeBuilderError error : kbuilder.getErrors()){
            System.out.println(error.getMessage());
        }
        assertFalse( kbuilder.hasErrors() );

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );

        DroolsScorecard scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        //age = 30, validLicence -1
        assertEquals(29.0, scorecard.getCalculatedScore(), 0.0);
        //age-reasoncode=AGE02, license-reasoncode=VL002
        assertEquals(2, scorecard.getReasonCodes().size());
        //VL002 - should be knocked out as we are using the pointsBelow Algorithm.
        assertEquals(0, scorecard.getReasonCodes().indexOf("VL002"));

        session = kbase.newStatefulKnowledgeSession();
        scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 0);
        scorecardType.set(scorecard, "occupation", "SKYDIVER");
        session.insert(scorecard);
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertTrue(-1 == scorecard.getCalculatedScore());
        assertEquals(3, scorecard.getReasonCodes().size());
        //[AGE01, VL002, OCC01]
        assertEquals(2, scorecard.getReasonCodes().indexOf("OCC01"));
        assertEquals(1, scorecard.getReasonCodes().indexOf("VL002"));
        assertEquals(0, scorecard.getReasonCodes().indexOf("AGE01"));

        session = kbase.newStatefulKnowledgeSession();
        scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 20);
        scorecardType.set(scorecard, "occupation", "TEACHER");
        scorecardType.set(scorecard, "residenceState", "AP");
        scorecardType.set(scorecard, "validLicense", true);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals(41.0,scorecard.getCalculatedScore(), 0.0);
        //[OCC02, AGE03, VL001, RS001]
        assertEquals(4, scorecard.getReasonCodes().size());
        assertEquals(2, scorecard.getReasonCodes().indexOf("OCC02"));
        assertEquals(1, scorecard.getReasonCodes().indexOf("RS001"));
        assertEquals(0, scorecard.getReasonCodes().indexOf("VL001"));
    }
}
