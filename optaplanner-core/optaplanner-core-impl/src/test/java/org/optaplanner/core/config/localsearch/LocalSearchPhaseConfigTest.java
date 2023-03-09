package org.optaplanner.core.config.localsearch;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

class LocalSearchPhaseConfigTest {

    @Test
    void withMethodCallsProperlyChain() {
        final int acceptedCountLimit = 5;
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig()
                .withLocalSearchType(LocalSearchType.TABU_SEARCH)
                .withTerminationConfig(new TerminationConfig().withBestScoreFeasible(true))
                .withForagerConfig(new LocalSearchForagerConfig().withAcceptedCountLimit(acceptedCountLimit));

        assertSoftly(softly -> {
            softly.assertThat(localSearchPhaseConfig.getLocalSearchType()).isEqualTo(LocalSearchType.TABU_SEARCH);
            softly.assertThat(localSearchPhaseConfig.getTerminationConfig()).isNotNull();
            softly.assertThat(localSearchPhaseConfig.getTerminationConfig().getBestScoreFeasible()).isTrue();
            softly.assertThat(localSearchPhaseConfig.getForagerConfig()).isNotNull();
            softly.assertThat(localSearchPhaseConfig.getForagerConfig().getAcceptedCountLimit())
                    .isEqualTo(acceptedCountLimit);
        });
    }
}
