package org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptorTest;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class GreatDelugeAcceptorTest extends AbstractAcceptorTest {

    @Test
    public void isAcceptedPositiveLevelSingleScoreRainSpeed() {

        GreatDelugeAcceptor acceptor = new GreatDelugeAcceptor();
        acceptor.setInitialWaterLevels(SimpleScore.of(1100));
        acceptor.setRainSpeedScore(SimpleScore.of(100));

        DefaultSolverScope<TestdataSolution> solverScope = new DefaultSolverScope<>();
        solverScope.setBestScore(SimpleScore.of(-1000));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(SimpleScore.of(-1000));
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        acceptor.phaseStarted(phaseScope);


        // lastCompletedStepScore = -1000
        // water level 1000
        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        acceptor.stepStarted(stepScope0);
        LocalSearchMoveScope<TestdataSolution> moveScope0 = buildMoveScope(stepScope0, -500);
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900)));
        assertEquals(true, acceptor.isAccepted(moveScope0));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -800)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope0, -2000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -1000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope0, -900))); // Repeated call

        stepScope0.setStep(moveScope0.getMove());
        stepScope0.setScore(moveScope0.getScore());
        solverScope.setBestScore(moveScope0.getScore());
        acceptor.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);


        // lastCompletedStepScore = -500
        // water level 900
        LocalSearchStepScope<TestdataSolution> stepScope1 = new LocalSearchStepScope<>(phaseScope);
        acceptor.stepStarted(stepScope1);
        LocalSearchMoveScope<TestdataSolution> moveScope1 = buildMoveScope(stepScope1, -600);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -2000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -700)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -1000)));
        assertEquals(true, acceptor.isAccepted(moveScope1));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope1, -500)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope1, -901)));

        stepScope1.setStep(moveScope1.getMove());
        stepScope1.setScore(moveScope1.getScore());
        solverScope.setBestScore(moveScope1.getScore());
        acceptor.stepEnded(stepScope1);
        phaseScope.setLastCompletedStepScope(stepScope1);


        // lastCompletedStepScore = -600
        // water level 800
        LocalSearchStepScope<TestdataSolution> stepScope2 = new LocalSearchStepScope<>(phaseScope);
        acceptor.stepStarted(stepScope2);
        LocalSearchMoveScope<TestdataSolution> moveScope2 = buildMoveScope(stepScope1, -350);
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, -900)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, -2000)));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, -700)));
        assertEquals(false, acceptor.isAccepted(buildMoveScope(stepScope2, -801)));
        assertEquals(true, acceptor.isAccepted(moveScope2));
        assertEquals(true, acceptor.isAccepted(buildMoveScope(stepScope2, -500)));

        stepScope1.setStep(moveScope2.getMove());
        stepScope1.setScore(moveScope2.getScore());
        acceptor.stepEnded(stepScope2);
        phaseScope.setLastCompletedStepScope(stepScope2);

        acceptor.phaseEnded(phaseScope);
    }


    @Test
    public void isAcceptedPositiveLevelMultipleScoreRainSpeed() {

        GreatDelugeAcceptor acceptor = new GreatDelugeAcceptor();
        acceptor.setInitialWaterLevels(HardMediumSoftScore.of(0, 200, 500));
        acceptor.setRainSpeedScore(HardMediumSoftScore.of(0,100,100));

        DefaultSolverScope<TestdataSolution> solverScope = new DefaultSolverScope<>();
        solverScope.setBestScore(HardMediumSoftScore.of(0, -200, -1000));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(HardMediumSoftScore.of(0, -200, -1000));
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        acceptor.phaseStarted(phaseScope);


        // lastCompletedStepScore = 0/-200/-1000
        // water level 0/100/400
        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        acceptor.stepStarted(stepScope0);
        LocalSearchMoveScope<TestdataSolution> moveScope0 = new LocalSearchMoveScope<>(stepScope0, 0, mock(Move.class));
        moveScope0.setScore(HardMediumSoftScore.of(0,-100,-300));
        LocalSearchMoveScope<TestdataSolution> moveScope1 = new LocalSearchMoveScope<>(stepScope0, 0, mock(Move.class));
        moveScope1.setScore(HardMediumSoftScore.of(0,-100,-500));
        LocalSearchMoveScope<TestdataSolution> moveScope2 = new LocalSearchMoveScope<>(stepScope0, 0, mock(Move.class));
        moveScope2.setScore(HardMediumSoftScore.of(0,-50,-800));
        LocalSearchMoveScope<TestdataSolution> moveScope3 = new LocalSearchMoveScope<>(stepScope0, 0, mock(Move.class));
        moveScope3.setScore(HardMediumSoftScore.of(-5,-50,-100));
        LocalSearchMoveScope<TestdataSolution> moveScope4 = new LocalSearchMoveScope<>(stepScope0, 0, mock(Move.class));
        moveScope4.setScore(HardMediumSoftScore.of(0,-22,-200));

        assertEquals(true, acceptor.isAccepted(moveScope0));
        assertEquals(false, acceptor.isAccepted(moveScope1));
        assertEquals(true, acceptor.isAccepted(moveScope2));
        assertEquals(false, acceptor.isAccepted(moveScope3));
        assertEquals(true, acceptor.isAccepted(moveScope4));

        stepScope0.setStep(moveScope4.getMove());
        stepScope0.setScore(moveScope4.getScore());
        solverScope.setBestScore(moveScope4.getScore());
        acceptor.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    public void isAcceptedPositiveLevelMultipleScoreRainSpeedRatio() {

        GreatDelugeAcceptor acceptor = new GreatDelugeAcceptor();
        acceptor.setInitialWaterLevels(HardMediumSoftScore.of(0, 200, 500));
        acceptor.setRainSpeedRatio(0.9);

        DefaultSolverScope<TestdataSolution> solverScope = new DefaultSolverScope<>();
        solverScope.setBestScore(HardMediumSoftScore.of(0, -200, -1000));
        LocalSearchPhaseScope<TestdataSolution> phaseScope = new LocalSearchPhaseScope<>(solverScope);
        LocalSearchStepScope<TestdataSolution> lastCompletedStepScope = new LocalSearchStepScope<>(phaseScope, -1);
        lastCompletedStepScope.setScore(HardMediumSoftScore.of(0, -200, -1000));
        phaseScope.setLastCompletedStepScope(lastCompletedStepScope);
        acceptor.phaseStarted(phaseScope);


        // lastCompletedStepScore = 0/-200/-1000
        // water level 0/180/450
        LocalSearchStepScope<TestdataSolution> stepScope0 = new LocalSearchStepScope<>(phaseScope);
        acceptor.stepStarted(stepScope0);
        LocalSearchMoveScope<TestdataSolution> moveScope0 = new LocalSearchMoveScope<>(stepScope0, 0, mock(Move.class));
        moveScope0.setScore(HardMediumSoftScore.of(0,-180,-300));
        LocalSearchMoveScope<TestdataSolution> moveScope1 = new LocalSearchMoveScope<>(stepScope0, 0, mock(Move.class));
        moveScope1.setScore(HardMediumSoftScore.of(0,-180,-500));
        LocalSearchMoveScope<TestdataSolution> moveScope2 = new LocalSearchMoveScope<>(stepScope0, 0, mock(Move.class));
        moveScope2.setScore(HardMediumSoftScore.of(0,-50,-800));
        LocalSearchMoveScope<TestdataSolution> moveScope3 = new LocalSearchMoveScope<>(stepScope0, 0, mock(Move.class));
        moveScope3.setScore(HardMediumSoftScore.of(-5,-50,-100));
        LocalSearchMoveScope<TestdataSolution> moveScope4 = new LocalSearchMoveScope<>(stepScope0, 0, mock(Move.class));
        moveScope4.setScore(HardMediumSoftScore.of(0,-180,-450));

        assertEquals(true, acceptor.isAccepted(moveScope0));
        assertEquals(false, acceptor.isAccepted(moveScope1));
        assertEquals(true, acceptor.isAccepted(moveScope2));
        assertEquals(false, acceptor.isAccepted(moveScope3));
        assertEquals(true, acceptor.isAccepted(moveScope4));

        stepScope0.setStep(moveScope2.getMove());
        stepScope0.setScore(moveScope2.getScore());
        solverScope.setBestScore(moveScope2.getScore());
        acceptor.stepEnded(stepScope0);
        phaseScope.setLastCompletedStepScope(stepScope0);

        acceptor.phaseEnded(phaseScope);
    }

    @Test
    public void negativeWaterLevelSingleScore() {

        GreatDelugeAcceptor acceptor = new GreatDelugeAcceptor();
        acceptor.setInitialWaterLevels(SimpleScore.of(-100));
        try {
            acceptor.phaseStarted(null);
        } catch (IllegalArgumentException e) {
            assertEquals("The initial level (" + acceptor.getInitialWaterLevels()
                    + ") cannot have negative level (" + "-100.0" + ").", e.getMessage());
        }
    }
    @Test
    public void negativeWaterLevelMultipleScore() {

        GreatDelugeAcceptor acceptor = new GreatDelugeAcceptor();
        acceptor.setInitialWaterLevels(HardMediumSoftScore.parseScore("1hard/-1medium/2soft"));
        try {
            acceptor.phaseStarted(null);
        } catch (IllegalArgumentException e) {
            assertEquals("The initial level (" + acceptor.getInitialWaterLevels()
                    + ") cannot have negative level (" + "-1.0" + ").", e.getMessage());
        }
    }
   }