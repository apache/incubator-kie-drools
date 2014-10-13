package org.drools.scorecards;

import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.drools.pmml.pmml_4_2.PMML4Compiler;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.drools.scorecards.ScorecardCompiler.DrlType.INTERNAL_DECLARED_TYPES;

public class DrlFromPMMLTest {

    private static String drl;

    @Before
    public void setUp() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_c.xls")) ) {
            PMML pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull( pmmlDocument );
            PMML4Compiler.dumpModel( pmmlDocument, System.out );
            drl = scorecardCompiler.getDRL();
        } else {
            fail("failed to parse scoremodel Excel.");
        }
    }

    @Test
    public void testDrlNoNull() throws Exception {
        assertNotNull(drl);
        assertTrue(drl.length() > 0);
    }

    @Test
    public void testPackage() throws Exception {
        assertTrue( drl.contains( "package org.drools.scorecards.example" ) );
    }

    @Test
    public void testRuleCount() throws Exception {
        assertEquals( 61, StringUtil.countMatches( drl, "rule \"" ) );
    }

    @Test
    public void testImports() throws Exception {
        assertEquals( 2, StringUtil.countMatches(drl, "import ") );
    }

    @Test
    public void testDRLExecution() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources().newByteArrayResource( drl.getBytes() )
                           .setSourcePath( "test_scorecard_rules.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        Results res = kieBuilder.buildAll().getResults();
        KieContainer kieContainer = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() );

        KieBase kbase = kieContainer.getKieBase();
        KieSession session = kbase.newKieSession();



        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );
        Object scorecard = scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = 5, age = 25, validLicence -1
        assertEquals( 29.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        session = kbase.newKieSession();
        scorecard = scorecardType.newInstance();
        scorecardType.set(scorecard, "occupation", "SKYDIVER");
        scorecardType.set(scorecard, "age", 0);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertEquals( -1.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );

        session = kbase.newKieSession();
        scorecard = scorecardType.newInstance();
        scorecardType.set(scorecard, "residenceState", "AP");
        scorecardType.set(scorecard, "occupation", "TEACHER");
        scorecardType.set(scorecard, "age", 20);
        scorecardType.set(scorecard, "validLicense", true);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals( 41.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );
    }


}
