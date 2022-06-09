package org.optaplanner.examples.nurserostering.persistence;

import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;
import org.optaplanner.examples.nurserostering.app.NurseRosteringApp;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

class NurseRosteringImporterTest extends ImportDataFilesTest<NurseRoster> {

    @Override
    protected AbstractSolutionImporter<NurseRoster> createSolutionImporter() {
        return new NurseRosteringImporter();
    }

    @Override
    protected String getDataDirName() {
        return NurseRosteringApp.DATA_DIR_NAME;
    }
}
