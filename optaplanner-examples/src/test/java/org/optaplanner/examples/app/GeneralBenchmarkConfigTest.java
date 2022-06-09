package org.optaplanner.examples.app;

import org.optaplanner.examples.common.app.AbstractBenchmarkConfigTest;
import org.optaplanner.examples.common.app.CommonBenchmarkApp;

class GeneralBenchmarkConfigTest extends AbstractBenchmarkConfigTest {

    @Override
    protected CommonBenchmarkApp getBenchmarkApp() {
        return new GeneralOptaPlannerBenchmarkApp();
    }
}
