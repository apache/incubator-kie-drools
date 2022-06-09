package org.optaplanner.examples.pas.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

class PatientAdmissionScheduleSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<PatientAdmissionSchedule> {

    @Override
    protected CommonApp<PatientAdmissionSchedule> createCommonApp() {
        return new PatientAdmissionScheduleApp();
    }
}
