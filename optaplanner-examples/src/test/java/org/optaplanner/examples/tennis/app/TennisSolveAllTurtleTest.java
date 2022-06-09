package org.optaplanner.examples.tennis.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.tennis.domain.TennisSolution;

class TennisSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<TennisSolution> {

    @Override
    protected CommonApp<TennisSolution> createCommonApp() {
        return new TennisApp();
    }
}
