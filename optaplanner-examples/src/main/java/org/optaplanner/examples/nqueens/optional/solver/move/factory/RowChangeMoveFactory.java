package org.optaplanner.examples.nqueens.optional.solver.move.factory;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.domain.Row;
import org.optaplanner.examples.nqueens.optional.solver.move.RowChangeMove;

public class RowChangeMoveFactory implements MoveListFactory<NQueens> {

    @Override
    public List<RowChangeMove> createMoveList(NQueens nQueens) {
        List<RowChangeMove> moveList = new ArrayList<>();
        for (Queen queen : nQueens.getQueenList()) {
            for (Row toRow : nQueens.getRowList()) {
                moveList.add(new RowChangeMove(queen, toRow));
            }
        }
        return moveList;
    }

}
