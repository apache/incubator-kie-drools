package org.optaplanner.core.config.solver.testutil.corruptedmoves.factory;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.config.solver.testutil.corruptedmoves.AbstractTestdataMove;
import org.optaplanner.core.config.solver.testutil.corruptedmoves.TestdataCorruptedEntityUndoMove;
import org.optaplanner.core.config.solver.testutil.corruptedmoves.TestdataCorruptedUndoMove;

public class AbstractTestdataCorruptedUndoMoveTotalMappingFactory implements MoveListFactory<TestdataSolution> {

    private boolean corruptEntityAsWell;

    AbstractTestdataCorruptedUndoMoveTotalMappingFactory(boolean corruptEntityAsWell) {
        this.corruptEntityAsWell = corruptEntityAsWell;
    }

    @Override
    public List<? extends Move<TestdataSolution>> createMoveList(TestdataSolution solution) {
        List<AbstractTestdataMove> moveList = new ArrayList<>();

        for (TestdataEntity entity : solution.getEntityList()) {
            for (TestdataValue value : solution.getValueList()) {
                if (corruptEntityAsWell) {
                    moveList.add(new TestdataCorruptedEntityUndoMove(entity, value));
                } else {
                    moveList.add(new TestdataCorruptedUndoMove(entity, value));
                }
            }
        }
        return moveList;
    }
}
