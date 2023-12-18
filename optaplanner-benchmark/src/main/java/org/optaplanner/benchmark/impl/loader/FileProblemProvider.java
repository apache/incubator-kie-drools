/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.benchmark.impl.loader;

import java.io.File;

import jakarta.xml.bind.annotation.XmlTransient;

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
