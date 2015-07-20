package org.optaplanner.examples.nqueens.solver.tracking;

import org.optaplanner.examples.nqueens.domain.NQueens;

public class NQueensSolutionInitializer {

    public static NQueens initialize(NQueens solution) {
        for (int i = 0; i < solution.getQueenList().size(); i++) {
            solution.getQueenList().get(i).setRow(solution.getRowList().get(0));
        }
        return solution;
    }

}
