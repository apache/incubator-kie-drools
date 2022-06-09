package org.optaplanner.examples.nqueens.persistence;

import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class NQueensXmlSolutionFileIO extends XStreamSolutionFileIO<NQueens> {

    public NQueensXmlSolutionFileIO() {
        super(NQueens.class);
    }
}
