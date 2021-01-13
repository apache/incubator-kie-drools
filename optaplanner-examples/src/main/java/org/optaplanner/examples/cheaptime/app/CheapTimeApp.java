/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.cheaptime.app;

import java.util.HashSet;
import java.util.Set;

import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.persistence.CheapTimeExporter;
import org.optaplanner.examples.cheaptime.persistence.CheapTimeImporter;
import org.optaplanner.examples.cheaptime.persistence.CheapTimeXmlSolutionFileIO;
import org.optaplanner.examples.cheaptime.swingui.CheapTimePanel;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class CheapTimeApp extends CommonApp<CheapTimeSolution> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/cheaptime/solver/cheapTimeSolverConfig.xml";

    public static final String DATA_DIR_NAME = "cheaptime";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new CheapTimeApp().init();
    }

    public CheapTimeApp() {
        super("Cheap time scheduling",
                "Official competition name: ICON Challenge on Forecasting and Scheduling\n\n" +
                        "Assign tasks to machines and time.\n\n" +
                        "Each machine must have enough hardware to run all of its tasks.\n" +
                        "Each task and machine consumes power. The power price differs over time.\n" +
                        "Minimize the power cost.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                CheapTimePanel.LOGO_PATH);
    }

    @Override
    protected CheapTimePanel createSolutionPanel() {
        return new CheapTimePanel();
    }

    @Override
    public SolutionFileIO<CheapTimeSolution> createSolutionFileIO() {
        return new CheapTimeXmlSolutionFileIO();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[] {
                new CheapTimeImporter()
        };
    }

    @Override
    protected Set<AbstractSolutionExporter> createSolutionExporters() {
        Set<AbstractSolutionExporter> exporters = new HashSet<>(1);
        exporters.add(new CheapTimeExporter());
        return exporters;
    }

}
