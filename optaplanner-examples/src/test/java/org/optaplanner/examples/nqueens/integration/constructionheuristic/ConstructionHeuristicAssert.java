package org.optaplanner.examples.nqueens.integration.constructionheuristic;

import org.optaplanner.examples.nqueens.integration.util.QueenCoordinates;

import java.util.List;

/**
 * To enable assertion of construction heuristic test in single common way.
 */
public interface ConstructionHeuristicAssert {

    List<QueenCoordinates> assertSolution(final int n, final List<QueenCoordinates> results);

}
