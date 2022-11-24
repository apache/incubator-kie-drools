package org.optaplanner.examples.nqueens.persistence;

import org.optaplanner.examples.common.persistence.jackson.AbstractExampleSolutionFileIO;
import org.optaplanner.examples.nqueens.domain.NQueens;

public class NQueensSolutionFileIO extends AbstractExampleSolutionFileIO<NQueens> {

    public NQueensSolutionFileIO() {
        super(NQueens.class);
    }
}
