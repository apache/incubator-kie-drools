package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testutil.TestRandom;

class BetaDistributionNearbyRandomTest {

    @Test
    void betaDistributionAlphaTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BetaDistributionNearbyRandom(-0.2, 0.3));
    }

    @Test
    void betaDistributionBetaTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BetaDistributionNearbyRandom(0.2, -0.3));
    }

    @Test
    void nextIntUniform() {
        NearbyRandom nearbyRandom = new BetaDistributionNearbyRandom(1.0, 1.0);

        assertThat(nearbyRandom.nextInt(new TestRandom(0), 500)).isEqualTo(0);
        assertThat(nearbyRandom.nextInt(new TestRandom(1.0 / 500.0), 500)).isEqualTo(1);
        assertThat(nearbyRandom.nextInt(new TestRandom(2.0 / 500.0), 500)).isEqualTo(2);
        assertThat(nearbyRandom.nextInt(new TestRandom(3.0 / 500.0), 500)).isEqualTo(3);
    }

}
