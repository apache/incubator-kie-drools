package org.optaplanner.examples.examination.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.examination.domain.Examination;

class ExaminationSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<Examination> {

    @Override
    protected CommonApp<Examination> createCommonApp() {
        return new ExaminationApp();
    }
}
