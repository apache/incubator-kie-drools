package org.drools.scenariosimulation.backend.runner.model;

import java.util.List;

import org.drools.scenariosimulation.api.model.AuditLogLine;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.core.DMNDecisionResult;

import static org.drools.scenariosimulation.backend.TestUtils.commonCheckAuditLogLine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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
    public void addAuditMessage() {
        assertTrue(scenarioResultMetadata.getAuditLogLines().isEmpty());
        int index = 1;
        String decisionName = "decisionName";
        String result = DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED.toString();
        scenarioResultMetadata.addAuditMessage(index, decisionName, result);
        final List<AuditLogLine> retrieved = scenarioResultMetadata.getAuditLogLines();
        assertEquals(1, retrieved.size());
        commonCheckAuditLogLine(retrieved.get(0), decisionName, result, null);
    }

    @Test
    public void addAuditMessageWithErrorMessage() {
        assertTrue(scenarioResultMetadata.getAuditLogLines().isEmpty());
        int index = 1;
        String decisionName = "decisionName";
        String result =  DMNDecisionResult.DecisionEvaluationStatus.FAILED.toString();
        String message = "Message";
        scenarioResultMetadata.addAuditMessage(index, decisionName, result, message);
        final List<AuditLogLine> retrieved = scenarioResultMetadata.getAuditLogLines();
        assertEquals(1, retrieved.size());
        commonCheckAuditLogLine(retrieved.get(0), decisionName, result, message);
    }
}
