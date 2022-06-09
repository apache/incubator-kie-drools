package org.optaplanner.core.impl.phase.custom;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.AbstractPhase;
import org.optaplanner.core.impl.phase.Phase;

/**
 * A {@link CustomPhase} is a {@link Phase} which uses {@link CustomPhaseCommand}s.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Phase
 * @see AbstractPhase
 * @see DefaultCustomPhase
 */
public interface CustomPhase<Solution_> extends Phase<Solution_> {

}
