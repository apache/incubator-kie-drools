package org.optaplanner.benchmark.config.blueprint;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

public class SolverBenchmarkBluePrintConfigTest {

    @Test
    public void withoutSolverBenchmarkBluePrintType() {
        SolverBenchmarkBluePrintConfig config = new SolverBenchmarkBluePrintConfig();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(config::validate)
                .withMessageContaining("solverBenchmarkBluePrintType");
    }
}
