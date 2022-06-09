package org.optaplanner.core.api.domain.solution;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.score.AbstractBendableScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

/**
 * Specifies that a property (or a field) on a {@link PlanningSolution} class holds the {@link Score} of that solution.
 * <p>
 * This property can be null if the {@link PlanningSolution} is uninitialized.
 * <p>
 * This property is modified by the {@link Solver},
 * every time when the {@link Score} of this {@link PlanningSolution} has been calculated.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface PlanningScore {

    /**
     * Required for bendable scores.
     * <p>
     * For example with 3 hard levels, hard level 0 always outweighs hard level 1 which always outweighs hard level 2,
     * which outweighs all the soft levels.
     *
     * @return 0 or higher if the {@link Score} is a {@link AbstractBendableScore}, not used otherwise
     */
    int bendableHardLevelsSize() default NO_LEVEL_SIZE;

    /**
     * Required for bendable scores.
     * <p>
     * For example with 3 soft levels, soft level 0 always outweighs soft level 1 which always outweighs soft level 2.
     *
     * @return 0 or higher if the {@link Score} is a {@link AbstractBendableScore}, not used otherwise
     */
    int bendableSoftLevelsSize() default NO_LEVEL_SIZE;

    /** Workaround for annotation limitation in {@link #bendableHardLevelsSize()} and {@link #bendableSoftLevelsSize()}. */
    int NO_LEVEL_SIZE = -1;

    /**
     * Overrides the default determined {@link ScoreDefinition} to implement a custom one.
     * <p>
     * If this is not specified, the {@link ScoreDefinition} is automatically determined
     * based on the return type of the annotated property (or field) on a {@link PlanningSolution}.
     *
     * @deprecated Support for custom scores is deprecated and will be removed in OptaPlanner 9.0.
     * @return {@link NullScoreDefinition} when it is null (workaround for annotation limitation)
     */
    @Deprecated(forRemoval = true)
    Class<? extends ScoreDefinition> scoreDefinitionClass() default NullScoreDefinition.class;

    /** Workaround for annotation limitation in {@link #scoreDefinitionClass()}. */
    @Deprecated(forRemoval = true)
    interface NullScoreDefinition extends ScoreDefinition {
    }

}
