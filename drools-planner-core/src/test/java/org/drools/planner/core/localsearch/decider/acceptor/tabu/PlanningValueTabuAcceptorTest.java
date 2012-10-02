package org.drools.planner.core.localsearch.decider.acceptor.tabu;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.buildin.simple.DefaultSimpleScore;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.testdata.domain.TestdataValue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlanningValueTabuAcceptorTest {

    @Test
    public void tabuSize() {
        PlanningValueTabuAcceptor acceptor = new PlanningValueTabuAcceptor();
        acceptor.setTabuSize(2);
        acceptor.setAspirationEnabled(true);

        TestdataValue v0 = new TestdataValue("v0");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        TestdataValue v4 = new TestdataValue("v4");

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(new DefaultSimpleScore(0));
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(solverScope);
        acceptor.phaseStarted(phaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        stepScope0.setStepIndex(0);
        MoveScope moveScope1 = buildMoveScope(stepScope0, v1);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v0)));
        assertEquals(true, acceptor.isAccepted(moveScope1));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v2))); // repeated call
        stepScope0.setStep(moveScope1.getMove());
        acceptor.stepEnded(stepScope0);

        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        stepScope1.setStepIndex(1);
        MoveScope moveScope2 = buildMoveScope(stepScope1, v2);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v1)));
        assertEquals(true, acceptor.isAccepted(moveScope2));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v2))); // repeated call
        stepScope1.setStep(moveScope2.getMove());
        acceptor.stepEnded(stepScope1);

        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(phaseScope);
        stepScope2.setStepIndex(2);
        MoveScope moveScope4 = buildMoveScope(stepScope2, v4);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, v0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, v3)));
        assertEquals(true, acceptor.isAccepted(moveScope4));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v2))); // repeated call
        stepScope2.setStep(moveScope4.getMove());
        acceptor.stepEnded(stepScope2);

        LocalSearchStepScope stepScope3 = new LocalSearchStepScope(phaseScope);
        stepScope3.setStepIndex(3);
        MoveScope moveScope3 = buildMoveScope(stepScope3, v3);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, v0)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v2)));
        assertEquals(true, acceptor.isAccepted(moveScope3));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v2))); // repeated call
        stepScope3.setStep(moveScope3.getMove());
        acceptor.stepEnded(stepScope3);

        LocalSearchStepScope stepScope4 = new LocalSearchStepScope(phaseScope);
        stepScope4.setStepIndex(4);
        MoveScope moveScope1Again = buildMoveScope(stepScope4, v1);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope4, v0)));
        assertEquals(true, acceptor.isAccepted(moveScope1Again));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope4, v2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope4, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope4, v4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope4, v2))); // repeated call
        stepScope4.setStep(moveScope1Again.getMove());
        acceptor.stepEnded(stepScope4);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    public void tabuSizeMultipleEntitiesPerStep() {
        PlanningValueTabuAcceptor acceptor = new PlanningValueTabuAcceptor();
        acceptor.setTabuSize(2);
        acceptor.setAspirationEnabled(true);

        TestdataValue v0 = new TestdataValue("v0");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        TestdataValue v4 = new TestdataValue("v4");

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(new DefaultSimpleScore(0));
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(solverScope);
        acceptor.phaseStarted(phaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        stepScope0.setStepIndex(0);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v0)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v0, v1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v0, v2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v0, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v0, v4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v1, v2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v1, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v1, v4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v2, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v2, v4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v3, v4)));
        stepScope0.setStep(buildMoveScope(stepScope0, v0, v2).getMove());
        acceptor.stepEnded(stepScope0);

        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        stepScope1.setStepIndex(1);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v0)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v0, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v0, v2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v0, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v0, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v1, v2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v1, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v1, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v2, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v2, v4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v3, v4)));
        stepScope1.setStep(buildMoveScope(stepScope1, v1).getMove());
        acceptor.stepEnded(stepScope1);

        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(phaseScope);
        stepScope2.setStepIndex(2);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v0, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v0, v2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v0, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v0, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v1, v2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v1, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v1, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v2, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v2, v4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, v3, v4)));
        stepScope2.setStep(buildMoveScope(stepScope2, v3, v4).getMove());
        acceptor.stepEnded(stepScope2);

        LocalSearchStepScope stepScope3 = new LocalSearchStepScope(phaseScope);
        stepScope3.setStepIndex(3);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, v0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, v2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v0, v1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, v0, v2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v0, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v0, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v1, v2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v1, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v1, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v2, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v2, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v3, v4)));
        stepScope3.setStep(buildMoveScope(stepScope3, v0).getMove());
        acceptor.stepEnded(stepScope3);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    public void aspiration() {
        PlanningValueTabuAcceptor acceptor = new PlanningValueTabuAcceptor();
        acceptor.setTabuSize(2);
        acceptor.setAspirationEnabled(true);

        TestdataValue v0 = new TestdataValue("v0");
        TestdataValue v1 = new TestdataValue("v1");

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(new DefaultSimpleScore(-100));
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(solverScope);
        acceptor.phaseStarted(phaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        stepScope0.setStepIndex(0);
        stepScope0.setStep(buildMoveScope(stepScope0, v1).getMove());
        acceptor.stepEnded(stepScope0);

        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        stepScope1.setStepIndex(1);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -120, v0)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -20, v0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -120, v1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -20, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -120, v0, v1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -20, v0, v1)));
        stepScope1.setStep(buildMoveScope(stepScope1, -20, v1).getMove());
        acceptor.stepEnded(stepScope1);

        acceptor.phaseEnded(phaseScope);
    }

    private MoveScope buildMoveScope(LocalSearchStepScope stepScope, TestdataValue... values) {
        return buildMoveScope(stepScope, 0, values);
    }

    private MoveScope buildMoveScope(LocalSearchStepScope stepScope, int score, TestdataValue... values) {
        MoveScope moveScope = new MoveScope(stepScope);
        Move move = mock(Move.class);
        when(move.getPlanningValues()).thenReturn((Collection) Arrays.asList(values));
        moveScope.setMove(move);
        moveScope.setScore(new DefaultSimpleScore(score));
        return moveScope;
    }

}
