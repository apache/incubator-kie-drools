package org.optaplanner.core.impl.localsearch.decider.acceptor.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.CompositeAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class CompositeAcceptorTest {

    @Test
    void phaseLifecycle() {
        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = mock(LocalSearchPhaseScope.class);
        LocalSearchStepScope<TestdataSolution> stepScope = mock(LocalSearchStepScope.class);

        Acceptor acceptor1 = mock(Acceptor.class);
        Acceptor acceptor2 = mock(Acceptor.class);
        Acceptor acceptor3 = mock(Acceptor.class);
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor(acceptor1, acceptor2, acceptor3);

        compositeAcceptor.solvingStarted(solverScope);
        compositeAcceptor.phaseStarted(phaseScope);
        compositeAcceptor.stepStarted(stepScope);
        compositeAcceptor.stepEnded(stepScope);
        compositeAcceptor.stepStarted(stepScope);
        compositeAcceptor.stepEnded(stepScope);
        compositeAcceptor.phaseEnded(phaseScope);
        compositeAcceptor.phaseStarted(phaseScope);
        compositeAcceptor.stepStarted(stepScope);
        compositeAcceptor.stepEnded(stepScope);
        compositeAcceptor.phaseEnded(phaseScope);
        compositeAcceptor.solvingEnded(solverScope);

        verifyPhaseLifecycle(acceptor1, 1, 2, 3);
        verifyPhaseLifecycle(acceptor2, 1, 2, 3);
        verifyPhaseLifecycle(acceptor3, 1, 2, 3);
    }

    @Test
    void isAccepted() {
        assertThat(isCompositeAccepted(true, true, true)).isTrue();
        assertThat(isCompositeAccepted(false, true, true)).isFalse();
        assertThat(isCompositeAccepted(true, false, true)).isFalse();
        assertThat(isCompositeAccepted(true, true, false)).isFalse();
        assertThat(isCompositeAccepted(false, false, false)).isFalse();
    }

    private boolean isCompositeAccepted(boolean... childAccepts) {
        List<Acceptor> acceptorList = new ArrayList<>(childAccepts.length);
        for (boolean childAccept : childAccepts) {
            Acceptor acceptor = mock(Acceptor.class);
            when(acceptor.isAccepted(any(LocalSearchMoveScope.class))).thenReturn(childAccept);
            acceptorList.add(acceptor);
        }
        CompositeAcceptor acceptor = new CompositeAcceptor(acceptorList);
        return acceptor.isAccepted(mock(LocalSearchMoveScope.class));
    }
}
