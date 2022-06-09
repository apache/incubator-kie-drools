package org.optaplanner.benchmark.config.blueprint;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

import org.optaplanner.benchmark.config.SolverBenchmarkConfig;

@XmlType(propOrder = {
        "solverBenchmarkBluePrintType"
})
public class SolverBenchmarkBluePrintConfig {

    protected SolverBenchmarkBluePrintType solverBenchmarkBluePrintType = null;

    public SolverBenchmarkBluePrintType getSolverBenchmarkBluePrintType() {
        return solverBenchmarkBluePrintType;
    }

    public void setSolverBenchmarkBluePrintType(SolverBenchmarkBluePrintType solverBenchmarkBluePrintType) {
        this.solverBenchmarkBluePrintType = solverBenchmarkBluePrintType;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public List<SolverBenchmarkConfig> buildSolverBenchmarkConfigList() {
        validate();
        return solverBenchmarkBluePrintType.buildSolverBenchmarkConfigList();
    }

    protected void validate() {
        if (solverBenchmarkBluePrintType == null) {
            throw new IllegalArgumentException(
                    "The solverBenchmarkBluePrint must have"
                            + " a solverBenchmarkBluePrintType (" + solverBenchmarkBluePrintType + ").");
        }
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public SolverBenchmarkBluePrintConfig withSolverBenchmarkBluePrintType(
            SolverBenchmarkBluePrintType solverBenchmarkBluePrintType) {
        this.solverBenchmarkBluePrintType = solverBenchmarkBluePrintType;
        return this;
    }

}
