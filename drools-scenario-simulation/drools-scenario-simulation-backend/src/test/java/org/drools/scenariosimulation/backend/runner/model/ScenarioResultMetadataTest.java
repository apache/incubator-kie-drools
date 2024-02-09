/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
