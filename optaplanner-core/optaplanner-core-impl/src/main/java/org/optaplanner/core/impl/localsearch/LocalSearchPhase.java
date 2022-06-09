package org.optaplanner.core.impl.localsearch;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.localsearch.decider.acceptor.lateacceptance.LateAcceptanceAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.AbstractTabuAcceptor;
import org.optaplanner.core.impl.phase.AbstractPhase;
import org.optaplanner.core.impl.phase.Phase;

/**
 * A {@link LocalSearchPhase} is a {@link Phase} which uses a Local Search algorithm,
 * such as {@link AbstractTabuAcceptor Tabu Search}, {@link SimulatedAnnealingAcceptor Simulated Annealing},
 * {@link LateAcceptanceAcceptor Late Acceptance}, ...
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Phase
 * @see AbstractPhase
 * @see DefaultLocalSearchPhase
 */
public interface LocalSearchPhase<Solution_> extends Phase<Solution_> {

}
