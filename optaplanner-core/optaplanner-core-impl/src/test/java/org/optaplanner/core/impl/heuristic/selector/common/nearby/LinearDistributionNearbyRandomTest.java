package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testutil.TestRandom;

class LinearDistributionNearbyRandomTest {

    @Test
    void sizeMaximumTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new LinearDistributionNearbyRandom(-10));
    }

    @Test
    void nextInt() {
        NearbyRandom nearbyRandom = new LinearDistributionNearbyRandom(100);

        assertThat(nearbyRandom.nextInt(new TestRandom(0), 500)).isEqualTo(0);
        assertThat(nearbyRandom.nextInt(new TestRandom(2.0 / 100.0), 500)).isEqualTo(1);
        assertThat(nearbyRandom.nextInt(new TestRandom(2.0 / 100.0 + 2.0 / 100.0 + 2.0 / 10000.0), 500)).isEqualTo(2);
        assertThat(nearbyRandom.nextInt(new TestRandom(2.0 / 100.0 + 2.0 / 100.0 + 2.0 / 10000.0 + 2.0 / 100.0 + 4.0 / 10000.0),
                500)).isEqualTo(3);

        assertThat(nearbyRandom.nextInt(new TestRandom(0), 10)).isEqualTo(0);
        assertThat(nearbyRandom.nextInt(new TestRandom(2.0 / 10.0), 10)).isEqualTo(1);
    }

    @Test
    void cornerCase() {
        Random random = new TestRandom(
                Math.nextAfter(1.0, Double.NEGATIVE_INFINITY),
                Math.nextAfter(1.0, Double.NEGATIVE_INFINITY));
        NearbyRandom nearbyRandom = new LinearDistributionNearbyRandom(100);

        assertThat(nearbyRandom.nextInt(random, 10)).isEqualTo(9);

        assertThat(nearbyRandom.nextInt(random, 500)).isEqualTo(99);
    }

}
