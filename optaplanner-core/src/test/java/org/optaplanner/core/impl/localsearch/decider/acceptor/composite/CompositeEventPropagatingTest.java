package org.optaplanner.core.impl.localsearch.decider.acceptor.composite;

import org.junit.Test;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.CompositeAcceptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CompositeEventPropagatingTest {

    private List<Acceptor> createAcceptors() {
        return Arrays.asList(
                (Acceptor) new TestingAcceptor(false),
                new TestingAcceptor(false),
                new TestingAcceptor(false));
    }

    @Test
    public void phaseStarted() {
        List<Acceptor> acceptors = createAcceptors();
        CompositeAcceptor acceptor = new CompositeAcceptor();
        acceptor.setAcceptorList(acceptors);
        acceptor.phaseStarted(null);
        for (Acceptor acceptor_ : acceptors) {
            assertTrue(((TestingAcceptor) acceptor_).isPhaseStarted());
        }
    }

    @Test
    public void phaseEnded() {
        List<Acceptor> acceptors = createAcceptors();
        CompositeAcceptor acceptor = new CompositeAcceptor();
        acceptor.setAcceptorList(acceptors);
        acceptor.phaseEnded(null);
        for (Acceptor acceptor_ : acceptors) {
            assertTrue(((TestingAcceptor) acceptor_).isPhaseEnded());
        }
    }

    @Test
    public void stepStarted() {
        List<Acceptor> acceptors = createAcceptors();
        CompositeAcceptor acceptor = new CompositeAcceptor();
        acceptor.setAcceptorList(acceptors);
        acceptor.stepStarted(null);
        for (Acceptor acceptor_ : acceptors) {
            assertTrue(((TestingAcceptor) acceptor_).isStepStarted());
        }
    }

    @Test
    public void stepEnded() {
        List<Acceptor> acceptors = createAcceptors();
        CompositeAcceptor acceptor = new CompositeAcceptor();
        acceptor.setAcceptorList(acceptors);
        acceptor.stepEnded(null);
        for (Acceptor acceptor_ : acceptors) {
            assertTrue(((TestingAcceptor) acceptor_).isStepEnded());
        }
    }

    @Test
    public void solvingStarted() {
        List<Acceptor> acceptors = createAcceptors();
        CompositeAcceptor acceptor = new CompositeAcceptor();
        acceptor.setAcceptorList(acceptors);
        acceptor.solvingStarted(null);
        for (Acceptor acceptor_ : acceptors) {
            assertTrue(((TestingAcceptor) acceptor_).isSolvingStarted());
        }
    }

    @Test
    public void solvingEnded() {
        List<Acceptor> acceptors = createAcceptors();
        CompositeAcceptor acceptor = new CompositeAcceptor();
        acceptor.setAcceptorList(acceptors);
        acceptor.solvingEnded(null);
        for (Acceptor acceptor_ : acceptors) {
            assertTrue(((TestingAcceptor) acceptor_).isSolvingEnded());
        }
    }
}
