package org.optaplanner.examples.nqueens.persistence;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.nqueens.domain.NQueens;

public class NQueensSolutionFileIO extends AbstractJsonSolutionFileIO<NQueens> {

    public NQueensSolutionFileIO() {
        super(NQueens.class);
    }
}
