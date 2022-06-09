package org.optaplanner.core.config.solver.testutil.corruptedmove;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestdataCorruptedUndoMove extends AbstractTestdataMove {

    public TestdataCorruptedUndoMove(TestdataEntity entity, TestdataValue toValue) {
        super(entity, toValue);
    }

    @Override
    protected AbstractMove<TestdataSolution> createUndoMove(ScoreDirector<TestdataSolution> scoreDirector) {
        // Corrupts the undo move by not undo-ing the value
        return new TestdataCorruptedEntityUndoMove(entity, toValue);
    }
}
