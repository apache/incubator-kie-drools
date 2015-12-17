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

package org.optaplanner.examples.pas.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleDao;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleExporter;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleImporter;
import org.optaplanner.examples.pas.swingui.PatientAdmissionSchedulePanel;

public class PatientAdmissionScheduleApp extends CommonApp<PatientAdmissionSchedule> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/pas/solver/patientAdmissionScheduleSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new PatientAdmissionScheduleApp().init();
    }

    public PatientAdmissionScheduleApp() {
        super("Hospital bed planning",
                "Official competition name: PAS - Patient admission scheduling\n\n" +
                        "Assign patients to beds.",
                SOLVER_CONFIG,
                PatientAdmissionSchedulePanel.LOGO_PATH);
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new PatientAdmissionSchedulePanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new PatientAdmissionScheduleDao();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new PatientAdmissionScheduleImporter()
        };
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new PatientAdmissionScheduleExporter();
    }

}
