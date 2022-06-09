package org.optaplanner.examples.vehiclerouting.persistence;

import java.io.File;

import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class VehicleRoutingFileIO implements SolutionFileIO<VehicleRoutingSolution> {

    private VehicleRoutingImporter importer = new VehicleRoutingImporter();

    @Override
    public String getInputFileExtension() {
        return "vrp";
    }

    @Override
    public VehicleRoutingSolution read(File inputSolutionFile) {
        return importer.readSolution(inputSolutionFile);
    }

    @Override
    public void write(VehicleRoutingSolution solution, File outputSolutionFile) {
        throw new UnsupportedOperationException();
    }

}
