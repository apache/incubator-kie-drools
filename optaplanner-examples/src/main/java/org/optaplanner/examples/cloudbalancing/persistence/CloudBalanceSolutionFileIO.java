package org.optaplanner.examples.cloudbalancing.persistence;

import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;

public class CloudBalanceSolutionFileIO extends JacksonSolutionFileIO<CloudBalance> {

    public CloudBalanceSolutionFileIO() {
        super(CloudBalance.class);
    }
}
