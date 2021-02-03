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

public class ScenarioSimulationBackendMessages {

    private ScenarioSimulationBackendMessages() {
        // Util class - Not instantiable
    }

    public static String getGenericCollectionErrorMessage() {
        return "Impossible to find elements in the collection to satisfy the conditions.";
    }

    public static String getCollectionHTMLErrorMessage(String wrongValue, List<String> pathToWrongValue) {
        if (wrongValue == null) {
            return getCollectionWithoutWrongValueHTMLErrorMessage(pathToWrongValue);
        }
        return "Value <strong>\"" + wrongValue + "\"</strong>" +
                " is wrong inside:\n" +
                "<em>" + String.join("\n", pathToWrongValue) + "</em>";
    }

    private static String getCollectionWithoutWrongValueHTMLErrorMessage(List<String> pathToWrongValue) {
        if (pathToWrongValue == null || pathToWrongValue.isEmpty()) {
            return getGenericCollectionErrorMessage();
        }
        return "Following path is wrong:\n" +
                "<em>" + String.join("\n", pathToWrongValue) + "</em>";
    }

}