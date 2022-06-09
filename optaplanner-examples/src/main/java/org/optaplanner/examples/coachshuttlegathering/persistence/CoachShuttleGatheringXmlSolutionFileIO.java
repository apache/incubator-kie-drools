package org.optaplanner.examples.coachshuttlegathering.persistence;

import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class CoachShuttleGatheringXmlSolutionFileIO extends XStreamSolutionFileIO<CoachShuttleGatheringSolution> {

    public CoachShuttleGatheringXmlSolutionFileIO() {
        super(CoachShuttleGatheringSolution.class);
    }
}
