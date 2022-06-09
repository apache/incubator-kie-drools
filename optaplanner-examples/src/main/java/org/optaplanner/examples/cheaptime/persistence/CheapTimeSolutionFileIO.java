package org.optaplanner.examples.cheaptime.persistence;

import java.io.File;

import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class CheapTimeSolutionFileIO implements SolutionFileIO<CheapTimeSolution> {

    private CheapTimeImporter importer = new CheapTimeImporter();
    private CheapTimeExporter exporter = new CheapTimeExporter();

    @Override
    public String getInputFileExtension() {
        return null;
    }

    @Override
    public String getOutputFileExtension() {
        return "txt";
    }

    @Override
    public CheapTimeSolution read(File inputSolutionFile) {
        return importer.readSolution(inputSolutionFile);
    }

    @Override
    public void write(CheapTimeSolution solution, File outputSolutionFile) {
        exporter.writeSolution(solution, outputSolutionFile);
    }

}
