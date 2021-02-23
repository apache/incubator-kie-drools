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

    public final static String UNKNOWN_ERROR = "Unknown Error";
    public final static String NULL = "null";

    public static String getUnknownErrorMessage() {
        return UNKNOWN_ERROR;
    }

    public static String getFactWithWrongValueExceptionMessage(Object expectedValue, Object actualValue) {
        if (expectedValue == null) {
            expectedValue = NULL;
        }
        if (actualValue == null) {
            actualValue = NULL;
        }
        return String.format("The expected value is %s but the actual one is %s", expectedValue, actualValue);
    }






    public static String getGenericScenarioExceptionMessage(String scenarioDescription) {
        return  "Scenario '" + scenarioDescription + "' failed";
    }

    public static String getGenericCollectionErrorMessage() {
        return "Impossible to find elements in the collection to satisfy the conditions.";
    }

    public static String getCollectionExceptionMessage(String wrongValue, List<String> pathToWrongValue) {
        if (wrongValue == null) {
            return getCollectionWithoutWrongValueExceptionMessage(pathToWrongValue);
        }
        return "Value \"" + wrongValue + "\" is wrong inside: \"" +
                String.join(".", pathToWrongValue) + "\"";
    }

    private static String getCollectionWithoutWrongValueExceptionMessage(List<String> pathToWrongValue) {
        if (pathToWrongValue == null || pathToWrongValue.isEmpty()) {
            return "";
        }
        return "Following path \"" + String.join(".", pathToWrongValue) + "\" is wrong";
    }



}