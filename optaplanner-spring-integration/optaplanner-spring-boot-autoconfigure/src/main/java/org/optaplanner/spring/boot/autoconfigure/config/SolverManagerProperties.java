package org.optaplanner.spring.boot.autoconfigure.config;

public class SolverManagerProperties {

    /**
     * The number of solvers that run in parallel. This directly influences CPU consumption.
     * Defaults to "AUTO".
     * Other options include a number or formula based on the available processor count.
     */
    private String parallelSolverCount;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public String getParallelSolverCount() {
        return parallelSolverCount;
    }

    public void setParallelSolverCount(String parallelSolverCount) {
        this.parallelSolverCount = parallelSolverCount;
    }

}
