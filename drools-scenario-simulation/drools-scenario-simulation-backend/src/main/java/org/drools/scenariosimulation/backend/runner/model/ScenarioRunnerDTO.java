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

import java.util.List;

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;

public class ScenarioRunnerDTO {

    private final String fileName;
    private final Settings settings;
    private final Background background;
    private final List<ScenarioWithIndex> scenarioWithIndices;
    private final ScesimModelDescriptor simulationModelDescriptor;

    public ScenarioRunnerDTO(ScenarioSimulationModel model, String fileName) {
        this(model.getSimulation().getScesimModelDescriptor(), model.getSimulation().getScenarioWithIndex(), fileName, model.getSettings(), model.getBackground());
    }

    public ScenarioRunnerDTO(ScesimModelDescriptor simulationModelDescriptor, List<ScenarioWithIndex> scenarioWithIndices, String fileName, Settings settings, Background background) {
        this.simulationModelDescriptor = simulationModelDescriptor;
        this.scenarioWithIndices = scenarioWithIndices;
        this.fileName = fileName;
        this.settings = settings;
        this.background = background;
    }

    public String getFileName() {
        return fileName;
    }

    public Settings getSettings() {
        return settings;
    }

    public Background getBackground() {
        return background;
    }

    public List<ScenarioWithIndex> getScenarioWithIndices() {
        return scenarioWithIndices;
    }

    public ScesimModelDescriptor getSimulationModelDescriptor() {
        return simulationModelDescriptor;
    }
}
