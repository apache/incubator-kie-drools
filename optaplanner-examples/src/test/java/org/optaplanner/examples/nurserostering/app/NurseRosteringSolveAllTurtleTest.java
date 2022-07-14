package org.optaplanner.examples.nurserostering.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

class NurseRosteringSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<NurseRoster> {

    @Override
    protected CommonApp<NurseRoster> createCommonApp() {
        return new NurseRosteringApp();
    }

}
