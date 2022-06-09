package org.optaplanner.examples.nurserostering.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.nurserostering.app.NurseRosteringApp;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

class NurseRosteringOpenDataFilesTest extends OpenDataFilesTest<NurseRoster> {

    @Override
    protected CommonApp<NurseRoster> createCommonApp() {
        return new NurseRosteringApp();
    }
}
