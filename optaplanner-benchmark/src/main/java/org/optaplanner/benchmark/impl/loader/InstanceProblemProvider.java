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

import java.util.Objects;

import javax.xml.bind.annotation.XmlTransient;

import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public class InstanceProblemProvider<Solution_> implements ProblemProvider<Solution_> {

    private String problemName;
    @XmlTransient
    private Solution_ problem;
    @XmlTransient
    private SolutionCloner<Solution_> solutionCloner;

    public InstanceProblemProvider() {
        // Required by JAXB
    }

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
        }
        if (!(o instanceof InstanceProblemProvider)) {
            return false;
        }
        InstanceProblemProvider<?> that = (InstanceProblemProvider<?>) o;
        return Objects.equals(problemName, that.problemName) &&
                Objects.equals(problem, that.problem) &&
                Objects.equals(solutionCloner, that.solutionCloner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemName, problem, solutionCloner);
    }

    @Override
    public String toString() {
        return problemName;
    }

}
