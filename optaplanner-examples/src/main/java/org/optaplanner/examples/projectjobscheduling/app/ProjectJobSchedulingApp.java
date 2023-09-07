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

package org.optaplanner.examples.projectjobscheduling.app;

import java.util.Collections;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;
import org.optaplanner.examples.projectjobscheduling.persistence.ProjectJobSchedulingImporter;
import org.optaplanner.examples.projectjobscheduling.persistence.ProjectJobSchedulingSolutionFileIO;
import org.optaplanner.examples.projectjobscheduling.swingui.ProjectJobSchedulingPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class ProjectJobSchedulingApp extends CommonApp<Schedule> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/projectjobscheduling/projectJobSchedulingSolverConfig.xml";

    public static final String DATA_DIR_NAME = "projectjobscheduling";

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
                SOLVER_CONFIG, DATA_DIR_NAME,
                ProjectJobSchedulingPanel.LOGO_PATH);
    }

    @Override
    protected ProjectJobSchedulingPanel createSolutionPanel() {
        return new ProjectJobSchedulingPanel();
    }

    @Override
    public SolutionFileIO<Schedule> createSolutionFileIO() {
        return new ProjectJobSchedulingSolutionFileIO();
    }

    @Override
    protected Set<AbstractSolutionImporter<Schedule>> createSolutionImporters() {
        return Collections.singleton(new ProjectJobSchedulingImporter());
    }

}
