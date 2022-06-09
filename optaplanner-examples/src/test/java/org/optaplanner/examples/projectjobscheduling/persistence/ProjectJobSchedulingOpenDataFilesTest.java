package org.optaplanner.examples.projectjobscheduling.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.projectjobscheduling.app.ProjectJobSchedulingApp;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;

class ProjectJobSchedulingOpenDataFilesTest extends OpenDataFilesTest<Schedule> {

    @Override
    protected CommonApp<Schedule> createCommonApp() {
        return new ProjectJobSchedulingApp();
    }
}
