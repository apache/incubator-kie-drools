package org.optaplanner.examples.tennis.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.tennis.app.TennisApp;
import org.optaplanner.examples.tennis.domain.TennisSolution;

class TennisOpenDataFilesTest extends OpenDataFilesTest<TennisSolution> {

    @Override
    protected CommonApp<TennisSolution> createCommonApp() {
        return new TennisApp();
    }
}
