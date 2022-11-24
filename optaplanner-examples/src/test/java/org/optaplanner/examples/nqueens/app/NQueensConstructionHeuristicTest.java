package org.optaplanner.examples.nqueens.app;

import java.util.stream.Stream;

import org.optaplanner.examples.common.app.AbstractConstructionHeuristicTest;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.nqueens.domain.NQueens;

class NQueensConstructionHeuristicTest extends AbstractConstructionHeuristicTest<NQueens> {

    @Override
    protected CommonApp<NQueens> createCommonApp() {
        return new NQueensApp();
    }

    @Override
    protected Stream<String> unsolvedFileNames() {
        return Stream.of("4queens.json", "8queens.json");
    }
}
