package org.optaplanner.benchmark.config.blueprint;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class SolverBenchmarkBluePrintConfigTest {

    @Test
    public void withoutSolverBenchmarkBluePrintType() {
        SolverBenchmarkBluePrintConfig config = new SolverBenchmarkBluePrintConfig();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(config::validate)
                .withMessageContaining("solverBenchmarkBluePrintType");
    }
}
