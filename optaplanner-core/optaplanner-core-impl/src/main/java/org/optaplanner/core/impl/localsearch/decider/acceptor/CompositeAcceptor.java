package org.optaplanner.core.impl.localsearch.decider.acceptor;

import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Combines several acceptors into one.
 * Does a logical AND over the accepted status of its acceptors.
 * For example: combine planning entity and planning value tabu to do tabu on both.
 */
public class CompositeAcceptor<Solution_> extends AbstractAcceptor<Solution_> {

    protected final List<Acceptor<Solution_>> acceptorList;

    public CompositeAcceptor(List<Acceptor<Solution_>> acceptorList) {
        this.acceptorList = acceptorList;
    }

    public CompositeAcceptor(Acceptor<Solution_>... acceptors) {
        this(Arrays.asList(acceptors));
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        for (Acceptor<Solution_> acceptor : acceptorList) {
            acceptor.solvingStarted(solverScope);
        }
    }

    @Override
    public void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope) {
        for (Acceptor<Solution_> acceptor : acceptorList) {
            acceptor.phaseStarted(phaseScope);
        }
    }

    @Override
    public void stepStarted(LocalSearchStepScope<Solution_> stepScope) {
        for (Acceptor<Solution_> acceptor : acceptorList) {
            acceptor.stepStarted(stepScope);
        }
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope<Solution_> moveScope) {
        for (Acceptor<Solution_> acceptor : acceptorList) {
            boolean accepted = acceptor.isAccepted(moveScope);
            if (!accepted) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void stepEnded(LocalSearchStepScope<Solution_> stepScope) {
        for (Acceptor<Solution_> acceptor : acceptorList) {
            acceptor.stepEnded(stepScope);
        }
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        for (Acceptor<Solution_> acceptor : acceptorList) {
            acceptor.phaseEnded(phaseScope);
        }
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        for (Acceptor<Solution_> acceptor : acceptorList) {
            acceptor.solvingEnded(solverScope);
        }
    }

}
