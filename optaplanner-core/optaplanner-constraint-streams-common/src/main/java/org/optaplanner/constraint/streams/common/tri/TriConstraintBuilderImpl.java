package org.optaplanner.constraint.streams.common.tri;

import java.util.Collection;
import java.util.Objects;

import org.optaplanner.constraint.streams.common.AbstractConstraintBuilder;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintJustification;
import org.optaplanner.core.api.score.stream.tri.TriConstraintBuilder;

public final class TriConstraintBuilderImpl<A, B, C, Score_ extends Score<Score_>>
        extends AbstractConstraintBuilder<Score_>
        implements TriConstraintBuilder<A, B, C, Score_> {

    private QuadFunction<A, B, C, Score_, ConstraintJustification> justificationMapping;
    private TriFunction<A, B, C, Collection<Object>> indictedObjectsMapping;

    public TriConstraintBuilderImpl(TriConstraintConstructor<A, B, C, Score_> constraintConstructor, ScoreImpactType impactType,
            Score_ constraintWeight) {
        super(constraintConstructor, impactType, constraintWeight);
    }

    @Override
    protected QuadFunction<A, B, C, Score_, ConstraintJustification> getJustificationMapping() {
        return justificationMapping;
    }

    @Override
    public <ConstraintJustification_ extends ConstraintJustification> TriConstraintBuilder<A, B, C, Score_> justifyWith(
            QuadFunction<A, B, C, Score_, ConstraintJustification_> justificationMapping) {
        if (this.justificationMapping != null) {
            throw new IllegalStateException("Justification mapping already set (" + justificationMapping + ").");
        }
        this.justificationMapping =
                (QuadFunction<A, B, C, Score_, ConstraintJustification>) Objects.requireNonNull(justificationMapping);
        return this;
    }

    @Override
    protected TriFunction<A, B, C, Collection<Object>> getIndictedObjectsMapping() {
        return indictedObjectsMapping;
    }

    @Override
    public TriConstraintBuilder<A, B, C, Score_> indictWith(TriFunction<A, B, C, Collection<Object>> indictedObjectsMapping) {
        if (this.indictedObjectsMapping != null) {
            throw new IllegalStateException("Indicted objects' mapping already set (" + indictedObjectsMapping + ").");
        }
        this.indictedObjectsMapping = Objects.requireNonNull(indictedObjectsMapping);
        return this;
    }

}
