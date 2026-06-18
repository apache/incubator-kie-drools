/*
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

package org.optaplanner.examples.pas.app;

import java.util.Collections;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleExporter;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleImporter;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleSolutionFileIO;
import org.optaplanner.examples.pas.swingui.PatientAdmissionSchedulePanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class PatientAdmissionScheduleApp extends CommonApp<PatientAdmissionSchedule> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/pas/patientAdmissionScheduleSolverConfig.xml";

    public static final String DATA_DIR_NAME = "pas";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new PatientAdmissionScheduleApp().init();
    }

    public PatientAdmissionScheduleApp() {
        super("Hospital bed planning",
                "Official competition name: PAS - Patient admission scheduling\n\n" +
                        "Assign patients to beds.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                PatientAdmissionSchedulePanel.LOGO_PATH);
    }

    @Override
    protected PatientAdmissionSchedulePanel createSolutionPanel() {
        return new PatientAdmissionSchedulePanel();
    }

    @Override
    public SolutionFileIO<PatientAdmissionSchedule> createSolutionFileIO() {
        return new PatientAdmissionScheduleSolutionFileIO();
    }

    @Override
    protected Set<AbstractSolutionImporter<PatientAdmissionSchedule>> createSolutionImporters() {
        return Collections.singleton(new PatientAdmissionScheduleImporter());
    }

    @Override
    protected Set<AbstractSolutionExporter<PatientAdmissionSchedule>> createSolutionExporters() {
        return Collections.singleton(new PatientAdmissionScheduleExporter());
    }

}
