/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.backend.util;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScenarioSimulationServerMessagesTest {

    @Test
    public void getFactWithWrongValueExceptionMessage() {
        String factName = "Fact.name";
        String testResult = ScenarioSimulationServerMessages.getFactWithWrongValueExceptionMessage(factName, null, null);
        assertThat(testResult).isEqualTo("Failed in \"Fact.name\": The expected value is \"" + ScenarioSimulationServerMessages.NULL + "\" but the actual one is \"" + ScenarioSimulationServerMessages.NULL + "\"");
        testResult = ScenarioSimulationServerMessages.getFactWithWrongValueExceptionMessage(factName, 1, null);
        assertThat(testResult).isEqualTo("Failed in \"Fact.name\": The expected value is \"" + 1 + "\" but the actual one is \"" + ScenarioSimulationServerMessages.NULL + "\"");
        testResult = ScenarioSimulationServerMessages.getFactWithWrongValueExceptionMessage(factName, null, "value");
        assertThat(testResult).isEqualTo("Failed in \"Fact.name\": The expected value is \"" + ScenarioSimulationServerMessages.NULL + "\" but the actual one is \"value\"");
    }

    @Test
    public void getGenericScenarioExceptionMessage() {
        assertThat(ScenarioSimulationServerMessages.getGenericScenarioExceptionMessage("An exception message")).isEqualTo("Failure reason: An exception message");
    }

    @Test
    public void getCollectionFactExceptionMessage() {
        String factName = "Fact.name";
        String wrongValue = "value";
        String testResult = ScenarioSimulationServerMessages.getCollectionFactExceptionMessage(factName, Collections.emptyList(), wrongValue);
        assertThat(testResult).isEqualTo("Failed in \"Fact.name\": Impossible to find elements in the collection to satisfy the conditions.");
        testResult = ScenarioSimulationServerMessages.getCollectionFactExceptionMessage(factName, Arrays.asList("Item #2"), wrongValue);
        assertThat(testResult).isEqualTo("Failed in \"Fact.name\": Value \"value\" is wrong in \"Item #2\"");
        testResult = ScenarioSimulationServerMessages.getCollectionFactExceptionMessage(factName, Arrays.asList("Item #2"), null);
        assertThat(testResult).isEqualTo("Failed in \"Fact.name\": Wrong in \"Item #2\"");

    }

    @Test
    public void getIndexedScenarioMessage() {
        String failureMessage = "Failure message";
        String scenarioDescription = "First Case";
        String fileName = "ScesimTest";
        String testResult = ScenarioSimulationServerMessages.getIndexedScenarioMessage(failureMessage, 1, scenarioDescription, fileName);
        assertThat(testResult).isEqualTo("#1 First Case: Failure message (ScesimTest)");
        testResult = ScenarioSimulationServerMessages.getIndexedScenarioMessage(failureMessage, 1, scenarioDescription, null);
        assertThat(testResult).isEqualTo("#1 First Case: Failure message");
        testResult = ScenarioSimulationServerMessages.getIndexedScenarioMessage(failureMessage, 1, "", fileName);
        assertThat(testResult).isEqualTo("#1: Failure message (ScesimTest)");
        testResult = ScenarioSimulationServerMessages.getIndexedScenarioMessage(failureMessage, 1, null, fileName);
        assertThat(testResult).isEqualTo("#1: Failure message (ScesimTest)");
    }

}
