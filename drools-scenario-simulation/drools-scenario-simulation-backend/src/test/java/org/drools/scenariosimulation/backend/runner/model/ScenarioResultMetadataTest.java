package org.drools.scenariosimulation.backend.runner.model;

import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.scenariosimulation.backend.TestUtils.commonCheckAuditLogLine;
import static org.mockito.Mockito.mock;
import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.FAILED;
import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED;

public class ScenarioResultMetadataTest {

    private ScenarioResultMetadata scenarioResultMetadata;
    private ScenarioWithIndex scenarioWithIndex;
    private Scenario scenarioMock;
    private int SCENARIO_INDEX = 0;

    @Before
    public void setup() {
        scenarioMock = mock(Scenario.class);
        scenarioWithIndex = new ScenarioWithIndex(SCENARIO_INDEX, scenarioMock);
        scenarioResultMetadata = new ScenarioResultMetadata(scenarioWithIndex);
    }

    @Test
    public void noLogLinesAtTheStart() {
        assertThat(scenarioResultMetadata.getAuditLogLines()).isEmpty();
    }

    
    @Test
    public void addAuditMessage() {
        scenarioResultMetadata.addAuditMessage(1, "decisionName", SUCCEEDED.toString());
        
        assertThat(scenarioResultMetadata.getAuditLogLines()).hasSize(1);
        commonCheckAuditLogLine(scenarioResultMetadata.getAuditLogLines().get(0), "decisionName", SUCCEEDED.toString());
    }

    @Test
    public void addAuditMessageWithErrorMessage() {
        scenarioResultMetadata.addAuditMessage(1, "decisionName", FAILED.toString(), "Message");
        
        assertThat(scenarioResultMetadata.getAuditLogLines()).hasSize(1);
        commonCheckAuditLogLine(scenarioResultMetadata.getAuditLogLines().get(0), "decisionName", FAILED.toString(), "Message");
    }
}
