package org.optaplanner.examples.cheaptime.persistence;

import org.optaplanner.examples.cheaptime.app.CheapTimeApp;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;

class CheapTimeOpenDataFilesTest extends OpenDataFilesTest<CheapTimeSolution> {

    @Override
    protected CommonApp<CheapTimeSolution> createCommonApp() {
        return new CheapTimeApp();
    }
}
