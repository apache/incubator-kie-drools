package org.optaplanner.core.impl.partitionedsearch;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.AbstractPhase;
import org.optaplanner.core.impl.phase.Phase;

/**
 * A {@link PartitionedSearchPhase} is a {@link Phase} which uses a Partition Search algorithm.
 * It splits the {@link PlanningSolution} into pieces and solves those separately with other {@link Phase}s.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Phase
 * @see AbstractPhase
 * @see DefaultPartitionedSearchPhase
 */
public interface PartitionedSearchPhase<Solution_> extends Phase<Solution_> {

}
