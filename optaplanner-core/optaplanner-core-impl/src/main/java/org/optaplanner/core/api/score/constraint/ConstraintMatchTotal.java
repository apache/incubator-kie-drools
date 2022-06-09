package org.optaplanner.core.api.score.constraint;

import java.util.Set;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;

/**
 * Explains the {@link Score} of a {@link PlanningSolution}, from the opposite side than {@link Indictment}.
 * Retrievable from {@link ScoreExplanation#getConstraintMatchTotalMap()}.
 * 
 * @param <Score_> the actual score type
 */
public interface ConstraintMatchTotal<Score_ extends Score<Score_>> {

    /**
     * @param constraintPackage never null
     * @param constraintName never null
     * @return never null
     */
    static String composeConstraintId(String constraintPackage, String constraintName) {
        return constraintPackage + "/" + constraintName;
    }

    /**
     * @return never null
     */
    String getConstraintPackage();

    /**
     * @return never null
     */
    String getConstraintName();

    /**
     * The value of the {@link ConstraintWeight} annotated member of the {@link ConstraintConfiguration}.
     * It's independent to the state of the {@link PlanningVariable planning variables}.
     * Do not confuse with {@link #getScore()}.
     *
     * @return null if {@link ConstraintWeight} isn't used for this constraint
     */
    Score_ getConstraintWeight();

    /**
     * @return never null
     */
    Set<ConstraintMatch<Score_>> getConstraintMatchSet();

    /**
     * @return {@code >= 0}
     */
    default int getConstraintMatchCount() {
        return getConstraintMatchSet().size();
    }

    /**
     * Sum of the {@link #getConstraintMatchSet()}'s {@link ConstraintMatch#getScore()}.
     *
     * @return never null
     */
    Score_ getScore();

    /**
     * To create a constraintId, use {@link #composeConstraintId(String, String)}.
     *
     * @return never null
     */
    String getConstraintId();

}
