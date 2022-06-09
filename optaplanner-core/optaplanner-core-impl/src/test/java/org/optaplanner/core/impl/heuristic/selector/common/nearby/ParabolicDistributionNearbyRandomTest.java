package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testutil.TestRandom;

class ParabolicDistributionNearbyRandomTest {

    @Test
    void sizeMaximumTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ParabolicDistributionNearbyRandom(-10));
    }

    @Test
    void nextInt() {
        Random random = new TestRandom(
                0.0,
                1.0 - Math.pow(1 - 1.0 / 100.0, 3.0),
                1.0 - Math.pow(1 - 2.0 / 100.0, 3.0));
        NearbyRandom nearbyRandom = new ParabolicDistributionNearbyRandom(100);

        assertThat(nearbyRandom.nextInt(random, 500)).isEqualTo(0);
        assertThat(nearbyRandom.nextInt(random, 500)).isEqualTo(1);
        assertThat(nearbyRandom.nextInt(random, 500)).isEqualTo(2);
    }

    @Test
    void cornerCase() {
        Random random = new TestRandom(
                Math.nextAfter(1.0, Double.NEGATIVE_INFINITY),
                Math.nextAfter(1.0, Double.NEGATIVE_INFINITY),
                0, 0);
        NearbyRandom nearbyRandom = new ParabolicDistributionNearbyRandom(100);

        assertThat(nearbyRandom.nextInt(random, 500)).isEqualTo(99);
        assertThat(nearbyRandom.nextInt(random, 10)).isEqualTo(9);

        assertThat(nearbyRandom.nextInt(random, 500)).isEqualTo(0);
        assertThat(nearbyRandom.nextInt(random, 10)).isEqualTo(0);
    }

}
