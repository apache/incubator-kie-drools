package org.optaplanner.examples.common.solver.tracking;


import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.testdata.util.listeners.StepTestListener;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;

import java.util.ArrayList;
import java.util.List;

public class NQueensStepTracker extends StepTestListener {

    private List<Integer> filledColumns = new ArrayList<Integer>();
    private List<NQueensStepTracking> trackingList = new ArrayList<NQueensStepTracking>();

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        NQueens queens = (NQueens) stepScope.getWorkingSolution();

        for (Queen queen : queens.getQueenList()) {
            if (queen.getRow() != null && !filledColumns.contains(queen.getColumn().getIndex())) {
                filledColumns.add(queen.getColumn().getIndex());
                trackingList.add(new NQueensStepTracking(queen.getColumnIndex(), queen.getRowIndex()));
            }
        }
    }

    public List<NQueensStepTracking> getTrackingList() {
        return trackingList;
    }

}
