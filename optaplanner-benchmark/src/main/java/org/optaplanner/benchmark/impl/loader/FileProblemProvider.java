package org.optaplanner.benchmark.impl.loader;

import java.io.File;

import javax.xml.bind.annotation.XmlTransient;

import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class FileProblemProvider<Solution_> implements ProblemProvider<Solution_> {

    @XmlTransient
    private SolutionFileIO<Solution_> solutionFileIO;

    private File problemFile;

    private FileProblemProvider() {
        // Required by JAXB
    }

    public FileProblemProvider(SolutionFileIO<Solution_> solutionFileIO, File problemFile) {
        this.solutionFileIO = solutionFileIO;
        this.problemFile = problemFile;
    }

    public SolutionFileIO<Solution_> getSolutionFileIO() {
        return solutionFileIO;
    }

    public File getProblemFile() {
        return problemFile;
    }

    @Override
    public String getProblemName() {
        String name = problemFile.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return name.substring(0, lastDotIndex);
        } else {
            return name;
        }
    }

    @Override
    public Solution_ readProblem() {
        return solutionFileIO.read(problemFile);
    }

    @Override
    public void writeSolution(Solution_ solution, SubSingleBenchmarkResult subSingleBenchmarkResult) {
        String filename = subSingleBenchmarkResult.getSingleBenchmarkResult().getProblemBenchmarkResult().getName()
                + "." + solutionFileIO.getOutputFileExtension();
        File solutionFile = new File(subSingleBenchmarkResult.getResultDirectory(), filename);
        solutionFileIO.write(solution, solutionFile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof FileProblemProvider) {
            FileProblemProvider other = (FileProblemProvider) o;
            return problemFile.equals(other.problemFile);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return problemFile.hashCode();
    }

    @Override
    public String toString() {
        return problemFile.toString();
    }

}
