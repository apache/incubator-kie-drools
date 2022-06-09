package org.optaplanner.benchmark.config.ranking;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.benchmark.impl.ranking.TotalRankSolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.ranking.TotalScoreSolverRankingComparator;
import org.optaplanner.benchmark.impl.ranking.WorstScoreSolverRankingComparator;
import org.optaplanner.core.api.domain.solution.PlanningSolution;

@XmlEnum
public enum SolverRankingType {
    /**
     * Maximize the overall score, so minimize the overall cost if all {@link PlanningSolution}s would be executed.
     *
     * @see TotalScoreSolverRankingComparator
     */
    TOTAL_SCORE,
    /**
     * Minimize the worst case scenario.
     *
     * @see WorstScoreSolverRankingComparator
     */
    WORST_SCORE,
    /**
     * Maximize the overall ranking.
     *
     * @see TotalRankSolverRankingWeightFactory
     */
    TOTAL_RANKING;

}
