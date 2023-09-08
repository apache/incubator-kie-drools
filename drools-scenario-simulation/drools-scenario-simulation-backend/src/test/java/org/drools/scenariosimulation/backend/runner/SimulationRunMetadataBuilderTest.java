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
package org.drools.scenariosimulation.backend.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.AuditLogLine;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.junit.Test;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.drools.scenariosimulation.backend.TestUtils.commonCheckAuditLogLine;
import static org.drools.scenariosimulation.backend.TestUtils.getRandomlyGeneratedDMNMessageList;

public class SimulationRunMetadataBuilderTest {

    @Test
    public void build() {
        ScenarioWithIndex scenarioWithIndex1 = new ScenarioWithIndex(1, new Scenario());

        ScenarioResultMetadata result1 = new ScenarioResultMetadata(scenarioWithIndex1);
        result1.addExecuted("d1");
        result1.addExecuted("d2");
        result1.addAvailable("d1");
        result1.addAvailable("d2");
        result1.addAvailable("d3");

        List<DMNMessage> messagesResult1decision1 = getRandomlyGeneratedDMNMessageList();
        List<DMNMessage> messagesResult1decision2 = getRandomlyGeneratedDMNMessageList();

        Map<Integer, List<String>> expectedResult1Decision1 = fillAuditMessagesForDecision(result1, messagesResult1decision1, "d1");
        Map<Integer, List<String>> expectedResult1Decision2 = fillAuditMessagesForDecision(result1, messagesResult1decision2, "d2");

        ScenarioResultMetadata result2 = new ScenarioResultMetadata(new ScenarioWithIndex(2, new Scenario()));
        List<String> expectedDecisionsResult2 = List.of("d1", "d3");
        result2.addExecuted(expectedDecisionsResult2.get(0));
        result2.addExecuted(expectedDecisionsResult2.get(1));
        result2.addAvailable("d1");
        result2.addAvailable("d2");
        result2.addAvailable("d3");

        List<DMNMessage> messagesResult2decision1 = getRandomlyGeneratedDMNMessageList();
        List<DMNMessage> messagesResult2decision3 = getRandomlyGeneratedDMNMessageList();

        Map<Integer, List<String>> expectedResult2Decision1 = fillAuditMessagesForDecision(result1, messagesResult2decision1, "d1");
        Map<Integer, List<String>> expectedResult2Decision3 = fillAuditMessagesForDecision(result1, messagesResult2decision3, "d3");

        SimulationRunMetadataBuilder builder = SimulationRunMetadataBuilder.create();
        builder.addScenarioResultMetadata(result1);
        builder.addScenarioResultMetadata(result2);
        
        SimulationRunMetadata build = builder.build();

        assertThat(build.getAvailable()).isEqualTo(3);
        assertThat(build.getExecuted()).isEqualTo(3);
        assertThat(build.getCoveragePercentage()).isCloseTo(100, within(0.1));
        assertThat(build.getOutputCounter()).containsEntry("d1", 2).containsEntry("d2", 1);
        assertThat(build.getScenarioCounter().get(scenarioWithIndex1)).hasSize(2);
        
        AuditLog retrieved = build.getAuditLog();
        
        assertThat(retrieved).isNotNull();
        
        final List<AuditLogLine> auditLogLines = retrieved.getAuditLogLines();
        
        assertThat(auditLogLines).isNotNull().hasSize(messagesResult1decision1.size() + messagesResult1decision2.size() + messagesResult2decision1.size() + messagesResult2decision3.size());

        checkAuditLogLine(auditLogLines, expectedResult1Decision1, expectedResult1Decision2, expectedResult2Decision1, expectedResult2Decision3);
    }

    private void checkAuditLogLine(List<AuditLogLine> auditLogLines, Map<Integer, List<String>> ... expectedDecisionResults) {
        int auditLineCounter = 0;

        for (int externalCounter = 0; externalCounter < expectedDecisionResults.length; externalCounter ++) {

            Map<Integer, List<String>> expectedDecisionResult = expectedDecisionResults[externalCounter];

            for (int internalCounter = 0; internalCounter < expectedDecisionResult.size(); internalCounter ++) {
                List<String> expectedMessageParameters = expectedDecisionResult.get(internalCounter + 1);
                if (expectedMessageParameters.size() == 3) {
                    commonCheckAuditLogLine(auditLogLines.get(auditLineCounter), expectedMessageParameters.get(0), expectedMessageParameters.get(1), expectedMessageParameters.get(2));
                } else {
                    commonCheckAuditLogLine(auditLogLines.get(auditLineCounter), expectedMessageParameters.get(0), expectedMessageParameters.get(1));
                }
                auditLineCounter++;
            }

        }
    }

    private Map<Integer, List<String>> fillAuditMessagesForDecision(ScenarioResultMetadata scenarioResultMetadata, List<DMNMessage> messages, String decisionName) {
        Map<Integer, List<String>> expectedMessageParameters = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(0);
        messages.forEach(message -> {
            List<String> parameters = new ArrayList<>();
            final int i = counter.addAndGet(1);
            final DMNDecisionResult.DecisionEvaluationStatus status = DMNDecisionResult.DecisionEvaluationStatus.values()[new Random().nextInt(DMNDecisionResult.DecisionEvaluationStatus.values().length)];
            if (new Random().nextBoolean()) {
                scenarioResultMetadata.addAuditMessage(i, decisionName, status.toString());
                parameters.addAll(List.of(decisionName, status.toString()));
            } else {
                String messageContent = message.getLevel().name() + ": " + message.getText();
                scenarioResultMetadata.addAuditMessage(i, decisionName, status.toString(), messageContent);
                parameters.addAll(List.of(decisionName, status.toString(), messageContent));
            }
            expectedMessageParameters.put(i, parameters);
        });
        return expectedMessageParameters;
    }
}