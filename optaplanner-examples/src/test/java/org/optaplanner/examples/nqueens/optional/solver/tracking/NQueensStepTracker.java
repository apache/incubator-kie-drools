package org.optaplanner.examples.nqueens.optional.solver.tracking;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;

public class NQueensStepTracker extends PhaseLifecycleListenerAdapter<NQueens> {

    private NQueens lastStepSolution = null;
    private List<NQueensStepTracking> trackingList = new ArrayList<>();

    public NQueensStepTracker() {
    }

    public List<NQueensStepTracking> getTrackingList() {
        return trackingList;
    }

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        lastStepSolution = (NQueens) phaseScope.getSolverScope().getBestSolution();
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        NQueens queens = (NQueens) stepScope.getWorkingSolution();
        for (int i = 0; i < queens.getQueenList().size(); i++) {
            Queen queen = queens.getQueenList().get(i);
            Queen lastStepQueen = lastStepSolution.getQueenList().get(i);
            if (queen.getRowIndex() != lastStepQueen.getRowIndex()) {
                trackingList.add(new NQueensStepTracking(queen.getColumnIndex(), queen.getRowIndex()));
                break;
            }
        }
        lastStepSolution = (NQueens) stepScope.createOrGetClonedSolution();
    }

}
