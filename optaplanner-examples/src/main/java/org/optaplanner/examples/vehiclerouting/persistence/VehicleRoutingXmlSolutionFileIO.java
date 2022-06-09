package org.optaplanner.examples.vehiclerouting.persistence;

import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class VehicleRoutingXmlSolutionFileIO extends XStreamSolutionFileIO<VehicleRoutingSolution> {

    public VehicleRoutingXmlSolutionFileIO() {
        super(VehicleRoutingSolution.class);
    }
}
