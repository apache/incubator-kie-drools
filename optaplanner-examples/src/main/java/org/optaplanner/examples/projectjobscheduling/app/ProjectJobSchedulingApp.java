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

package org.optaplanner.examples.projectjobscheduling.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;
import org.optaplanner.examples.projectjobscheduling.persistence.ProjectJobSchedulingDao;
import org.optaplanner.examples.projectjobscheduling.persistence.ProjectJobSchedulingImporter;
import org.optaplanner.examples.projectjobscheduling.swingui.ProjectJobSchedulingPanel;

public class ProjectJobSchedulingApp extends CommonApp<Schedule> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/projectjobscheduling/solver/projectJobSchedulingSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new ProjectJobSchedulingApp().init();
    }

    public ProjectJobSchedulingApp() {
        super("Project job scheduling",
                "Official competition name:" +
                        " multi-mode resource-constrained multi-project scheduling problem (MRCMPSP)\n\n" +
                        "Schedule all jobs in time and execution mode.\n\n" +
                        "Minimize project delays.",
                SOLVER_CONFIG,
                ProjectJobSchedulingPanel.LOGO_PATH);
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new ProjectJobSchedulingPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new ProjectJobSchedulingDao();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new ProjectJobSchedulingImporter()
        };
    }

}
