package org.optaplanner.examples.examination.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.examination.app.ExaminationApp;
import org.optaplanner.examples.examination.domain.Examination;

class ExaminationOpenDataFilesTest extends OpenDataFilesTest<Examination> {

    @Override
    protected CommonApp<Examination> createCommonApp() {
        return new ExaminationApp();
    }
}
