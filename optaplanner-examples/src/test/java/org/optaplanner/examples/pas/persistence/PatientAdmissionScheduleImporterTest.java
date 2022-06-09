package org.optaplanner.examples.pas.persistence;

import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;
import org.optaplanner.examples.pas.app.PatientAdmissionScheduleApp;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

class PatientAdmissionScheduleImporterTest extends ImportDataFilesTest<PatientAdmissionSchedule> {

    @Override
    protected AbstractSolutionImporter<PatientAdmissionSchedule> createSolutionImporter() {
        return new PatientAdmissionScheduleImporter();
    }

    @Override
    protected String getDataDirName() {
        return PatientAdmissionScheduleApp.DATA_DIR_NAME;
    }
}
