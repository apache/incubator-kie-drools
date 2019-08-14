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

package org.drools.scenariosimulation.backend.runner.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.scenariosimulation.api.model.ScenarioWithIndex;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

public class ScenarioResultMetadata {

    protected final Set<String> available = new HashSet<>();

    protected final Map<String, Integer> executed = new HashMap<>();

    protected final ScenarioWithIndex scenarioWithIndex;

    public ScenarioResultMetadata(ScenarioWithIndex scenarioWithIndex) {
        this.scenarioWithIndex = scenarioWithIndex;
    }

    public void addAvailable(String element) {
        available.add(element);
    }

    public void addAllAvailable(Set<String> elements) {
        available.addAll(elements);
    }

    public void addExecuted(String element) {
        executed.compute(element, (key, value) -> value == null ? 1 : value + 1);
    }

    public void addAllExecuted(Map<String, Integer> elements) {
        executed.putAll(elements);
    }

    public Set<String> getAvailable() {
        return unmodifiableSet(available);
    }

    public Set<String> getExecuted() {
        return unmodifiableSet(executed.keySet());
    }

    public Map<String, Integer> getExecutedWithCounter() {
        return unmodifiableMap(executed);
    }

    public ScenarioWithIndex getScenarioWithIndex() {
        return scenarioWithIndex;
    }
}
