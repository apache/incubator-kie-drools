package org.optaplanner.core.api.score;

import java.util.Map;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.score.stream.Constraint;

/**
 * Build by {@link ScoreManager#explainScore(Object)} to hold {@link ConstraintMatchTotal}s and {@link Indictment}s
 * necessary to explain the quality of a particular {@link Score}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the actual score type
 */
public interface ScoreExplanation<Solution_, Score_ extends Score<Score_>> {

    /**
     * Retrieve the {@link PlanningSolution} that the score being explained comes from.
     *
     * @return never null
     */
    Solution_ getSolution();

    /**
     * Return the {@link Score} being explained.
     * If the specific {@link Score} type used by the {@link PlanningSolution} is required,
     * call {@link #getSolution()} and retrieve it from there.
     * 
     * @return never null
     */
    Score_ getScore();

    /**
     * Returns a diagnostic text that explains the {@link Score} through the {@link ConstraintMatch} API
     * to identify which constraints or planning entities cause that score quality.
     * In case of an {@link Score#isFeasible() infeasible} solution,
     * this can help diagnose the cause of that.
     * <p>
     * Do not parse this string.
     * Instead, to provide this information in a UI or a service,
     * use {@link #getConstraintMatchTotalMap()} and {@link #getIndictmentMap()}
     * and convert those into a domain specific API.
     *
     * @return never null
     */
    String getSummary();

    /**
     * Explains the {@link Score} of {@link #getScore()} ()} by splitting it up per {@link Constraint}.
     * <p>
     * The sum of {@link ConstraintMatchTotal#getScore()} equals {@link #getScore()} ()}.
     *
     * @return never null, the key is the {@link ConstraintMatchTotal#getConstraintId() constraintId}
     *         (to create one, use {@link ConstraintMatchTotal#composeConstraintId(String, String)}).
     * @see #getIndictmentMap()
     */
    Map<String, ConstraintMatchTotal<Score_>> getConstraintMatchTotalMap();

    /**
     * Explains the impact of each planning entity or problem fact on the {@link Score}.
     * An {@link Indictment} is basically the inverse of a {@link ConstraintMatchTotal}:
     * it is a {@link Score} total for each justification {@link Object}
     * in {@link ConstraintMatch#getJustificationList()}.
     * <p>
     * The sum of {@link ConstraintMatchTotal#getScore()} differs from {@link #getScore()} ()}
     * because each {@link ConstraintMatch#getScore()} is counted
     * for each justification in {@link ConstraintMatch#getJustificationList()}.
     *
     * @return never null, the key is a {@link ProblemFactCollectionProperty problem fact} or a
     *         {@link PlanningEntity planning entity}
     * @see #getConstraintMatchTotalMap()
     */
    Map<Object, Indictment<Score_>> getIndictmentMap();

}
