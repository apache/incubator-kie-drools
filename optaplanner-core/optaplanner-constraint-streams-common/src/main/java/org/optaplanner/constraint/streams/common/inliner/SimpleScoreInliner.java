package org.optaplanner.constraint.streams.common.inliner;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class SimpleScoreInliner extends AbstractScoreInliner<SimpleScore> {

    private int score;

    SimpleScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter<SimpleScore, SimpleScoreContext> buildWeightedScoreImpacter(Constraint constraint,
            SimpleScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        SimpleScoreContext context = new SimpleScoreContext(this, constraint, constraintWeight,
                impact -> this.score += impact);
        return WeightedScoreImpacter.of(context, SimpleScoreContext::changeScoreBy);
    }

    @Override
    public SimpleScore extractScore(int initScore) {
        return SimpleScore.ofUninitialized(initScore, score);
    }

    @Override
    public String toString() {
        return SimpleScore.class.getSimpleName() + " inliner";
    }

}
