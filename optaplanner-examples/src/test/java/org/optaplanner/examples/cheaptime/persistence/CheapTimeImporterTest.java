package org.optaplanner.examples.cheaptime.persistence;

import org.optaplanner.examples.cheaptime.app.CheapTimeApp;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;

class CheapTimeImporterTest extends ImportDataFilesTest<CheapTimeSolution> {

    @Override
    protected AbstractSolutionImporter<CheapTimeSolution> createSolutionImporter() {
        return new CheapTimeImporter();
    }

    @Override
    protected String getDataDirName() {
        return CheapTimeApp.DATA_DIR_NAME;
    }
}
