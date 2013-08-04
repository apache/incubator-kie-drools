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


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testScoringExtension() throws Exception {
        PMML pmmlDocument;
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_scoring_strategies.xls")) ) {
            pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull(pmmlDocument);
            String drl = scorecardCompiler.getDRL();
            assertNotNull(drl);
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
        }
        fail();
    }

    @Test
    public void testAggregate() throws Exception {

        double finalScore = executeAndFetchScore("scorecards");
        //age==10 (30), validLicense==FALSE (-1)
        assertEquals(29.0, finalScore);
    }

    @Test
    public void testAverage() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_avg");
        //age==10 (30), validLicense==FALSE (-1)
        //count = 2
        assertEquals(14.5, finalScore);
    }

    @Test
    public void testMinimum() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_min");
        //age==10 (30), validLicense==FALSE (-1)
        assertEquals(-1.0, finalScore);
    }

    @Test
    public void testMaximum() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_max");
        //age==10 (30), validLicense==FALSE (-1)
        assertEquals(30.0, finalScore);
    }

    @Test
    public void testWeightedAggregate() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_w_aggregate");
        //age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        assertEquals(599.0, finalScore);
    }

    @Test
    public void testWeightedAverage() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_w_avg");
        //age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        assertEquals(299.5, finalScore);
    }

    @Test
    public void testWeightedMaximum() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_w_max");
        //age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        assertEquals(600.0, finalScore);
    }

    @Test
    public void testWeightedMinimum() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_w_min");
        //age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        assertEquals(-1.0, finalScore);
    }

    /* Tests with Initial Score */
    @Test
    public void testAggregateInitialScore() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_initial_score");
        //age==10 (30), validLicense==FALSE (-1)
        //initialScore = 100
        assertEquals(129.0, finalScore);
    }

    @Test
    public void testAverageInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_avg_initial_score");
        //age==10 (30), validLicense==FALSE (-1)
        //count = 2
        //initialScore = 100
        assertEquals(114.5, finalScore);
    }

    @Test
    public void testMinimumInitialScore() throws Exception {
        double finalScore = executeAndFetchScore("scorecards_min_initial_score");
        //age==10 (30), validLicense==FALSE (-1)
        //initialScore = 100
        assertEquals(99.0, finalScore);
    }

    @Test
    public void testMaximumInitialScore() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_max_initial_score");
        //age==10 (30), validLicense==FALSE (-1)
        //initialScore = 100
        assertEquals(130.0, finalScore);
    }

    @Test
    public void testWeightedAggregateInitialScore() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_w_aggregate_initial");
        //age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        //initialScore = 100
        assertEquals(699.0, finalScore);
    }

    @Test
    public void testWeightedAverageInitialScore() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_w_avg_initial");
        //age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        //initialScore = 100
        assertEquals(399.5, finalScore);
    }

    @Test
    public void testWeightedMaximumInitialScore() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_w_max_initial");
        //age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        //initialScore = 100
        assertEquals(700.0, finalScore);
    }

    @Test
    public void testWeightedMinimumInitialScore() throws Exception {

        double finalScore = executeAndFetchScore("scorecards_w_min_initial");
        //age==10 (score=30, w=20), validLicense==FALSE (score=-1, w=1)
        //initialScore = 100
        assertEquals(99.0, finalScore);
    }

    /* Internal functions */
    private double executeAndFetchScore(String sheetName) throws Exception {

        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        InputStream inputStream = PMMLDocumentTest.class.getResourceAsStream("/scoremodel_scoring_strategies.xls");
        boolean compileResult = scorecardCompiler.compileFromExcel(inputStream, sheetName);
        if (!compileResult) {
            for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
                System.err.println("Scorecard Compiler Error :"+error.getErrorLocation()+"->"+error.getErrorMessage());
            }
            return -999999;
        }
        String drl = scorecardCompiler.getDRL();
        //System.out.println(drl);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        for (KnowledgeBuilderError error : kbuilder.getErrors()){
            System.out.println(error.getMessage());
        }
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
