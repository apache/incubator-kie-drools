package org.optaplanner.examples.tsp.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.tsp.app.TspApp;
import org.optaplanner.examples.tsp.domain.TspSolution;

class TspOpenDataFilesTest extends OpenDataFilesTest<TspSolution> {

    @Override
    protected CommonApp<TspSolution> createCommonApp() {
        return new TspApp();
    }
}
