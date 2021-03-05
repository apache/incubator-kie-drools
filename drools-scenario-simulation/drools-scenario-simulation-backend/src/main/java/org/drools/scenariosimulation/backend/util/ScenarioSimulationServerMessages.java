/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.backend.util;

import java.util.List;

public class ScenarioSimulationServerMessages {

    private ScenarioSimulationServerMessages() {
        // Util class - Not instantiable
    }

    public static final String NULL = "null";

    public static String getFactWithWrongValueExceptionMessage(String factName, Object expectedValue, Object actualValue) {
        if (expectedValue == null) {
            expectedValue = NULL;
        }
        if (actualValue == null) {
            actualValue = NULL;
        }
        return String.format("Failed in \"%s\": The expected value is \"%s\" but the actual one is \"%s\"", factName, expectedValue, actualValue);
    }

    public static String getGenericScenarioExceptionMessage(String scenarioDescription, String exceptionMessage) {
        return  String.format("Scenario \"%s\" failed: %s", scenarioDescription, exceptionMessage);
    }

    public static String getGenericScenarioExceptionMessage(String scenarioDescription) {
        return  String.format("Scenario \"%s\" failed", scenarioDescription);
    }

    public static String getCollectionFactExceptionMessage(String factName, List<String> pathToWrongValue, Object wrongValue) {
        StringBuilder stringBuilder = new StringBuilder("Failed in \"").append(factName).append("\": ");
        if (pathToWrongValue.isEmpty()) {
            stringBuilder.append("Impossible to find elements in the collection to satisfy the conditions.");
        } else {
            if (wrongValue != null) {
                stringBuilder.append("Value \"").append(wrongValue).append("\" is wrong in ");
            } else {
                stringBuilder.append("Wrong in ");
            }
            stringBuilder.append("\"").append(String.join(".", pathToWrongValue)).append("\"");
        }
        return stringBuilder.toString();
    }

}