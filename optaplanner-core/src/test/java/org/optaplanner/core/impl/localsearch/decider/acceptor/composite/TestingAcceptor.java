package org.optaplanner.core.impl.localsearch.decider.acceptor.composite;

import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class TestingAcceptor extends AbstractAcceptor {

    private boolean accepting;
    private boolean phaseStarted;
    private boolean phaseEnded;
    private boolean stepStarted;
    private boolean stepEnded;
    private boolean solvingStarted;
    private boolean solvingEnded;

    public TestingAcceptor(boolean accepting) {
        this.accepting = accepting;
    }

    public boolean isPhaseStarted() {
        return phaseStarted;
    }

    public boolean isPhaseEnded() {
        return phaseEnded;
    }

    public boolean isStepStarted() {
        return stepStarted;
    }

    public boolean isStepEnded() {
        return stepEnded;
    }

    public boolean isSolvingStarted() {
        return solvingStarted;
    }

    public boolean isSolvingEnded() {
        return solvingEnded;
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        return accepting;
    }

    @Override
    public void phaseStarted(LocalSearchPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        this.phaseStarted = true;
    }

    @Override
    public void stepStarted(LocalSearchStepScope stepScope) {
        super.stepStarted(stepScope);
        this.stepStarted = true;
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        this.stepEnded = true;
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        this.phaseEnded = true;
    }

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        this.solvingStarted = true;
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingEnded(solverScope);
        this.solvingEnded = true;
    }

}
