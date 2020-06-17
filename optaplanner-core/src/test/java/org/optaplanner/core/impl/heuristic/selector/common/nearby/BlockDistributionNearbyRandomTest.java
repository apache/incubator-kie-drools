/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class BlockDistributionNearbyRandomTest {

    @Test
    public void sizeMinimumTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(-10, 300, 0.2, 0.0));
    }

    @Test
    public void sizeMaximumTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(10, 8, 0.2, 0.0));
    }

    @Test
    public void sizeRatioTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(10, 300, -0.2, 0.0));
    }

    @Test
    public void sizeRatioTooHigh() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(10, 300, 1.2, 0.0));
    }

    @Test
    public void uniformDistributionProbabilityTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(10, 300, 0.2, 1.3));
    }

    @Test
    public void uniformDistributionProbabilityTooHigh() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BlockDistributionNearbyRandom(10, 300, 0.2, -0.3));
    }

    @Test
    public void nextInt() {
        Random random = mock(Random.class);
        when(random.nextInt(anyInt())).thenReturn(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(10, 300, 0.2, 0.0);

        assertThat(nearbyRandom.nextInt(random, 100)).isEqualTo(0);
        verify(random).nextInt(20);
        assertThat(nearbyRandom.nextInt(random, 1000)).isEqualTo(1);
        verify(random).nextInt(200);
        assertThat(nearbyRandom.nextInt(random, 10000)).isEqualTo(2);
        verify(random).nextInt(300);
        assertThat(nearbyRandom.nextInt(random, 20)).isEqualTo(3);
        verify(random).nextInt(10);
        assertThat(nearbyRandom.nextInt(random, 7)).isEqualTo(4);
        verify(random).nextInt(7);

        nearbyRandom = new BlockDistributionNearbyRandom(100, 250, 1.0, 0.0);
        assertThat(nearbyRandom.nextInt(random, 700)).isEqualTo(5);
        verify(random).nextInt(250);
        assertThat(nearbyRandom.nextInt(random, 170)).isEqualTo(6);
        verify(random).nextInt(170);
        assertThat(nearbyRandom.nextInt(random, 70)).isEqualTo(7);
        verify(random).nextInt(70);

        when(random.nextDouble()).thenReturn(0.3);
        nearbyRandom = new BlockDistributionNearbyRandom(100, 500, 0.5, 0.4);
        assertThat(nearbyRandom.nextInt(random, 700)).isEqualTo(8);
        verify(random).nextInt(700);
        when(random.nextDouble()).thenReturn(0.5);
        assertThat(nearbyRandom.nextInt(random, 700)).isEqualTo(9);
        verify(random).nextInt(350);
    }

    @Test
    public void cornerCase() {
        Random random = mock(Random.class);
        double threshold = 0.5;
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(10, 100, 0.5, threshold);

        when(random.nextInt(anyInt())).thenReturn(-2);
        when(random.nextInt(1)).thenReturn(-1);
        when(random.nextDouble()).thenReturn(Math.nextAfter(threshold, Double.NEGATIVE_INFINITY));
        assertThat(nearbyRandom.nextInt(random, 1)).isEqualTo(-1);

        when(random.nextDouble()).thenReturn(threshold);
        when(random.nextInt(anyInt())).thenReturn(-2);
        when(random.nextInt(10)).thenReturn(-1);
        assertThat(nearbyRandom.nextInt(random, 10)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 11)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 20)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 19)).isEqualTo(-1);
        // Rounding
        assertThat(nearbyRandom.nextInt(random, 21)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 22)).isEqualTo(-2);

        when(random.nextInt(anyInt())).thenReturn(-2);
        when(random.nextInt(100)).thenReturn(-1);
        when(random.nextInt(99)).thenReturn(-3);
        assertThat(nearbyRandom.nextInt(random, 200)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 300)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 1000)).isEqualTo(-1);
        // Rounding
        assertThat(nearbyRandom.nextInt(random, 199)).isEqualTo(-3);
        assertThat(nearbyRandom.nextInt(random, 198)).isEqualTo(-3);
        assertThat(nearbyRandom.nextInt(random, 197)).isEqualTo(-2);

        when(random.nextInt(anyInt())).thenReturn(-2);
        when(random.nextInt(5)).thenReturn(-1);
        assertThat(nearbyRandom.nextInt(random, 5)).isEqualTo(-1);
        assertThat(nearbyRandom.nextInt(random, 6)).isEqualTo(-2);
        assertThat(nearbyRandom.nextInt(random, 4)).isEqualTo(-2);
    }

}
