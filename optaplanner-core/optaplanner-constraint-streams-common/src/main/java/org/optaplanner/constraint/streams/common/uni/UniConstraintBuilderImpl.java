package org.optaplanner.constraint.streams.common.uni;

import org.optaplanner.constraint.streams.common.AbstractConstraintBuilder;
import org.optaplanner.constraint.streams.common.ConstraintConstructor;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.uni.UniConstraintBuilder;

public final class UniConstraintBuilderImpl<A>
        extends AbstractConstraintBuilder<UniConstraintBuilder<A>>
        implements UniConstraintBuilder<A> {

    public UniConstraintBuilderImpl(ConstraintConstructor constraintConstructor, ScoreImpactType impactType,
            Score<?> constraintWeight) {
        super(constraintConstructor, impactType, constraintWeight);
    }

}
