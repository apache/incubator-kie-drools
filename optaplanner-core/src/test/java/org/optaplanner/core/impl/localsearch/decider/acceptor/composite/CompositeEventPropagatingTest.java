package org.optaplanner.core.impl.localsearch.decider.acceptor.composite;

import org.junit.Test;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.CompositeAcceptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

public class CompositeEventPropagatingTest {

    @Test
    public void phaseLifecycle() {
        Acceptor acceptor1 = mock(Acceptor.class);
        Acceptor acceptor2 = mock(Acceptor.class);
        Acceptor acceptor3 = mock(Acceptor.class);
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor(acceptor1, acceptor2, acceptor3);

        compositeAcceptor.solvingStarted(null);
        compositeAcceptor.phaseStarted(null);
        compositeAcceptor.stepStarted(null);
        compositeAcceptor.stepEnded(null);
        compositeAcceptor.stepStarted(null);
        compositeAcceptor.stepEnded(null);
        compositeAcceptor.phaseEnded(null);
        compositeAcceptor.phaseStarted(null);
        compositeAcceptor.stepStarted(null);
        compositeAcceptor.stepEnded(null);
        compositeAcceptor.phaseEnded(null);
        compositeAcceptor.solvingEnded(null);

        verifyPhaseLifecycle(acceptor1, 1, 2, 3);
        verifyPhaseLifecycle(acceptor2, 1, 2, 3);
        verifyPhaseLifecycle(acceptor3, 1, 2, 3);
    }

}
