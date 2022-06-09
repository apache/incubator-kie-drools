package org.optaplanner.examples.cheaptime.persistence;

import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class CheapTimeXmlSolutionFileIO extends XStreamSolutionFileIO<CheapTimeSolution> {

    public CheapTimeXmlSolutionFileIO() {
        super(CheapTimeSolution.class);
    }
}
