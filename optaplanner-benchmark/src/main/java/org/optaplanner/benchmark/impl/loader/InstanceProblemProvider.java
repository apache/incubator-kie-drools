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
        // Do not compare the solutionCloner, because the same extraProblem instance or the same problem inputFile
        // might be benchmarked with different solvers using different SolutionCloner configurations
        // yet they should be reported on a single BEST_SCORE graph
        return Objects.equals(problemName, that.problemName) &&
                Objects.equals(problem, that.problem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemName, problem);
    }

    @Override
    public String toString() {
        return problemName;
    }

}
