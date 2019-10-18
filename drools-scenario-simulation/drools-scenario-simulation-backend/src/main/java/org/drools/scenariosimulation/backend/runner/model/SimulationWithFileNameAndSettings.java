/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;

public class SimulationWithFileNameAndSettings {

    private final Simulation simulation;
    private final String fileName;
    private final Settings settings;

    public SimulationWithFileNameAndSettings(Simulation simulation, String fileName, Settings settings) {
        this.simulation = simulation;
        this.fileName = fileName;
        this.settings = settings;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public String getFileName() {
        return fileName;
    }

    public Settings getSettings() {
        return settings;
    }
}
