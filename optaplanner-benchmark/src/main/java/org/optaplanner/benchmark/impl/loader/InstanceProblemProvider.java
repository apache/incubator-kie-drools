/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.impl.loader;

import java.io.File;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.io.FilenameUtils;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

@XStreamAlias("instanceProblemProvider")
public class InstanceProblemProvider<Solution_> implements ProblemProvider<Solution_> {

    private final String problemName;
    private final Solution_ problem;
    private final SolutionCloner<Solution_> solutionCloner;

    public InstanceProblemProvider(String problemName, SolutionDescriptor<Solution_> solutionDescriptor, Solution_ problem) {
        this.problemName = problemName;
        this.problem = problem;
        solutionCloner = solutionDescriptor.getSolutionCloner();
    }

    @Override
    public String getProblemName() {
        return problemName;
    }

    @Override
    public Solution_ readProblem() {
        // Return a planning clone so multiple solver benchmarks don't affect each other
        return solutionCloner.cloneSolution(problem);
    }

    @Override
    public void writeSolution(Solution_ solution, SubSingleBenchmarkResult subSingleBenchmarkResult) {
        // TODO maybe we can store them in a List and somehow return a List<List<Solution_>> from PlannerBenchmark?
        throw new UnsupportedOperationException("Writing the solution (" + solution
                + ") is not supported for a benchmark problem given as an instance.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof InstanceProblemProvider) {
            InstanceProblemProvider other = (InstanceProblemProvider) o;
            return problem.equals(other.problem);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return problem.hashCode();
    }

    @Override
    public String toString() {
        return problem.toString();
    }

}
