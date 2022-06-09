package org.optaplanner.examples.tsp.persistence;

import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;
import org.optaplanner.examples.tsp.app.TspApp;
import org.optaplanner.examples.tsp.domain.TspSolution;

class TspImporterTest extends ImportDataFilesTest<TspSolution> {

    @Override
    protected AbstractSolutionImporter<TspSolution> createSolutionImporter() {
        return new TspImporter();
    }

    @Override
    protected String getDataDirName() {
        return TspApp.DATA_DIR_NAME;
    }
}
