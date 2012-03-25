package org.drools.planner.core.localsearch.decider.acceptor.tabu;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.generic.GenericChangeMove;
import org.drools.planner.core.score.buildin.simple.DefaultSimpleScore;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.testdata.domain.TestdataEntity;
import org.drools.planner.core.testdata.domain.TestdataValue;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlanningEntityTabuAcceptorTest {

    @Test
    public void tabuSize() {
        PlanningEntityTabuAcceptor tabuAcceptor = new PlanningEntityTabuAcceptor();
        tabuAcceptor.setTabuSize(2);
        tabuAcceptor.setAspirationEnabled(true);

        TestdataEntity e0 = new TestdataEntity("e0");
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3");
        TestdataEntity e4 = new TestdataEntity("e4");

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(new DefaultSimpleScore(0));
        LocalSearchSolverPhaseScope solverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        tabuAcceptor.phaseStarted(solverPhaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(solverPhaseScope);
        stepScope0.setStepIndex(0);
        MoveScope moveScope1 = buildMoveScope(stepScope0, e1);
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e0)));
        assertEquals(true, tabuAcceptor.isAccepted(moveScope1));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e2)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e3)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e4)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e2))); // repeated call
        stepScope0.setStep(moveScope1.getMove());
        tabuAcceptor.stepTaken(stepScope0);
        
        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(solverPhaseScope);
        stepScope1.setStepIndex(1);
        MoveScope moveScope2 = buildMoveScope(stepScope1, e2);
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e0)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e1)));
        assertEquals(true, tabuAcceptor.isAccepted(moveScope2));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e3)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e4)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e2))); // repeated call
        stepScope1.setStep(moveScope2.getMove());
        tabuAcceptor.stepTaken(stepScope1);

        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(solverPhaseScope);
        stepScope2.setStepIndex(2);
        MoveScope moveScope4 = buildMoveScope(stepScope1, e4);
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e0)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e1)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e2)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e3)));
        assertEquals(true, tabuAcceptor.isAccepted(moveScope4));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e2))); // repeated call
        stepScope2.setStep(moveScope4.getMove());
        tabuAcceptor.stepTaken(stepScope2);

        LocalSearchStepScope stepScope3 = new LocalSearchStepScope(solverPhaseScope);
        stepScope3.setStepIndex(3);
        MoveScope moveScope3 = buildMoveScope(stepScope1, e3);
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e0)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e1)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e2)));
        assertEquals(true, tabuAcceptor.isAccepted(moveScope3));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e2))); // repeated call
        stepScope3.setStep(moveScope3.getMove());
        tabuAcceptor.stepTaken(stepScope3);

        LocalSearchStepScope stepScope4 = new LocalSearchStepScope(solverPhaseScope);
        stepScope4.setStepIndex(4);
        MoveScope moveScope1Again = buildMoveScope(stepScope1, e1);
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope4, e0)));
        assertEquals(true, tabuAcceptor.isAccepted(moveScope1Again));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope4, e2)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope4, e3)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope4, e4)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope4, e2))); // repeated call
        stepScope4.setStep(moveScope1Again.getMove());
        tabuAcceptor.stepTaken(stepScope4);
        
        tabuAcceptor.phaseEnded(solverPhaseScope);
    }

    @Test
    public void tabuSizeMultipleEntitiesPerStep() {
        PlanningEntityTabuAcceptor tabuAcceptor = new PlanningEntityTabuAcceptor();
        tabuAcceptor.setTabuSize(2);
        tabuAcceptor.setAspirationEnabled(true);

        TestdataEntity e0 = new TestdataEntity("e0");
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3");
        TestdataEntity e4 = new TestdataEntity("e4");

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(new DefaultSimpleScore(0));
        LocalSearchSolverPhaseScope solverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        tabuAcceptor.phaseStarted(solverPhaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(solverPhaseScope);
        stepScope0.setStepIndex(0);
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e0)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e1)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e2)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e3)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e4)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e0, e1)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e0, e2)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e0, e3)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e0, e4)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e1, e2)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e1, e3)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e1, e4)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e2, e3)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e2, e4)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope0, e3, e4)));
        stepScope0.setStep(buildMoveScope(stepScope0, e0, e2).getMove());
        tabuAcceptor.stepTaken(stepScope0);

        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(solverPhaseScope);
        stepScope1.setStepIndex(1);
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e0)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e1)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e2)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e3)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e0, e1)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e0, e2)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e0, e3)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e0, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e1, e2)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e1, e3)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e1, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e2, e3)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e2, e4)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, e3, e4)));
        stepScope1.setStep(buildMoveScope(stepScope1, e1).getMove());
        tabuAcceptor.stepTaken(stepScope1);

        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(solverPhaseScope);
        stepScope2.setStepIndex(2);
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e0)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e1)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e2)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e3)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e0, e1)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e0, e2)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e0, e3)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e0, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e1, e2)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e1, e3)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e1, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e2, e3)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e2, e4)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope2, e3, e4)));
        stepScope2.setStep(buildMoveScope(stepScope2, e3, e4).getMove());
        tabuAcceptor.stepTaken(stepScope2);

        LocalSearchStepScope stepScope3 = new LocalSearchStepScope(solverPhaseScope);
        stepScope3.setStepIndex(3);
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e0)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e1)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e2)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e3)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e0, e1)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e0, e2)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e0, e3)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e0, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e1, e2)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e1, e3)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e1, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e2, e3)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e2, e4)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope3, e3, e4)));
        stepScope3.setStep(buildMoveScope(stepScope3, e0).getMove());
        tabuAcceptor.stepTaken(stepScope3);

        tabuAcceptor.phaseEnded(solverPhaseScope);
    }

    @Test
    public void aspiration() {
        PlanningEntityTabuAcceptor tabuAcceptor = new PlanningEntityTabuAcceptor();
        tabuAcceptor.setTabuSize(2);
        tabuAcceptor.setAspirationEnabled(true);

        TestdataEntity e0 = new TestdataEntity("e0");
        TestdataEntity e1 = new TestdataEntity("e1");

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(new DefaultSimpleScore(-100));
        LocalSearchSolverPhaseScope solverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        tabuAcceptor.phaseStarted(solverPhaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(solverPhaseScope);
        stepScope0.setStepIndex(0);
        stepScope0.setStep(buildMoveScope(stepScope0, e1).getMove());
        tabuAcceptor.stepTaken(stepScope0);

        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(solverPhaseScope);
        stepScope1.setStepIndex(1);
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, -120, e0)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, -20, e0)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, -120, e1)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, -20, e1)));
        assertEquals(false, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, -120, e0, e1)));
        assertEquals(true, tabuAcceptor.isAccepted(buildMoveScope(stepScope1, -20, e0, e1)));
        stepScope1.setStep(buildMoveScope(stepScope1, -20, e1).getMove());
        tabuAcceptor.stepTaken(stepScope1);

        tabuAcceptor.phaseEnded(solverPhaseScope);
    }

    private MoveScope buildMoveScope(LocalSearchStepScope stepScope, TestdataEntity... entities) {
        return buildMoveScope(stepScope, 0, entities);
    }

    private MoveScope buildMoveScope(LocalSearchStepScope stepScope, int score, TestdataEntity... entities) {
        MoveScope moveScope = new MoveScope(stepScope);
        Move move = mock(Move.class);
        when(move.getPlanningEntities()).thenReturn((Collection) Arrays.asList(entities));
        moveScope.setMove(move);
        moveScope.setScore(new DefaultSimpleScore(score));
        return moveScope;
    }

}
