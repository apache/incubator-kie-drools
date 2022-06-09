package org.optaplanner.examples.pas.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.pas.app.PatientAdmissionScheduleApp;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

class PatientAdmissionScheduleOpenDataFilesTest extends OpenDataFilesTest<PatientAdmissionSchedule> {

    @Override
    protected CommonApp<PatientAdmissionSchedule> createCommonApp() {
        return new PatientAdmissionScheduleApp();
    }
}
