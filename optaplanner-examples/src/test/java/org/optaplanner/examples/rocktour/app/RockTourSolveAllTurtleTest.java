package org.optaplanner.examples.rocktour.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;

class RockTourSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<RockTourSolution> {

    @Override
    protected CommonApp<RockTourSolution> createCommonApp() {
        return new RockTourApp();
    }
}
