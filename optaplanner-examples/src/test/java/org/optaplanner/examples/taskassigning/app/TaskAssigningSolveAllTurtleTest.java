package org.optaplanner.examples.taskassigning.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;

class TaskAssigningSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<TaskAssigningSolution> {

    @Override
    protected CommonApp<TaskAssigningSolution> createCommonApp() {
        return new TaskAssigningApp();
    }
}
