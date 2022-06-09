package org.optaplanner.examples.projectjobscheduling.persistence;

import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;
import org.optaplanner.examples.projectjobscheduling.app.ProjectJobSchedulingApp;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;

class ProjectJobSchedulingImporterTest extends ImportDataFilesTest<Schedule> {

    @Override
    protected AbstractSolutionImporter<Schedule> createSolutionImporter() {
        return new ProjectJobSchedulingImporter();
    }

    @Override
    protected String getDataDirName() {
        return ProjectJobSchedulingApp.DATA_DIR_NAME;
    }
}
