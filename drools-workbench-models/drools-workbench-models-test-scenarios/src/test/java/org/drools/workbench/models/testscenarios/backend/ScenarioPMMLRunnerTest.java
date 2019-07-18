package org.drools.workbench.models.testscenarios.backend;

import java.util.Arrays;

import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyScorecardScore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.soup.project.datamodel.imports.Import;

import static org.junit.Assert.assertEquals;

public class ScenarioPMMLRunnerTest
        extends org.drools.workbench.models.testscenarios.backend.RuleUnit {

    @Test
    public void testScoreCardPasses() throws Exception {
        runScorecardTest("abc", true);
    }

    @Test
    public void testScoreCardFails() throws Exception {
        runScorecardTest("wrongValue", false);
    }

    private void runScorecardTest(final String nameValue,
                                  final boolean expected) throws Exception {
        KieSession kieSession = getKieSession("test_scard.scgd");

        ScenarioRunner runner = new ScenarioPMMLRunner(kieSession.getKieBase());

        Scenario scenario = new Scenario();
        scenario.getImports().addImport(new Import("org.drools.workbench.models.testscenarios.backend.Person"));
        scenario.setPackageName("org.drools.workbench.models.testscenarios.backend");
        scenario.setModelName("Scard");

        scenario.getFixtures().add(
                new FactData(
                        "Person",
                        "f2",
                        Arrays.asList(
                                new FieldData(
                                        "name",
                                        nameValue)),
                        false
                ));

        scenario.getFixtures().add(new ExecutionTrace());

        VerifyScorecardScore verifyScorecardScore = new VerifyScorecardScore(25.0);
        scenario.getFixtures().add(verifyScorecardScore);

        runner.run(scenario);

        assertEquals(expected, verifyScorecardScore.wasSuccessful());
    }
}