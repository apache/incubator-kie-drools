package org.optaplanner.core.impl.testdata.heuristic.move.factory;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.heuristic.move.TestdataChangeMoveWithCorruptedUndoMove;

public class TestdataChangeMoveWithCorruptedUndoMoveFactory implements MoveListFactory<TestdataSolution> {

    @Override
    public List<? extends Move<TestdataSolution>> createMoveList(TestdataSolution solution) {
        List<TestdataChangeMoveWithCorruptedUndoMove<TestdataSolution>> moveList = new ArrayList<>();

        for (TestdataEntity entity : solution.getEntityList()) {
            for (TestdataValue value : solution.getValueList()) {
                moveList.add(new TestdataChangeMoveWithCorruptedUndoMove<>(entity, value));
            }
        }
        return moveList;
    }
}
