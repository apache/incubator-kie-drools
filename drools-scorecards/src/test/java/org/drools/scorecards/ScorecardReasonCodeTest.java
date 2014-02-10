package org.drools.scorecards;

import org.dmg.pmml.pmml_4_1.descr.Attribute;
import org.dmg.pmml.pmml_4_1.descr.Characteristic;
import org.dmg.pmml.pmml_4_1.descr.Characteristics;
import org.dmg.pmml.pmml_4_1.descr.PMML;
import org.dmg.pmml.pmml_4_1.descr.Scorecard;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.drools.scorecards.ScorecardCompiler.DrlType.INTERNAL_DECLARED_TYPES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ScorecardReasonCodeTest {

    private static PMML pmmlDocument;
    private static ScorecardCompiler scorecardCompiler;


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
        scorecardCompiler.compileFromExcel( PMMLDocumentTest.class.getResourceAsStream( "/scoremodel_c.xls" ) );
        PMML pmml = scorecardCompiler.getPMMLDocument();
        for (Object serializable : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                assertFalse(((Scorecard) serializable).isUseReasonCodes());
            }
        }
    }

    @Test
    public void testUseReasonCodes() throws Exception {
        scorecardCompiler = new ScorecardCompiler( INTERNAL_DECLARED_TYPES );
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"));
        if (!compileResult) {
            for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
                System.out.println("setup :"+error.getErrorLocation()+"->"+error.getErrorMessage());
            }
        }

        pmmlDocument = scorecardCompiler.getPMMLDocument();

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
        scorecardCompiler = new ScorecardCompiler( INTERNAL_DECLARED_TYPES );
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"));
        if (!compileResult) {
            for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
                System.out.println("setup :"+error.getErrorLocation()+"->"+error.getErrorMessage());
            }
        }

        pmmlDocument = scorecardCompiler.getPMMLDocument();

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
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler( INTERNAL_DECLARED_TYPES );
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"));
        if (!compileResult) {
            for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
                System.out.println("setup :"+error.getErrorLocation()+"->"+error.getErrorMessage());
            }
        }

        pmmlDocument = scorecardCompiler.getPMMLDocument();

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

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources().newClassPathResource( "scoremodel_reasoncodes.xls" )
                           .setSourcePath( "scoremodel_reasoncodes.xls" )
                           .setResourceType( ResourceType.SCARD ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        Results res = kieBuilder.buildAll().getResults();
        KieContainer kieContainer = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() );

        KieBase kbase = kieContainer.getKieBase();
        KieSession session = kbase.newKieSession();


        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );
        FactType scorecardInternalsType = kbase.getFactType( "org.drools.scorecards.example","ScoreCard" );
        FactType scorecardOutputType = kbase.getFactType( "org.drools.scorecards.example","SampleScoreOutput" );

        Object scorecard = scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.insert(scorecard);
        session.fireAllRules();
        assertEquals( 129.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        Object scorecardInternals = session.getObjects( new ClassObjectFilter( scorecardInternalsType.getFactClass() ) ).iterator().next();
        assertEquals( 129.0, scorecardInternalsType.get( scorecardInternals, "score" ) );
        Map reasonCodesMap = (Map) scorecardInternalsType.get( scorecardInternals, "ranking" );
        assertNotNull( reasonCodesMap );
        assertEquals( 2, reasonCodesMap.size() );
        assertEquals( 16.0, reasonCodesMap.get( "VL002" ) );
        assertEquals( -20.0, reasonCodesMap.get( "AGE02" ) );

        Object scorecardOutput = session.getObjects( new ClassObjectFilter( scorecardOutputType.getFactClass() ) ).iterator().next();
        assertEquals( 129.0, scorecardOutputType.get( scorecardOutput, "calculatedScore" ) );
        assertEquals( "VL002", scorecardOutputType.get( scorecardOutput, "reasonCode" ) );

        session.dispose();


        session = kbase.newKieSession();
        scorecard = scorecardType.newInstance();
        scorecardType.set( scorecard, "age", 0 );
        scorecardType.set( scorecard, "occupation", "SKYDIVER" );
        session.insert( scorecard );
        session.fireAllRules();

        assertEquals( 99.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        scorecardInternals = session.getObjects( new ClassObjectFilter( scorecardInternalsType.getFactClass() ) ).iterator().next();
        System.out.println( scorecardInternals );
        assertEquals( 99.0, scorecardInternalsType.get( scorecardInternals, "score" ) );
        reasonCodesMap = (Map) scorecardInternalsType.get( scorecardInternals, "ranking" );
        assertNotNull( reasonCodesMap );
        assertEquals( 3, reasonCodesMap.size() );
        assertEquals( 109.0, reasonCodesMap.get( "OCC01" ) );
        assertEquals( 16.0, reasonCodesMap.get( "VL002" ) );
        assertEquals( 0.0, reasonCodesMap.get( "AGE01" ) );

        scorecardOutput = session.getObjects( new ClassObjectFilter( scorecardOutputType.getFactClass() ) ).iterator().next();
        assertEquals( 99.0, scorecardOutputType.get( scorecardOutput, "calculatedScore" ) );
        assertEquals( "OCC01", scorecardOutputType.get( scorecardOutput, "reasonCode" ) );


        session.dispose();

        session = kbase.newKieSession();
        scorecard = scorecardType.newInstance();
        scorecardType.set( scorecard, "age", 20 );
        scorecardType.set( scorecard, "occupation", "TEACHER" );
        scorecardType.set( scorecard, "residenceState", "AP" );
        scorecardType.set( scorecard, "validLicense", true );
        session.insert( scorecard );
        session.fireAllRules();

        assertEquals( 141.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        scorecardInternals = session.getObjects( new ClassObjectFilter( scorecardInternalsType.getFactClass() ) ).iterator().next();
        System.out.println( scorecardInternals );
        assertEquals( 141.0, scorecardInternalsType.get( scorecardInternals, "score" ) );
        reasonCodesMap = (Map) scorecardInternalsType.get( scorecardInternals, "ranking" );
        assertNotNull( reasonCodesMap );
        assertEquals( 4, reasonCodesMap.size() );
        assertEquals( 89.0, reasonCodesMap.get( "OCC02" ) );
        assertEquals( 22.0, reasonCodesMap.get( "RS001" ) );
        assertEquals( 14.0, reasonCodesMap.get( "VL001" ) );
        assertEquals( -30.0, reasonCodesMap.get( "AGE03" ) );


        scorecardOutput = session.getObjects( new ClassObjectFilter( scorecardOutputType.getFactClass() ) ).iterator().next();
        assertEquals( 141.0, scorecardOutputType.get( scorecardOutput, "calculatedScore" ) );
        assertEquals( "OCC02", scorecardOutputType.get( scorecardOutput, "reasonCode" ) );

        session.dispose();
    }






    @Test
    public void testPointsAbove() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel( PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_pointsAbove" );
        assertEquals( 0, scorecardCompiler.getScorecardParseErrors().size() );

        String drl = scorecardCompiler.getDRL();
        assertNotNull(drl);


        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources().newByteArrayResource( drl.getBytes() )
                           .setSourcePath( "scoremodel_pointsAbove.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        Results res = kieBuilder.buildAll().getResults();
        KieContainer kieContainer = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() );

        KieBase kbase = kieContainer.getKieBase();
        KieSession session = kbase.newKieSession();

        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );
        FactType scorecardInternalsType = kbase.getFactType( "org.drools.scorecards.example","ScoreCard" );
        FactType scorecardOutputType = kbase.getFactType( "org.drools.scorecards.example","SampleScoreOutput" );

        Object scorecard = scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.insert(scorecard);
        session.fireAllRules();
        assertEquals( 29.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        Object scorecardInternals = session.getObjects( new ClassObjectFilter( scorecardInternalsType.getFactClass() ) ).iterator().next();
        assertEquals( 29.0, scorecardInternalsType.get( scorecardInternals, "score" ) );
        Map reasonCodesMap = (Map) scorecardInternalsType.get( scorecardInternals, "ranking" );
        assertNotNull( reasonCodesMap );
        assertEquals( 2, reasonCodesMap.size() );
        assertEquals( -16.0, reasonCodesMap.get( "VL002" ) );
        assertEquals( 20.0, reasonCodesMap.get( "AGE02" ) );

        Object scorecardOutput = session.getObjects( new ClassObjectFilter( scorecardOutputType.getFactClass() ) ).iterator().next();
        assertEquals( 29.0, scorecardOutputType.get( scorecardOutput, "calculatedScore" ) );
        assertEquals( "AGE02", scorecardOutputType.get( scorecardOutput, "reasonCode" ) );

        session.dispose();




        session = kbase.newKieSession();
        scorecard = scorecardType.newInstance();
        scorecardType.set( scorecard, "age", 0 );
        scorecardType.set( scorecard, "occupation", "SKYDIVER" );
        session.insert( scorecard );
        session.fireAllRules();

        assertEquals( -1.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        scorecardInternals = session.getObjects( new ClassObjectFilter( scorecardInternalsType.getFactClass() ) ).iterator().next();
        System.out.println( scorecardInternals );
        assertEquals( -1.0, scorecardInternalsType.get( scorecardInternals, "score" ) );
        reasonCodesMap = (Map) scorecardInternalsType.get( scorecardInternals, "ranking" );
        assertNotNull( reasonCodesMap );
        assertEquals( 3, reasonCodesMap.size() );
        assertEquals( -109.0, reasonCodesMap.get( "OCC01" ) );
        assertEquals( -16.0, reasonCodesMap.get( "VL002" ) );
        assertEquals( 0.0, reasonCodesMap.get( "AGE01" ) );
        assertEquals( Arrays.asList( "AGE01", "VL002", "OCC01" ), new ArrayList( reasonCodesMap.keySet() ) );

        scorecardOutput = session.getObjects( new ClassObjectFilter( scorecardOutputType.getFactClass() ) ).iterator().next();
        assertEquals( -1.0, scorecardOutputType.get( scorecardOutput, "calculatedScore" ) );
        assertEquals( "AGE01", scorecardOutputType.get( scorecardOutput, "reasonCode" ) );

        session.dispose();


        session = kbase.newKieSession();
        scorecard = scorecardType.newInstance();
        scorecardType.set( scorecard, "age", 20 );
        scorecardType.set( scorecard, "occupation", "TEACHER" );
        scorecardType.set( scorecard, "residenceState", "AP" );
        scorecardType.set( scorecard, "validLicense", true );
        session.insert( scorecard );
        session.fireAllRules();

        assertEquals( 41.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        scorecardInternals = session.getObjects( new ClassObjectFilter( scorecardInternalsType.getFactClass() ) ).iterator().next();
        System.out.println( scorecardInternals );
        assertEquals( 41.0, scorecardInternalsType.get( scorecardInternals, "score" ) );
        reasonCodesMap = (Map) scorecardInternalsType.get( scorecardInternals, "ranking" );
        assertNotNull( reasonCodesMap );
        assertEquals( 4, reasonCodesMap.size() );
        assertEquals( -89.0, reasonCodesMap.get( "OCC02" ) );
        assertEquals( -22.0, reasonCodesMap.get( "RS001" ) );
        assertEquals( -14.0, reasonCodesMap.get( "VL001" ) );
        assertEquals( 30.0, reasonCodesMap.get( "AGE03" ) );
        assertEquals( Arrays.asList( "AGE03", "VL001", "RS001", "OCC02" ), new ArrayList( reasonCodesMap.keySet() ) );


        scorecardOutput = session.getObjects( new ClassObjectFilter( scorecardOutputType.getFactClass() ) ).iterator().next();
        assertEquals( 41.0, scorecardOutputType.get( scorecardOutput, "calculatedScore" ) );
        assertEquals( "AGE03", scorecardOutputType.get( scorecardOutput, "reasonCode" ) );

        session.dispose();

    }




    @Test
    public void testPointsBelow() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_pointsBelow");
        assertEquals(0, scorecardCompiler.getScorecardParseErrors().size());
        String drl = scorecardCompiler.getDRL();


        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources().newByteArrayResource( drl.getBytes() )
                           .setSourcePath( "scoremodel_pointsAbove.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        Results res = kieBuilder.buildAll().getResults();
        KieContainer kieContainer = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() );

        KieBase kbase = kieContainer.getKieBase();
        KieSession session = kbase.newKieSession();

        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );
        FactType scorecardInternalsType = kbase.getFactType( "org.drools.scorecards.example","ScoreCard" );
        FactType scorecardOutputType = kbase.getFactType( "org.drools.scorecards.example","SampleScoreOutput" );

        Object scorecard = scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.insert(scorecard);
        session.fireAllRules();
        assertEquals( 29.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        Object scorecardInternals = session.getObjects( new ClassObjectFilter( scorecardInternalsType.getFactClass() ) ).iterator().next();
        assertEquals( 29.0, scorecardInternalsType.get( scorecardInternals, "score" ) );
        Map reasonCodesMap = (Map) scorecardInternalsType.get( scorecardInternals, "ranking" );
        assertNotNull( reasonCodesMap );
        assertEquals( 2, reasonCodesMap.size() );
        assertEquals( 16.0, reasonCodesMap.get( "VL002" ) );
        assertEquals( -20.0, reasonCodesMap.get( "AGE02" ) );

        Object scorecardOutput = session.getObjects( new ClassObjectFilter( scorecardOutputType.getFactClass() ) ).iterator().next();
        assertEquals( 29.0, scorecardOutputType.get( scorecardOutput, "calculatedScore" ) );
        assertEquals( "VL002", scorecardOutputType.get( scorecardOutput, "reasonCode" ) );

        session.dispose();


        session = kbase.newKieSession();
        scorecard = scorecardType.newInstance();
        scorecardType.set( scorecard, "age", 0 );
        scorecardType.set( scorecard, "occupation", "SKYDIVER" );
        session.insert( scorecard );
        session.fireAllRules();

        assertEquals( -1.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        scorecardInternals = session.getObjects( new ClassObjectFilter( scorecardInternalsType.getFactClass() ) ).iterator().next();
        System.out.println( scorecardInternals );
        assertEquals( -1.0, scorecardInternalsType.get( scorecardInternals, "score" ) );
        reasonCodesMap = (Map) scorecardInternalsType.get( scorecardInternals, "ranking" );
        assertNotNull( reasonCodesMap );
        assertEquals( 3, reasonCodesMap.size() );
        assertEquals( 109.0, reasonCodesMap.get( "OCC01" ) );
        assertEquals( 16.0, reasonCodesMap.get( "VL002" ) );
        assertEquals( 0.0, reasonCodesMap.get( "AGE01" ) );

        scorecardOutput = session.getObjects( new ClassObjectFilter( scorecardOutputType.getFactClass() ) ).iterator().next();
        assertEquals( -1.0, scorecardOutputType.get( scorecardOutput, "calculatedScore" ) );
        assertEquals( "OCC01", scorecardOutputType.get( scorecardOutput, "reasonCode" ) );


        session.dispose();

        session = kbase.newKieSession();
        scorecard = scorecardType.newInstance();
        scorecardType.set( scorecard, "age", 20 );
        scorecardType.set( scorecard, "occupation", "TEACHER" );
        scorecardType.set( scorecard, "residenceState", "AP" );
        scorecardType.set( scorecard, "validLicense", true );
        session.insert( scorecard );
        session.fireAllRules();

        assertEquals( 41.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        scorecardInternals = session.getObjects( new ClassObjectFilter( scorecardInternalsType.getFactClass() ) ).iterator().next();
        System.out.println( scorecardInternals );
        assertEquals( 41.0, scorecardInternalsType.get( scorecardInternals, "score" ) );
        reasonCodesMap = (Map) scorecardInternalsType.get( scorecardInternals, "ranking" );
        assertNotNull( reasonCodesMap );
        assertEquals( 4, reasonCodesMap.size() );
        assertEquals( 89.0, reasonCodesMap.get( "OCC02" ) );
        assertEquals( 22.0, reasonCodesMap.get( "RS001" ) );
        assertEquals( 14.0, reasonCodesMap.get( "VL001" ) );
        assertEquals( -30.0, reasonCodesMap.get( "AGE03" ) );


        scorecardOutput = session.getObjects( new ClassObjectFilter( scorecardOutputType.getFactClass() ) ).iterator().next();
        assertEquals( 41.0, scorecardOutputType.get( scorecardOutput, "calculatedScore" ) );
        assertEquals( "OCC02", scorecardOutputType.get( scorecardOutput, "reasonCode" ) );

        session.dispose();
    }



}
