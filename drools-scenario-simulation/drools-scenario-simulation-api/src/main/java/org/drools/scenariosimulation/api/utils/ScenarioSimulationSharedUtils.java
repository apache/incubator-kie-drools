/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.scenariosimulation.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;

public class ScenarioSimulationSharedUtils {

    public static final String FILE_EXTENSION = "scesim";

    /**
     * Returns true if given string equals <code>List.class.getName()</code> or <code>Map.class.getName()</code>
     * @param className
     * @return
     */
    public static boolean isCollection(String className) {
        return isList(className) || isMap(className);
    }

    /**
     * Returns true if given string equals <code>List.class.getName()</code>
     * @param className
     * @return
     */
    public static boolean isList(String className) {
        return List.class.getName().equals(className);
    }

    /**
     * Returns true if given string equals <code>List.class.getName()</code>
     * @param className
     * @return
     */
    public static boolean isMap(String className) {
        return Map.class.getName().equals(className);
    }

    public static List<ScenarioWithIndex> toScenarioWithIndex(Simulation simulation) {
        List<ScenarioWithIndex> toReturn = new ArrayList<>();
        List<Scenario> scenarios = simulation.getUnmodifiableScenarios();
        for (int index = 0; index < scenarios.size(); index += 1) {
            toReturn.add(new ScenarioWithIndex(index + 1, scenarios.get(index)));
        }
        return toReturn;
    }
}
