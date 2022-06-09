package org.optaplanner.constraint.streams.bavet.common;

import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraint;

public interface BavetScoringConstraintStream<Solution_> {

    void setConstraint(BavetConstraint<Solution_> constraint);

    void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet);

}
