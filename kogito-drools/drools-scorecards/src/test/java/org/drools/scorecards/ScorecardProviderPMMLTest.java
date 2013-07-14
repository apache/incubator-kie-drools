package org.drools.scorecards;


import org.drools.compiler.compiler.ScoreCardFactory;
import org.drools.compiler.compiler.ScoreCardProvider;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.io.InputStream;

import static junit.framework.Assert.*;
import static org.kie.internal.builder.ScoreCardConfiguration.SCORECARD_INPUT_TYPE;


public class ScorecardProviderPMMLTest {
    private static String drl;
    private ScoreCardProvider scoreCardProvider;

    @Before
    public void setUp() throws Exception {
        scoreCardProvider = ScoreCardFactory.getScoreCardProvider();
        assertNotNull(scoreCardProvider);
    }

    @Test
    public void testDrlGeneration() throws Exception {
        InputStream is = ScorecardProviderPMMLTest.class.getResourceAsStream("/SimpleScorecard.pmml");
        assertNotNull(is);

        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        scconf.setInputType(SCORECARD_INPUT_TYPE.PMML);
        drl = scoreCardProvider.loadFromInputStream(is, scconf);
        assertNotNull(drl);
        assertTrue(drl.length() > 0);
    }

    @Test
    public void testKnowledgeBaseWithExecution() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        scconf.setInputType(SCORECARD_INPUT_TYPE.PMML);
        kbuilder.add( ResourceFactory.newUrlResource(ScorecardProviderPMMLTest.class.getResource("/SimpleScorecard.pmml")),
            ResourceType.SCARD,
            scconf );
        assertFalse( kbuilder.hasErrors() );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        assertNotNull(kbase);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );
        assertNotNull(scorecardType);

        DroolsScorecard scorecard = (DroolsScorecard) scorecardType.newInstance();
        assertNotNull(scorecard);

        scorecardType.set(scorecard, "age", 10);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = 5, age = 25, validLicence -1
        assertEquals(29.0,scorecard.getCalculatedScore());

    }
}