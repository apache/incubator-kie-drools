package org.drools.scorecards;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ScorecardsKModuleTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testScorecardFromKModule2() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieBase kBase = kContainer.getKieBase("namedkiesession");
        assertNotNull(kBase);

        KieSession kSession = kContainer.newKieSession("ksession1");
        assertNotNull(kSession);

        FactType scorecardType = kBase.getFactType( "org.drools.scorecards.example","SampleScore" );
        assertNotNull(scorecardType);

        Object scorecard = scorecardType.newInstance();
        assertNotNull(scorecard);

        scorecardType.set(scorecard, "age", 10);
        kSession.insert( scorecard );
        kSession.fireAllRules();
        kSession.dispose();
        //occupation = 5, age = 25, validLicence -1
        assertEquals( 29.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );
    }

    @Test
    public void testScorecardFromKBase2() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieBase kBase = kContainer.getKieBase("kbase2");
        assertNotNull(kBase);

        KieSession kSession = kContainer.newKieSession("ksession2");
        assertNotNull(kSession);

        FactType scorecardType = kBase.getFactType( "org.drools.scorecards.example","SampleScore" );
        assertNotNull(scorecardType);

        Object scorecard = scorecardType.newInstance();
        assertNotNull(scorecard);

        scorecardType.set(scorecard, "age", 50);
        scorecardType.set(scorecard, "validLicense", true);
        scorecardType.set(scorecard, "occupation", "PROGRAMMER");
        //occupation
        kSession.insert( scorecard );
        kSession.fireAllRules();
        kSession.dispose();
        //age = 25, validLicence 0, occupation=5
        assertEquals( 30.0, scorecardType.get( scorecard, "scorecard__calculatedScore" ) );
    }
}
