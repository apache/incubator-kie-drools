package org.optaplanner.quarkus.it.domain;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

public class StringLengthVariableListener
        implements VariableListener<TestdataStringLengthShadowSolution, TestdataStringLengthShadowEntity> {

    @Override
    public void beforeEntityAdded(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        /* Nothing to do */
    }

    @Override
    public void afterEntityAdded(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        /* Nothing to do */
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        /* Nothing to do */
    }

    @Override
    public void afterVariableChanged(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        int oldLength = (entity.getLength() != null) ? entity.getLength() : 0;
        int newLength = getLength(entity.getValue());
        if (oldLength != newLength) {
            scoreDirector.beforeVariableChanged(entity, "length");
            entity.setLength(getLength(entity.getValue()));
            scoreDirector.afterVariableChanged(entity, "length");
        }
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        /* Nothing to do */
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<TestdataStringLengthShadowSolution> scoreDirector,
            TestdataStringLengthShadowEntity entity) {
        /* Nothing to do */
    }

    private static int getLength(String value) {
        if (value != null) {
            return value.length();
        } else {
            return 0;
        }
    }
}
