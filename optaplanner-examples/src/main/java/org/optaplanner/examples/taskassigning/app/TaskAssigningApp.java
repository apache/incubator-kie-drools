/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.taskassigning.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.persistence.TaskAssigningXmlSolutionFileIO;
import org.optaplanner.examples.taskassigning.swingui.TaskAssigningPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class TaskAssigningApp extends CommonApp<TaskAssigningSolution> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/taskassigning/solver/taskAssigningSolverConfig.xml";

    public static final String DATA_DIR_NAME = "taskassigning";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new TaskAssigningApp().init();
    }

    public TaskAssigningApp() {
        super("Task assigning",
                "Assign tasks to employees in a sequence.\n\n"
                        + "Match skills and affinity.\n"
                        + "Prioritize critical tasks.\n"
                        + "Minimize the makespan.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                TaskAssigningPanel.LOGO_PATH);
    }

    @Override
    protected TaskAssigningPanel createSolutionPanel() {
        return new TaskAssigningPanel();
    }

    @Override
    public SolutionFileIO<TaskAssigningSolution> createSolutionFileIO() {
        return new TaskAssigningXmlSolutionFileIO();
    }

}
