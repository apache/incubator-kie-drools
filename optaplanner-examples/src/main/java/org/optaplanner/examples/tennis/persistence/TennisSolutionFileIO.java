package org.optaplanner.examples.tennis.persistence;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.tennis.domain.TennisSolution;

public class TennisSolutionFileIO extends AbstractJsonSolutionFileIO<TennisSolution> {

    public TennisSolutionFileIO() {
        super(TennisSolution.class);
    }
}
