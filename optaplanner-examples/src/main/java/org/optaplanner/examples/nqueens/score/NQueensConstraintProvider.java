package org.optaplanner.examples.nqueens.score;

import static org.optaplanner.core.api.score.stream.Joiners.equal;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.nqueens.domain.Queen;

public class NQueensConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                horizontalConflict(factory),
                ascendingDiagonalConflict(factory),
                descendingDiagonalConflict(factory),
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    protected Constraint horizontalConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Queen.class, equal(Queen::getRowIndex))
                .penalize("Horizontal conflict", SimpleScore.ONE);
    }

    protected Constraint ascendingDiagonalConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Queen.class, equal(Queen::getAscendingDiagonalIndex))
                .penalize("Ascending diagonal conflict", SimpleScore.ONE);
    }

    protected Constraint descendingDiagonalConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Queen.class, equal(Queen::getDescendingDiagonalIndex))
                .penalize("Descending diagonal conflict", SimpleScore.ONE);
    }

}
