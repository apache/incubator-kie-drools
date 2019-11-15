package org.optaplanner.core.impl.testdata.heuristic.move;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestdataChangeMoveWithCorruptedUndoMove<TestdataSolution> extends AbstractMove<TestdataSolution> {

    private TestdataEntity entity;
    private TestdataValue toValue;

    public TestdataChangeMoveWithCorruptedUndoMove(TestdataEntity entity, TestdataValue toValue) {
        this.entity = entity;
        this.toValue = toValue;
    }

    @Override
    protected AbstractMove<TestdataSolution> createUndoMove(ScoreDirector<TestdataSolution> scoreDirector) {
        return new TestdataChangeMoveWithCorruptedUndoMove<>(entity, toValue); // corrupted undo move
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return Collections.singletonList(entity);
    }

    @Override
    public Collection<?> getPlanningValues() {
        return Collections.singletonList(toValue);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<TestdataSolution> scoreDirector) {
        scoreDirector.beforeVariableChanged(entity, "value");
        entity.setValue(toValue);
        scoreDirector.afterVariableChanged(entity, "value");
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<TestdataSolution> scoreDirector) {
        return !Objects.equals(entity.getValue(), toValue);
    }

    @Override
    public String toString() {
        return entity + " -> " + toValue;
    }
}
