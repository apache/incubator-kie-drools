package org.optaplanner.examples.nqueens.integration.util;


import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.testdata.util.listeners.StepTestListener;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;

import java.util.ArrayList;
import java.util.List;

public class QueenCoordinatesStepListener extends StepTestListener {

    private List<Integer> filledColumns = new ArrayList<Integer>();
    private List<QueenCoordinates> coordinates = new ArrayList<QueenCoordinates>();

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        NQueens queens = (NQueens) stepScope.getWorkingSolution();

        for (Queen queen : queens.getQueenList()) {
            if (queen.getRow() != null && !filledColumns.contains(queen.getColumn().getIndex())) {
                filledColumns.add(queen.getColumn().getIndex());
                coordinates.add(new QueenCoordinates(queen.getColumnIndex(), queen.getRowIndex()));
            }
        }
    }

    public List<QueenCoordinates> getCoordinates() {
        return coordinates;
    }

}
