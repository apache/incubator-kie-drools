package org.optaplanner.core.impl.score;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;
import org.optaplanner.core.impl.solver.DefaultSolverManager;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public final class DefaultScoreManager<Solution_, Score_ extends Score<Score_>>
        implements ScoreManager<Solution_, Score_> {

    private final InnerScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory;

    public <ProblemId_> DefaultScoreManager(SolverManager<Solution_, ProblemId_> solverManager) {
        this(((DefaultSolverManager<Solution_, ProblemId_>) solverManager).getSolverFactory());
    }

    public DefaultScoreManager(SolverFactory<Solution_> solverFactory) {
        this.scoreDirectorFactory = ((DefaultSolverFactory<Solution_>) solverFactory).getScoreDirectorFactory();
    }

    public InnerScoreDirectorFactory<Solution_, Score_> getScoreDirectorFactory() {
        return scoreDirectorFactory;
    }

    @Override
    public Score_ updateScore(Solution_ solution) {
        try (InnerScoreDirector<Solution_, Score_> scoreDirector = scoreDirectorFactory.buildScoreDirector(false, false)) {
            scoreDirector.setWorkingSolution(solution);
            return scoreDirector.calculateScore();
        }
    }

    @Override
    public String getSummary(Solution_ solution) {
        return explainScore(solution).getSummary();
    }

    @Override
    public ScoreExplanation<Solution_, Score_> explainScore(Solution_ solution) {
        try (InnerScoreDirector<Solution_, Score_> scoreDirector =
                scoreDirectorFactory.buildScoreDirector(false, true)) {
            scoreDirector.setWorkingSolution(solution); // Init the ScoreDirector first, else NPEs may be thrown.
            boolean constraintMatchEnabled = scoreDirector.isConstraintMatchEnabled();
            if (!constraintMatchEnabled) {
                throw new IllegalStateException("When constraintMatchEnabled (" + constraintMatchEnabled
                        + ") is disabled, this method should not be called.");
            }
            return new DefaultScoreExplanation<>(solution, scoreDirector.calculateScore(),
                    scoreDirector.getConstraintMatchTotalMap(), scoreDirector.getIndictmentMap());
        }
    }
}
