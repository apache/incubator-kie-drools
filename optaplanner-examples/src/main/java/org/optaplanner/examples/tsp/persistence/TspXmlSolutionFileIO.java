package org.optaplanner.examples.tsp.persistence;

import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class TspXmlSolutionFileIO extends XStreamSolutionFileIO<TspSolution> {

    public TspXmlSolutionFileIO() {
        super(TspSolution.class);
    }
}
