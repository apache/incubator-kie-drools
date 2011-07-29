package org.drools.planner.core.solution.director;

import org.drools.WorkingMemory;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;

/**
 * A SolutionDirector hold a workingSolution and directs the Rule Engine to calculate the {@link Score}
 * of that {@link Solution}.
 */
public interface SolutionDirector {

    /**
     * The {@link Solution} that is used in the {@link WorkingMemory}.
     * <p/>
     * If the {@link Solution} has been changed since {@link #calculateScoreFromWorkingMemory} has been called,
     * the {@link Solution#getScore()} of this {@link Solution} won't be correct.
     * @return never null
     */
    Solution getWorkingSolution();

    /**
     * @return never null
     */
    WorkingMemory getWorkingMemory();

    /**
     * Calculates the {@Score} and updates the workingSolution accordingly.
     * @return never null, the score of the working solution
     */
    Score calculateScoreFromWorkingMemory();

}
