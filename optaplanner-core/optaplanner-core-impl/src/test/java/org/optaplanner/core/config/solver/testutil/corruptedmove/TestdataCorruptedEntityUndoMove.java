package org.optaplanner.core.config.solver.testutil.corruptedmove;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestdataCorruptedEntityUndoMove extends AbstractTestdataMove {

    public TestdataCorruptedEntityUndoMove(TestdataEntity entity, TestdataValue toValue) {
        super(entity, toValue);
    }

    @Override
    protected AbstractMove<TestdataSolution> createUndoMove(ScoreDirector<TestdataSolution> scoreDirector) {
        // Corrupts the undo move by creating a new entity and not undo-ing the value
        return new TestdataCorruptedEntityUndoMove(new TestdataEntity("corrupted"), toValue);
    }
}
