package org.optaplanner.examples.tennis.persistence;

import org.optaplanner.examples.common.persistence.jackson.AbstractExampleSolutionFileIO;
import org.optaplanner.examples.tennis.domain.TennisSolution;

public class TennisSolutionFileIO extends AbstractExampleSolutionFileIO<TennisSolution> {

    public TennisSolutionFileIO() {
        super(TennisSolution.class);
    }
}
