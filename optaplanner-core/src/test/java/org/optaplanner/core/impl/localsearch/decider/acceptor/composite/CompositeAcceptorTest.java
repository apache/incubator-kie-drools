package org.optaplanner.core.impl.localsearch.decider.acceptor.composite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.CompositeAcceptor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CompositeAcceptorTest {

    private final boolean[] acceptingStates;
    private final boolean result;

    public CompositeAcceptorTest(boolean[] acceptingStates, boolean result) {
        this.acceptingStates = acceptingStates;
        this.result = result;
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection parameters() {
        return Arrays.asList(new Object[][]{
                {new boolean[] {true, true, true}, true},
                {new boolean[] {false, true, true}, false},
                {new boolean[] {true, false, true}, false},
                {new boolean[] {true, true, false}, false},
                {new boolean[] {false, false, false}, false}
        });
    }

    @Test
    public void isAccepted() {
        List<Acceptor> acceptorList = Arrays.asList(
                (Acceptor) new TestingAcceptor(acceptingStates[0]),
                new TestingAcceptor(acceptingStates[1]),
                new TestingAcceptor(acceptingStates[2]));
        CompositeAcceptor acceptor = new CompositeAcceptor(acceptorList);
        assertEquals(result, acceptor.isAccepted(null));
    }

}
