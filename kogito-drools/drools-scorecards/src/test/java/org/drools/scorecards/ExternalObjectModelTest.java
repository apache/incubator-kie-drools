package org.drools.scorecards;

import org.dmg.pmml.pmml_4_1.descr.Extension;
import org.dmg.pmml.pmml_4_1.descr.Output;
import org.dmg.pmml.pmml_4_1.descr.OutputField;
import org.dmg.pmml.pmml_4_1.descr.PMML;
import org.dmg.pmml.pmml_4_1.descr.Scorecard;
import org.drools.pmml.pmml_4_1.PMML4Compiler;
import org.drools.pmml.pmml_4_1.PMML4Helper;
import org.drools.pmml.pmml_4_1.extensions.PMMLExtensionNames;
import org.drools.scorecards.example.Applicant;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.io.ResourceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.drools.scorecards.ScorecardCompiler.DrlType.EXTERNAL_OBJECT_MODEL;

public class ExternalObjectModelTest {
    private static ScorecardCompiler scorecardCompiler;

    @Before
    public void setUp() throws Exception {
        scorecardCompiler = new ScorecardCompiler(EXTERNAL_OBJECT_MODEL);
    }


    @Test
    public void testPMMLCustomOutput() throws Exception {
        PMML pmmlDocument = null;
        String drl = null;
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_externalmodel.xls")) ) {
            pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull( pmmlDocument );
            PMML4Compiler.dumpModel( pmmlDocument, System.out );
            drl = scorecardCompiler.getDRL();
            assertTrue( drl != null && ! drl.isEmpty() );
            //System.out.println(drl);
        } else {
            fail("failed to parse scoremodel Excel.");
        }

        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                Scorecard scorecard = (Scorecard)serializable;
                for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if ( obj instanceof Output) {
                        Output output = (Output)obj;
                        final List<OutputField> outputFields = output.getOutputFields();
                        assertEquals(1, outputFields.size());
                        final OutputField outputField = outputFields.get(0);
                        assertNotNull(outputField);
                        assertEquals("totalScore", outputField.getName());
                        assertEquals("Final Score", outputField.getDisplayName());
                        assertEquals("double", outputField.getDataType().value());
                        assertEquals("predictedValue", outputField.getFeature().value());
                        final Extension extension = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.EXTERNAL_CLASS );
                        assertNotNull(extension);
                        assertEquals("org.drools.scorecards.example.Applicant",extension.getValue());
                        return;
                    }
                }
            }
        }
        fail();
    }



    @Test
    public void testDRLExecution() throws Exception {
        PMML pmmlDocument = null;
        String drl = null;
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_externalmodel.xls")) ) {
            pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull( pmmlDocument );
            PMML4Compiler.dumpModel( pmmlDocument, System.out );
            drl = scorecardCompiler.getDRL();
            assertTrue( drl != null && ! drl.isEmpty() );
            //System.out.println(drl);
        } else {
            fail("failed to parse scoremodel Excel.");
        }

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources().newByteArrayResource( drl.getBytes() )
                           .setSourcePath( "test_scorecard_rules.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        Results res = kieBuilder.buildAll().getResults();
        System.err.print( res.getMessages() );
        KieContainer kieContainer = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() );

        KieBase kbase = kieContainer.getKieBase();
        KieSession session = kbase.newKieSession();

        Applicant applicant = new Applicant();
        applicant.setAge(10);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = 0, age = 30, validLicence -1
        assertEquals(29.0,applicant.getTotalScore());

        session = kbase.newKieSession();
        applicant = new Applicant();
        applicant.setOccupation("SKYDIVER");
        applicant.setAge(0);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertEquals(-1.0, applicant.getTotalScore());

        session = kbase.newKieSession();
        applicant = new Applicant();
        applicant.setResidenceState("AP");
        applicant.setOccupation("TEACHER");
        applicant.setAge(20);
        applicant.setValidLicense(true);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals(41.0,applicant.getTotalScore());
    }

    @Test
    public void testWithInitialScore() throws Exception {
        ScorecardCompiler scorecardCompiler2 = new ScorecardCompiler(EXTERNAL_OBJECT_MODEL);
        PMML pmmlDocument2 = null;
        String drl2 = null;
        if ( scorecardCompiler2.compileFromExcel( PMMLDocumentTest.class.getResourceAsStream("/scoremodel_externalmodel.xls" ), "scorecards_initialscore" ) ) {
            pmmlDocument2 = scorecardCompiler2.getPMMLDocument();
            assertNotNull(pmmlDocument2);
            drl2 = scorecardCompiler2.getDRL();
            PMML4Compiler.dumpModel( pmmlDocument2, System.out );
        } else {
            fail("failed to parse scoremodel Excel.");
        }
        assertNotNull( pmmlDocument2 );
        assertTrue( drl2 != null && ! drl2.isEmpty() );

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources().newByteArrayResource( drl2.getBytes() )
                           .setSourcePath( "test_scorecard_rules.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        Results res = kieBuilder.buildAll().getResults();
        System.err.println( res.getMessages() );
        KieContainer kieContainer = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() );

        KieBase kbase = kieContainer.getKieBase();
        KieSession session = kbase.newKieSession();

        Applicant applicant = new Applicant();
        applicant.setAge(10);
        session.insert(applicant);
        //session.addEventListener(new DebugWorkingMemoryEventListener());
        session.fireAllRules();
        session.dispose();
        //occupation = 0, age = 30, validLicence -1, initialScore=100
        assertEquals(129.0,applicant.getTotalScore());

        session = kbase.newKieSession();
        applicant = new Applicant();
        applicant.setOccupation("SKYDIVER");
        applicant.setAge(0);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1, initialScore=100;
        assertEquals(99.0, applicant.getTotalScore());

        session = kbase.newKieSession();
        applicant = new Applicant();
        applicant.setResidenceState("AP");
        applicant.setOccupation("TEACHER");
        applicant.setAge(20);
        applicant.setValidLicense(true);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1, initialScore=100
        assertEquals(141.0,applicant.getTotalScore());
    }

    @Test
    public void testWithReasonCodes() throws Exception {
        ScorecardCompiler scorecardCompiler2 = new ScorecardCompiler(EXTERNAL_OBJECT_MODEL);
        PMML pmmlDocument2 = null;
        String drl2 = null;
        if (scorecardCompiler2.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_externalmodel.xls"), "scorecards_reasoncode") ) {
            pmmlDocument2 = scorecardCompiler2.getPMMLDocument();
            PMML4Compiler.dumpModel( pmmlDocument2, System.out );
            assertNotNull( pmmlDocument2 );
            drl2 = scorecardCompiler2.getDRL();
            //System.out.println(drl2);
        } else {
            for (ScorecardError error : scorecardCompiler2.getScorecardParseErrors()){
                System.out.println(error.getErrorLocation()+":"+error.getErrorMessage());
            }
            fail("failed to parse scoremodel Excel (scorecards_reasoncode).");
        }
        assertNotNull( pmmlDocument2 );
        assertTrue( drl2 != null && ! drl2.isEmpty() );

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources().newByteArrayResource( drl2.getBytes() )
                           .setSourcePath( "test_scorecard_rules.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        Results res = kieBuilder.buildAll().getResults();
        KieContainer kieContainer = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() );

        KieBase kbase = kieContainer.getKieBase();
        KieSession session = kbase.newKieSession();

        FactType scorecardInternalsType = kbase.getFactType( PMML4Helper.pmmlDefaultPackageName(),"ScoreCard" );

        Applicant applicant = new Applicant();
        applicant.setAge(10);
        session.insert(applicant);
        //session.addEventListener(new DebugWorkingMemoryEventListener());
        session.fireAllRules();
        //occupation = 0, age = 30, validLicence -1, initialScore=100
        assertEquals( 129.0,applicant.getTotalScore() );
        assertEquals( "VL0099", applicant.getReasonCodes() );

        Object scorecardInternals = session.getObjects( new ClassObjectFilter( scorecardInternalsType.getFactClass() ) ).iterator().next();
        Assert.assertEquals( 129.0, scorecardInternalsType.get( scorecardInternals, "score" ) );
        Map reasonCodesMap = (Map) scorecardInternalsType.get( scorecardInternals, "ranking" );
        Assert.assertNotNull( reasonCodesMap );
        Assert.assertEquals( Arrays.asList( "VL0099", "AGE02" ), new ArrayList( reasonCodesMap.keySet() ) );



        session.dispose();

        session = kbase.newKieSession();
        applicant = new Applicant();
        applicant.setOccupation("SKYDIVER");
        applicant.setAge(0);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1, initialScore=100;
        assertEquals(99.0, applicant.getTotalScore());

        session = kbase.newKieSession();
        applicant = new Applicant();
        applicant.setResidenceState("AP");
        applicant.setOccupation("TEACHER");
        applicant.setAge(20);
        applicant.setValidLicense(true);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1, initialScore=100
        assertEquals(141.0,applicant.getTotalScore());
    }
}
