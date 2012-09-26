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
    public void tabuSize(){
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
        LocalSearchSolverPhaseScope solverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        acceptor.phaseStarted(solverPhaseScope);

        //FIRST STEP: no values in tabuList, acceptor should accept everything.
        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(solverPhaseScope);
        stepScope0.setStepIndex(0);
        MoveScope moveScope1 = buildMoveScope(stepScope0, v1);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v0)));
        assertEquals(true, acceptor.isAccepted(moveScope1));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v4)));
        //Repeated calls containing values which are not in tabuList should get accepted too.
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, v2)));
        //We accept first move, v1 should be added to tabuList
        stepScope0.setStep(moveScope1.getMove());
        acceptor.stepEnded(stepScope0);


        //SECOND STEP: v1 in tabuList, acceptor should accept every move
        //except a move containing v1
        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(solverPhaseScope);
        stepScope1.setStepIndex(1);
        MoveScope moveScope2 = buildMoveScope(stepScope1, v2);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v0)));
        //moves containing values which are in tabuList should not get accepted
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v1)));
        //Any amount of repeated calls containing moves with values which are in tabuList 
        //should get rejected too
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, v1)));
        assertEquals(true, acceptor.isAccepted(moveScope2));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v4)));
        //Repeated calls for same move containing values which are not in tabuList should get accepted too.
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, v2)));
        //We accept move with v2, tabuList should contain v1 and v2
        stepScope1.setStep(moveScope2.getMove());
        acceptor.stepEnded(stepScope1);

        //3rd STEP: v1 & v2 in tabuList, acceptor should accept every move
        //except a move containing v1 and v2
        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(solverPhaseScope);
        stepScope2.setStepIndex(2);
        MoveScope moveScope4 = buildMoveScope(stepScope2, v4);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, v0)));
        //moves containing values which are in tabuList should not get accepted
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v1)));
        //moves containing values which are in tabuList should not get accepted
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, v3)));
        assertEquals(true, acceptor.isAccepted(moveScope4));
        //Repeated calls for same move containing values which are in tabuList should not get accepted either
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, v2)));
        //We accept move with v4, tabuList should contain v2 and v4
        //v1 should be removed from tabuList
        stepScope2.setStep(moveScope4.getMove());
        acceptor.stepEnded(stepScope2);

        //4th STEP: v2 and v4 in tabuList, acceptor should accept every move
        //except a move containing v1 and v2
        LocalSearchStepScope stepScope3 = new LocalSearchStepScope(solverPhaseScope);
        stepScope3.setStepIndex(3);
        MoveScope moveScope3 = buildMoveScope(stepScope3, v3);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, v0)));
        //v1 not in tabuList anymore, move should get accepted
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v2)));
        assertEquals(true, acceptor.isAccepted(moveScope3));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, v2)));
        //We accept move with v3, tabuList should contain v3 and v4
        //v2 should be removed from tabuList
        stepScope3.setStep(moveScope3.getMove());
        acceptor.stepEnded(stepScope3);

        //5th STEP: v4 and v3 in tabuList, acceptor should accept every move
        //except a move containing v1 and v2
        LocalSearchStepScope stepScope4 = new LocalSearchStepScope(solverPhaseScope);
        stepScope4.setStepIndex(4);
        MoveScope moveScope1Again = buildMoveScope(stepScope4, v1);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope4, v0)));
        assertEquals(true, acceptor.isAccepted(moveScope1Again));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope4, v2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope4, v3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope4, v4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope4, v2)));
        //Value one gets accepted again
        stepScope4.setStep(moveScope1Again.getMove());
        acceptor.stepEnded(stepScope4);

        acceptor.phaseEnded(solverPhaseScope);
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
        LocalSearchSolverPhaseScope solverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        acceptor.phaseStarted(solverPhaseScope);

        //FIRST STEP: no values in tabuList, acceptor should accept everything.
        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(solverPhaseScope);
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

        //SECOND STEP: v0 and v2 in tabuList, acceptor should accept every move
        //except a move containing either v0 or v2 or both
        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(solverPhaseScope);
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

        //THIRD STEP: v0, v2, v1 in tabuList, acceptor should accept every move
        //except a move containing either v0, v2, v1 or a combination of those
        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(solverPhaseScope);
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

        //4TH STEP: v3, v4, v1 in tabuList, acceptor should accept every move
        //except a move containing either v3, v4, v1 or a combination of those
        //v0 and v2 should've been released from the tabuList
        LocalSearchStepScope stepScope3 = new LocalSearchStepScope(solverPhaseScope);
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

        acceptor.phaseEnded(solverPhaseScope);
    }

    @Test
    public void aspiration() {
        PlanningValueTabuAcceptor acceptor = new PlanningValueTabuAcceptor();
        acceptor.setTabuSize(2);
        //We want to accepted moves containing values from tabuList
        //if moves are better than best solution found (not working solution)
        acceptor.setAspirationEnabled(true);

        TestdataValue v0 = new TestdataValue("v0");
        TestdataValue v1 = new TestdataValue("v1");

        DefaultSolverScope solverScope = new DefaultSolverScope();
        //We set best score to -100
        solverScope.setBestScore(new DefaultSimpleScore(-100));
        LocalSearchSolverPhaseScope solverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        acceptor.phaseStarted(solverPhaseScope);

        //First step: we accept a move containing v1, so v1 should be added to
        //TabuList
        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(solverPhaseScope);
        stepScope0.setStepIndex(0);
        stepScope0.setStep(buildMoveScope(stepScope0, v1).getMove());
        acceptor.stepEnded(stepScope0);

        //Second step: tabuList contains v1 so any move containing v1 with a
        //resulting score less than the globally best known score (-100) should
        //get rejected. Otherwise the move will be accepted even though it
        //contains a tabu value
        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(solverPhaseScope);
        stepScope2.setStepIndex(1);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, -120, v0)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, -20, v0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, -120, v1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, -20, v1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, -120, v0, v1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, -20, v0, v1)));
        stepScope2.setStep(buildMoveScope(stepScope2, -20, v1).getMove());
        acceptor.stepEnded(stepScope2);

        acceptor.phaseEnded(solverPhaseScope);
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
