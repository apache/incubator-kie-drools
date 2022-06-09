package org.optaplanner.examples.rocktour.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.rocktour.app.RockTourApp;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;

class RockTourOpenDataFilesTest extends OpenDataFilesTest<RockTourSolution> {

    @Override
    protected CommonApp<RockTourSolution> createCommonApp() {
        return new RockTourApp();
    }
}
