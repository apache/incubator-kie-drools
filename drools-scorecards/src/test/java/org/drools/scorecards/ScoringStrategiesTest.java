package org.drools.scorecards;

import org.dmg.pmml.pmml_4_1.descr.Extension;
import org.dmg.pmml.pmml_4_1.descr.PMML;
import org.dmg.pmml.pmml_4_1.descr.Scorecard;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.io.InputStream;

import static junit.framework.Assert.*;
import static org.drools.scorecards.ScorecardCompiler.DrlType.INTERNAL_DECLARED_TYPES;

public class ScoringStrategiesTest {

    private PMML pmmlDocument;

    @Before
    public void setUp() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_scoring_strategies.xls")) ) {
            pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull(pmmlDocument);
            String drl = scorecardCompiler.getDRL();
            assertNotNull(drl);
        } else {
            fail("failed to parse scoremodel Excel.");
        }
    }

    @Test
    public void testScoringExtension() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                Scorecard scorecard = (Scorecard)serializable;
                assertEquals("Sample Score",scorecard.getModelName());
                Extension extension = ScorecardPMMLUtils.getExtension(scorecard.getExtensionsAndCharacteristicsAndMiningSchemas(), PMMLExtensionNames.SCORECARD_SCORING_STRATEGY);
                assertNotNull(extension);
                assertEquals(extension.getValue(), ScoringStrategy.AGGREGATE_SCORE.toString());
                return;
            }
        }
        fail();
    }

    @Test
    public void testScoringStrategyAggregate() throws Exception {

        double finalScore = executeAndFetchScore("scorecards");
        //age==10 (30), validLicense==FALSE (-1)
        assertEquals(29.0, finalScore);
    }

    @Test
    public void testScoringStrategyAverage() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_avg");
        //age==10 (30), validLicense==FALSE (-1)
        //count = 2
        assertEquals(14.5, finalScore);
    }

    @Test
    public void testScoringStrategyMinimum() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_min");
        //age==10 (30), validLicense==FALSE (-1)
        assertEquals(-1.0, finalScore);
    }

    @Test
    public void testScoringStrategyMaximum() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_max");
        //age==10 (30), validLicense==FALSE (-1)
        assertEquals(30.0, finalScore);
    }

    /* Tests with Initial Score */
    @Test
    public void testScoringStrategyAggregateInitialScore() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_initial_score");
        //age==10 (30), validLicense==FALSE (-1)
        //initialScore = 100
        assertEquals(129.0, finalScore);
    }

    @Test
    public void testScoringStrategyAverageInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_avg_initial_score");
        //age==10 (30), validLicense==FALSE (-1)
        //count = 2
        //initialScore = 100
        assertEquals(114.5, finalScore);
    }

    @Test
    public void testScoringStrategyMinimumInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_min_initial_score");
        //age==10 (30), validLicense==FALSE (-1)
        //initialScore = 100
        assertEquals(99.0, finalScore);
    }

    @Test
    public void testScoringStrategyMaximumInitialScore() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_max_initial_score");
        //age==10 (30), validLicense==FALSE (-1)
        //initialScore = 100
        assertEquals(130.0, finalScore);
    }

    /* Internal functions */
    private double executeAndFetchScore(String sheetName) throws Exception {

        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        scconf.setWorksheetName( sheetName );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newUrlResource(ScorecardProviderTest.class.getResource("/scoremodel_scoring_strategies.xls")),
                ResourceType.SCARD,
                scconf );
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        assertNotNull(kbase);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        StatelessKieSession session = kbase.newStatelessKnowledgeSession();
        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );
        DroolsScorecard scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.execute(scorecard);

        return scorecard.getCalculatedScore();

    }

}
