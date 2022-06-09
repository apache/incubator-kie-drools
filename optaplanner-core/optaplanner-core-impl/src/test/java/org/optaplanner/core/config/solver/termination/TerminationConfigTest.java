package org.optaplanner.core.config.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class TerminationConfigTest {

    @Test
    void overwriteSpentLimit() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setMinutesSpentLimit(1L);
        assertThat(terminationConfig.getMinutesSpentLimit()).isNotNull();
        terminationConfig.overwriteSpentLimit(Duration.ofHours(2L));
        assertThat(terminationConfig.getMinutesSpentLimit()).isNull();
    }

    @Test
    void overwriteUnimprovedSpentLimit() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setUnimprovedMinutesSpentLimit(1L);
        assertThat(terminationConfig.getUnimprovedMinutesSpentLimit()).isNotNull();
        terminationConfig.overwriteUnimprovedSpentLimit(Duration.ofHours(2L));
        assertThat(terminationConfig.getUnimprovedMinutesSpentLimit()).isNull();
    }

}
