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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.scenariosimulation.api.model.ScenarioWithIndex;

public class ScenarioResultMetadata {

    protected final Set<String> available = new HashSet<>();

    protected final Set<String> executed = new HashSet<>();

    protected final ScenarioWithIndex scenarioWithIndex;

    public ScenarioResultMetadata(ScenarioWithIndex scenarioWithIndex) {
        this.scenarioWithIndex = scenarioWithIndex;
    }

    public void addAvailable(String element) {
        available.add(element);
    }

    public void addExecuted(String element) {
        executed.add(element);
    }

    public Set<String> getAvailable() {
        return Collections.unmodifiableSet(available);
    }

    public Set<String> getExecuted() {
        return Collections.unmodifiableSet(executed);
    }

    public ScenarioWithIndex getScenarioWithIndex() {
        return scenarioWithIndex;
    }
}
