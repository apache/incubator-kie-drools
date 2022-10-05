package org.optaplanner.core.api.score.stream.quad;

import java.util.Collection;

import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintBuilder;
import org.optaplanner.core.api.score.stream.ConstraintJustification;

/**
 * Used to build a {@link Constraint} out of a {@link QuadConstraintStream}, applying optional configuration.
 * To build the constraint, use one of the terminal operations, such as {@link #asConstraint(String)}.
 * <p>
 * Unless {@link #justifyWith(PentaFunction)} is called, the default justification mapping will be used.
 * The function takes the input arguments and converts them into a {@link java.util.List}.
 * <p>
 * Unless {@link #indictWith(QuadFunction)} is called, the default indicted objects' mapping will be used.
 * The function takes the input arguments and converts them into a {@link java.util.List}.
 */
public interface QuadConstraintBuilder<A, B, C, D, Score_ extends Score<Score_>> extends ConstraintBuilder {

    /**
     * Sets a custom function to apply on a constraint match to justify it.
     *
     * @see ConstraintMatch
     * @param justificationMapping never null
     * @return this
     */
    <ConstraintJustification_ extends ConstraintJustification> QuadConstraintBuilder<A, B, C, D, Score_> justifyWith(
            PentaFunction<A, B, C, D, Score_, ConstraintJustification_> justificationMapping);

    /**
     * Sets a custom function to mark any object returned by it as responsible for causing the constraint to match.
     * Each object in the collection returned by this function will become an {@link Indictment}
     * and be available as a key in {@link ScoreExplanation#getIndictmentMap()}.
     *
     * @param indictedObjectsMapping never null
     * @return this
     */
    QuadConstraintBuilder<A, B, C, D, Score_> indictWith(QuadFunction<A, B, C, D, Collection<Object>> indictedObjectsMapping);

}
