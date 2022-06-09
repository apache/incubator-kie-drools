package org.optaplanner.core.impl.constructionheuristic;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.AbstractPhase;
import org.optaplanner.core.impl.phase.Phase;

/**
 * A {@link ConstructionHeuristicPhase} is a {@link Phase} which uses a construction heuristic algorithm,
 * such as First Fit, First Fit Decreasing, Cheapest Insertion, ...
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Phase
 * @see AbstractPhase
 * @see DefaultConstructionHeuristicPhase
 */
public interface ConstructionHeuristicPhase<Solution_> extends Phase<Solution_> {

}
