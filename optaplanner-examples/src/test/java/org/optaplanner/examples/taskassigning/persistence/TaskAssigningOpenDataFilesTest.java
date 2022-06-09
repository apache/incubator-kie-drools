package org.optaplanner.examples.taskassigning.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.taskassigning.app.TaskAssigningApp;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;

class TaskAssigningOpenDataFilesTest extends OpenDataFilesTest<TaskAssigningSolution> {

    @Override
    protected CommonApp<TaskAssigningSolution> createCommonApp() {
        return new TaskAssigningApp();
    }
}
