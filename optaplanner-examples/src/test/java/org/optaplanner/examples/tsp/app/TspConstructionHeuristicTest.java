package org.optaplanner.examples.tsp.app;

import java.util.stream.Stream;

import org.optaplanner.examples.common.app.AbstractConstructionHeuristicTest;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.tsp.domain.TspSolution;

class TspConstructionHeuristicTest extends AbstractConstructionHeuristicTest<TspSolution> {

    @Override
    protected CommonApp<TspSolution> createCommonApp() {
        return new TspApp();
    }

    @Override
    protected Stream<String> unsolvedFileNames() {
        return Stream.of("europe40.json");
    }
}
