package org.optaplanner.core.impl.exhaustivesearch;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.AbstractPhase;
import org.optaplanner.core.impl.phase.Phase;

/**
 * A {@link ExhaustiveSearchPhase} is a {@link Phase} which uses an exhaustive algorithm, such as Brute Force.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Phase
 * @see AbstractPhase
 * @see DefaultExhaustiveSearchPhase
 */
public interface ExhaustiveSearchPhase<Solution_> extends Phase<Solution_> {

}
