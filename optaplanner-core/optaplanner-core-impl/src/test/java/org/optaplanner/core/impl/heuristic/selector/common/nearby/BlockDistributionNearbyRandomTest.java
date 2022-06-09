package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testutil.TestRandom;

class BlockDistributionNearbyRandomTest {

    @Test
    void sizeMinimumTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(-10, 300, 0.2, 0.0));
    }

    @Test
    void sizeMaximumTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(10, 8, 0.2, 0.0));
    }

    @Test
    void sizeRatioTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(10, 300, -0.2, 0.0));
    }

    @Test
    void sizeRatioTooHigh() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(10, 300, 1.2, 0.0));
    }

    @Test
    void uniformDistributionProbabilityTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(10, 300, 0.2, 1.3));
    }

    @Test
    void uniformDistributionProbabilityTooHigh() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(10, 300, 0.2, -0.3));
    }

    @Test
    void nextInt() {
        TestRandom random = new TestRandom(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(10, 300, 0.2, 0.0);

        assertThat(nearbyRandom.nextInt(random, 100)).isEqualTo(0);
        random.assertIntBoundJustRequested(20);
        assertThat(nearbyRandom.nextInt(random, 1000)).isEqualTo(1);
        random.assertIntBoundJustRequested(200);
        assertThat(nearbyRandom.nextInt(random, 10000)).isEqualTo(2);
        random.assertIntBoundJustRequested(300);
        assertThat(nearbyRandom.nextInt(random, 20)).isEqualTo(3);
        random.assertIntBoundJustRequested(10);
        assertThat(nearbyRandom.nextInt(random, 7)).isEqualTo(4);
        random.assertIntBoundJustRequested(7);

        nearbyRandom = new BlockDistributionNearbyRandom(100, 250, 1.0, 0.0);
        assertThat(nearbyRandom.nextInt(random, 700)).isEqualTo(5);
        random.assertIntBoundJustRequested(250);
        assertThat(nearbyRandom.nextInt(random, 170)).isEqualTo(6);
        random.assertIntBoundJustRequested(170);
        assertThat(nearbyRandom.nextInt(random, 70)).isEqualTo(7);
        random.assertIntBoundJustRequested(70);

        random = new TestRandom(0.3, 8, 0.5, 9);
        nearbyRandom = new BlockDistributionNearbyRandom(100, 500, 0.5, 0.4);
        assertThat(nearbyRandom.nextInt(random, 700)).isEqualTo(8);
        random.assertIntBoundJustRequested(700);
        assertThat(nearbyRandom.nextInt(random, 700)).isEqualTo(9);
        random.assertIntBoundJustRequested(350);
    }

    @Test
    void cornerCase() {
        double threshold = 0.5;
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(10, 100, 0.5, threshold);

        Random random = new TestRandom(Math.nextAfter(threshold, Double.NEGATIVE_INFINITY), -1);
        assertThat(nearbyRandom.nextInt(random, 1)).isEqualTo(-1);

        random = new TestRandom(threshold, -1, threshold, -1, -1, -1, -1, -1);
        assertThat(nearbyRandom.nextInt(random, 10)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 11)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 20)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 19)).isEqualTo(-1);

        // Rounding
        random = new TestRandom(threshold, -1, threshold, -2);
        assertThat(nearbyRandom.nextInt(random, 21)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 22)).isEqualTo(-2);

        random = new TestRandom(threshold, -1, -1, -1, -1, -1);
        assertThat(nearbyRandom.nextInt(random, 200)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 300)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 1000)).isEqualTo(-1);

        // Rounding
        random = new TestRandom(threshold, -3, threshold, -3, -2, -2, -2);
        assertThat(nearbyRandom.nextInt(random, 199)).isEqualTo(-3);
        assertThat(nearbyRandom.nextInt(random, 198)).isEqualTo(-3);
        assertThat(nearbyRandom.nextInt(random, 197)).isEqualTo(-2);

        random = new TestRandom(1, -1, -1, -2, -2, -2);
        assertThat(nearbyRandom.nextInt(random, 5)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 6)).isEqualTo(-2);
        assertThat(nearbyRandom.nextInt(random, 4)).isEqualTo(-2);
    }

}
