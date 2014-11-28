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
        List<Acceptor> acceptorList = createAcceptors();
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor();
        compositeAcceptor.setAcceptorList(acceptorList);
        compositeAcceptor.phaseStarted(null);
        for (Acceptor acceptor : acceptorList) {
            assertTrue(((TestingAcceptor) acceptor).isPhaseStarted());
        }
    }

    @Test
    public void phaseEnded() {
        List<Acceptor> acceptorList = createAcceptors();
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor();
        compositeAcceptor.setAcceptorList(acceptorList);
        compositeAcceptor.phaseEnded(null);
        for (Acceptor acceptor : acceptorList) {
            assertTrue(((TestingAcceptor) acceptor).isPhaseEnded());
        }
    }

    @Test
    public void stepStarted() {
        List<Acceptor> acceptorList = createAcceptors();
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor();
        compositeAcceptor.setAcceptorList(acceptorList);
        compositeAcceptor.stepStarted(null);
        for (Acceptor acceptor : acceptorList) {
            assertTrue(((TestingAcceptor) acceptor).isStepStarted());
        }
    }

    @Test
    public void stepEnded() {
        List<Acceptor> acceptorList = createAcceptors();
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor();
        compositeAcceptor.setAcceptorList(acceptorList);
        compositeAcceptor.stepEnded(null);
        for (Acceptor acceptor : acceptorList) {
            assertTrue(((TestingAcceptor) acceptor).isStepEnded());
        }
    }

    @Test
    public void solvingStarted() {
        List<Acceptor> acceptorList = createAcceptors();
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor();
        compositeAcceptor.setAcceptorList(acceptorList);
        compositeAcceptor.solvingStarted(null);
        for (Acceptor acceptor : acceptorList) {
            assertTrue(((TestingAcceptor) acceptor).isSolvingStarted());
        }
    }

    @Test
    public void solvingEnded() {
        List<Acceptor> acceptorList = createAcceptors();
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor();
        compositeAcceptor.setAcceptorList(acceptorList);
        compositeAcceptor.solvingEnded(null);
        for (Acceptor acceptor : acceptorList) {
            assertTrue(((TestingAcceptor) acceptor).isSolvingEnded());
        }
    }

}
