/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nurserostering.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.persistence.NurseRosteringExporter;
import org.optaplanner.examples.nurserostering.persistence.NurseRosteringImporter;
import org.optaplanner.examples.nurserostering.swingui.NurseRosteringPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class NurseRosteringApp extends CommonApp<NurseRoster> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/nurserostering/solver/nurseRosteringSolverConfig.xml";

    public static final String DATA_DIR_NAME = "nurserostering";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new NurseRosteringApp().init();
    }

    public NurseRosteringApp() {
        super("Nurse rostering",
                "Official competition name: INRC2010 - Nurse rostering\n\n" +
                        "Assign shifts to nurses.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                NurseRosteringPanel.LOGO_PATH);
    }

    @Override
    protected NurseRosteringPanel createSolutionPanel() {
        return new NurseRosteringPanel();
    }

    @Override
    public SolutionFileIO<NurseRoster> createSolutionFileIO() {
        return new XStreamSolutionFileIO<>(NurseRoster.class);
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new NurseRosteringImporter()
        };
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new NurseRosteringExporter();
    }

}
