package org.optaplanner.examples.tennis.persistence;

import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class TennisXmlSolutionFileIO extends XStreamSolutionFileIO<TennisSolution> {

    public TennisXmlSolutionFileIO() {
        super(TennisSolution.class);
    }
}
