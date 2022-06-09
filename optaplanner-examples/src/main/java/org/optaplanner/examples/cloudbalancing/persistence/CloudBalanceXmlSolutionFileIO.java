package org.optaplanner.examples.cloudbalancing.persistence;

import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class CloudBalanceXmlSolutionFileIO extends XStreamSolutionFileIO<CloudBalance> {

    public CloudBalanceXmlSolutionFileIO() {
        super(CloudBalance.class);
    }
}
