package org.optaplanner.constraint.streams.common.tri;

import org.optaplanner.constraint.streams.common.AbstractConstraintBuilder;
import org.optaplanner.constraint.streams.common.ConstraintConstructor;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.tri.TriConstraintBuilder;

public final class TriConstraintBuilderImpl<A, B, C>
        extends AbstractConstraintBuilder<TriConstraintBuilder<A, B, C>>
        implements TriConstraintBuilder<A, B, C> {

    public TriConstraintBuilderImpl(ConstraintConstructor constraintConstructor, ScoreImpactType impactType,
            Score<?> constraintWeight) {
        super(constraintConstructor, impactType, constraintWeight);
    }

}
