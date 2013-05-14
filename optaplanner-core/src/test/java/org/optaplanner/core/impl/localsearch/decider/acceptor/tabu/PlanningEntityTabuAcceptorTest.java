package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu;

import java.util.Arrays;
import java.util.Collection;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlanningEntityTabuAcceptorTest {

    @Test
    public void tabuSize() {
        PlanningEntityTabuAcceptor acceptor = new PlanningEntityTabuAcceptor();
        acceptor.setTabuSize(2);
        acceptor.setAspirationEnabled(true);

        TestdataEntity e0 = new TestdataEntity("e0");
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3");
        TestdataEntity e4 = new TestdataEntity("e4");

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(SimpleScore.valueOf(0));
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(solverScope);
        acceptor.phaseStarted(phaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        stepScope0.setStepIndex(0);
        LocalSearchMoveScope moveScope1 = buildMoveScope(stepScope0, e1);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e0)));
        assertEquals(true, acceptor.isAccepted(moveScope1));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e2))); // repeated call
        stepScope0.setStep(moveScope1.getMove());
        acceptor.stepEnded(stepScope0);
        
        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        stepScope1.setStepIndex(1);
        LocalSearchMoveScope moveScope2 = buildMoveScope(stepScope1, e2);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, e0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, e1)));
        assertEquals(true, acceptor.isAccepted(moveScope2));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, e3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, e4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, e2))); // repeated call
        stepScope1.setStep(moveScope2.getMove());
        acceptor.stepEnded(stepScope1);

        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(phaseScope);
        stepScope2.setStepIndex(2);
        LocalSearchMoveScope moveScope4 = buildMoveScope(stepScope2, e4);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, e0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, e3)));
        assertEquals(true, acceptor.isAccepted(moveScope4));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e2))); // repeated call
        stepScope2.setStep(moveScope4.getMove());
        acceptor.stepEnded(stepScope2);

        LocalSearchStepScope stepScope3 = new LocalSearchStepScope(phaseScope);
        stepScope3.setStepIndex(3);
        LocalSearchMoveScope moveScope3 = buildMoveScope(stepScope3, e3);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, e0)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, e1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e2)));
        assertEquals(true, acceptor.isAccepted(moveScope3));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e2))); // repeated call
        stepScope3.setStep(moveScope3.getMove());
        acceptor.stepEnded(stepScope3);

        LocalSearchStepScope stepScope4 = new LocalSearchStepScope(phaseScope);
        stepScope4.setStepIndex(4);
        LocalSearchMoveScope moveScope1Again = buildMoveScope(stepScope4, e1);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope4, e0)));
        assertEquals(true, acceptor.isAccepted(moveScope1Again));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope4, e2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope4, e3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope4, e4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope4, e2))); // repeated call
        stepScope4.setStep(moveScope1Again.getMove());
        acceptor.stepEnded(stepScope4);
        
        acceptor.phaseEnded(phaseScope);
    }

    @Test
    public void tabuSizeMultipleEntitiesPerStep() {
        PlanningEntityTabuAcceptor acceptor = new PlanningEntityTabuAcceptor();
        acceptor.setTabuSize(2);
        acceptor.setAspirationEnabled(true);

        TestdataEntity e0 = new TestdataEntity("e0");
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3");
        TestdataEntity e4 = new TestdataEntity("e4");

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(SimpleScore.valueOf(0));
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(solverScope);
        acceptor.phaseStarted(phaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        stepScope0.setStepIndex(0);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e0)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e0, e1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e0, e2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e0, e3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e0, e4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e1, e2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e1, e3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e1, e4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e2, e3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e2, e4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, e3, e4)));
        stepScope0.setStep(buildMoveScope(stepScope0, e0, e2).getMove());
        acceptor.stepEnded(stepScope0);

        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        stepScope1.setStepIndex(1);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, e0)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, e1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, e2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, e3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, e0, e1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, e0, e2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, e0, e3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, e0, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, e1, e2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, e1, e3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, e1, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, e2, e3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, e2, e4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, e3, e4)));
        stepScope1.setStep(buildMoveScope(stepScope1, e1).getMove());
        acceptor.stepEnded(stepScope1);

        LocalSearchStepScope stepScope2 = new LocalSearchStepScope(phaseScope);
        stepScope2.setStepIndex(2);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e2)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, e3)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e0, e1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e0, e2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e0, e3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e0, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e1, e2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e1, e3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e1, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e2, e3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, e2, e4)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, e3, e4)));
        stepScope2.setStep(buildMoveScope(stepScope2, e3, e4).getMove());
        acceptor.stepEnded(stepScope2);

        LocalSearchStepScope stepScope3 = new LocalSearchStepScope(phaseScope);
        stepScope3.setStepIndex(3);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, e0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, e2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e0, e1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope3, e0, e2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e0, e3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e0, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e1, e2)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e1, e3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e1, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e2, e3)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e2, e4)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope3, e3, e4)));
        stepScope3.setStep(buildMoveScope(stepScope3, e0).getMove());
        acceptor.stepEnded(stepScope3);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    public void aspiration() {
        PlanningEntityTabuAcceptor acceptor = new PlanningEntityTabuAcceptor();
        acceptor.setTabuSize(2);
        acceptor.setAspirationEnabled(true);

        TestdataEntity e0 = new TestdataEntity("e0");
        TestdataEntity e1 = new TestdataEntity("e1");

        DefaultSolverScope solverScope = new DefaultSolverScope();
        solverScope.setBestScore(SimpleScore.valueOf(-100));
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(solverScope);
        acceptor.phaseStarted(phaseScope);

        LocalSearchStepScope stepScope0 = new LocalSearchStepScope(phaseScope);
        stepScope0.setStepIndex(0);
        stepScope0.setStep(buildMoveScope(stepScope0, e1).getMove());
        acceptor.stepEnded(stepScope0);

        LocalSearchStepScope stepScope1 = new LocalSearchStepScope(phaseScope);
        stepScope1.setStepIndex(1);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -120, e0)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -20, e0)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -120, e1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -20, e1)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -120, e0, e1)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -20, e0, e1)));
        stepScope1.setStep(buildMoveScope(stepScope1, -20, e1).getMove());
        acceptor.stepEnded(stepScope1);

        acceptor.phaseEnded(phaseScope);
    }

    private LocalSearchMoveScope buildMoveScope(LocalSearchStepScope stepScope, TestdataEntity... entities) {
        return buildMoveScope(stepScope, 0, entities);
    }

    private LocalSearchMoveScope buildMoveScope(LocalSearchStepScope stepScope, int score, TestdataEntity... entities) {
        LocalSearchMoveScope moveScope = new LocalSearchMoveScope(stepScope);
        Move move = mock(Move.class);
        when(move.getPlanningEntities()).thenReturn((Collection) Arrays.asList(entities));
        moveScope.setMove(move);
        moveScope.setScore(SimpleScore.valueOf(score));
        return moveScope;
    }

}
