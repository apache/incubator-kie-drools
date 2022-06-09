package org.optaplanner.core.impl.phase.scope;

import java.util.Random;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractPhaseScope<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final SolverScope<Solution_> solverScope;

    protected Long startingSystemTimeMillis;
    protected Long startingScoreCalculationCount;
    protected Score startingScore;
    protected Long endingSystemTimeMillis;
    protected Long endingScoreCalculationCount;
    protected long childThreadsScoreCalculationCount = 0;

    protected int bestSolutionStepIndex;

    public AbstractPhaseScope(SolverScope<Solution_> solverScope) {
        this.solverScope = solverScope;
    }

    public SolverScope<Solution_> getSolverScope() {
        return solverScope;
    }

    public Long getStartingSystemTimeMillis() {
        return startingSystemTimeMillis;
    }

    public <Score_ extends Score<Score_>> Score_ getStartingScore() {
        return (Score_) startingScore;
    }

    public Long getEndingSystemTimeMillis() {
        return endingSystemTimeMillis;
    }

    public int getBestSolutionStepIndex() {
        return bestSolutionStepIndex;
    }

    public void setBestSolutionStepIndex(int bestSolutionStepIndex) {
        this.bestSolutionStepIndex = bestSolutionStepIndex;
    }

    public abstract AbstractStepScope<Solution_> getLastCompletedStepScope();

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public void reset() {
        bestSolutionStepIndex = -1;
        // TODO Usage of solverScope.getBestScore() would be better performance wise but is null with a uninitialized score
        startingScore = solverScope.calculateScore();
        if (getLastCompletedStepScope().getStepIndex() < 0) {
            getLastCompletedStepScope().setScore(startingScore);
        }
    }

    public void startingNow() {
        startingSystemTimeMillis = System.currentTimeMillis();
        startingScoreCalculationCount = getScoreDirector().getCalculationCount();
    }

    public void endingNow() {
        endingSystemTimeMillis = System.currentTimeMillis();
        endingScoreCalculationCount = getScoreDirector().getCalculationCount();
    }

    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solverScope.getSolutionDescriptor();
    }

    public long calculateSolverTimeMillisSpentUpToNow() {
        return solverScope.calculateTimeMillisSpentUpToNow();
    }

    public long calculatePhaseTimeMillisSpentUpToNow() {
        long now = System.currentTimeMillis();
        return now - startingSystemTimeMillis;
    }

    public long getPhaseTimeMillisSpent() {
        return endingSystemTimeMillis - startingSystemTimeMillis;
    }

    public void addChildThreadsScoreCalculationCount(long addition) {
        solverScope.addChildThreadsScoreCalculationCount(addition);
        childThreadsScoreCalculationCount += addition;
    }

    public long getPhaseScoreCalculationCount() {
        return endingScoreCalculationCount - startingScoreCalculationCount + childThreadsScoreCalculationCount;
    }

    /**
     * @return at least 0, per second
     */
    public long getPhaseScoreCalculationSpeed() {
        long timeMillisSpent = getPhaseTimeMillisSpent();
        // Avoid divide by zero exception on a fast CPU
        return getPhaseScoreCalculationCount() * 1000L / (timeMillisSpent == 0L ? 1L : timeMillisSpent);
    }

    public <Score_ extends Score<Score_>> InnerScoreDirector<Solution_, Score_> getScoreDirector() {
        return solverScope.getScoreDirector();
    }

    public Solution_ getWorkingSolution() {
        return solverScope.getWorkingSolution();
    }

    public int getWorkingEntityCount() {
        return solverScope.getWorkingEntityCount();
    }

    public int getWorkingValueCount() {
        return solverScope.getWorkingValueCount();
    }

    public <Score_ extends Score<Score_>> Score_ calculateScore() {
        return (Score_) solverScope.calculateScore();
    }

    public <Score_ extends Score<Score_>> void assertExpectedWorkingScore(Score_ expectedWorkingScore,
            Object completedAction) {
        InnerScoreDirector<Solution_, Score_> innerScoreDirector = getScoreDirector();
        innerScoreDirector.assertExpectedWorkingScore(expectedWorkingScore, completedAction);
    }

    public <Score_ extends Score<Score_>> void assertWorkingScoreFromScratch(Score_ workingScore,
            Object completedAction) {
        InnerScoreDirector<Solution_, Score_> innerScoreDirector = getScoreDirector();
        innerScoreDirector.assertWorkingScoreFromScratch(workingScore, completedAction);
    }

    public <Score_ extends Score<Score_>> void assertPredictedScoreFromScratch(Score_ workingScore,
            Object completedAction) {
        InnerScoreDirector<Solution_, Score_> innerScoreDirector = getScoreDirector();
        innerScoreDirector.assertPredictedScoreFromScratch(workingScore, completedAction);
    }

    public <Score_ extends Score<Score_>> void assertShadowVariablesAreNotStale(Score_ workingScore,
            Object completedAction) {
        InnerScoreDirector<Solution_, Score_> innerScoreDirector = getScoreDirector();
        innerScoreDirector.assertShadowVariablesAreNotStale(workingScore, completedAction);
    }

    public Random getWorkingRandom() {
        return getSolverScope().getWorkingRandom();
    }

    public boolean isBestSolutionInitialized() {
        return solverScope.isBestSolutionInitialized();
    }

    public <Score_ extends Score<Score_>> Score_ getBestScore() {
        return (Score_) solverScope.getBestScore();
    }

    public long getPhaseBestSolutionTimeMillis() {
        long bestSolutionTimeMillis = solverScope.getBestSolutionTimeMillis();
        // If the termination is explicitly phase configured, previous phases must not affect it
        if (bestSolutionTimeMillis < startingSystemTimeMillis) {
            bestSolutionTimeMillis = startingSystemTimeMillis;
        }
        return bestSolutionTimeMillis;
    }

    public int getNextStepIndex() {
        return getLastCompletedStepScope().getStepIndex() + 1;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName(); // TODO add + "(" + phaseIndex + ")"
    }

}
