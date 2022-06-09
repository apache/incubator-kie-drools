package org.optaplanner.examples.examination.persistence;

import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;
import org.optaplanner.examples.examination.app.ExaminationApp;
import org.optaplanner.examples.examination.domain.Examination;

class ExaminationImporterTest extends ImportDataFilesTest<Examination> {

    @Override
    protected AbstractSolutionImporter<Examination> createSolutionImporter() {
        return new ExaminationImporter();
    }

    @Override
    protected String getDataDirName() {
        return ExaminationApp.DATA_DIR_NAME;
    }
}
