/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BlockDistributionNearbyRandomTest {

    @Test(expected = IllegalArgumentException.class)
    public void sizeMinimumTooLow() {
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(-10, 300, 0.2, 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sizeMaximumTooLow() {
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(10, 8, 0.2, 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sizeRatioTooLow() {
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(10, 300, -0.2, 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sizeRatioTooHigh() {
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(10, 300, 1.2, 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void uniformDistributionProbabilityTooLow() {
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(10, 300, 0.2, 1.3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void uniformDistributionProbabilityTooHigh() {
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(10, 300, 0.2, -0.3);
    }

    @Test
    public void nextInt() {
        Random random = mock(Random.class);
        when(random.nextInt(anyInt())).thenReturn(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        NearbyRandom nearbyRandom = new BlockDistributionNearbyRandom(10, 300, 0.2, 0.0);

        assertEquals(0, nearbyRandom.nextInt(random, 100));
        verify(random).nextInt(20);
        assertEquals(1, nearbyRandom.nextInt(random, 1000));
        verify(random).nextInt(200);
        assertEquals(2, nearbyRandom.nextInt(random, 10000));
        verify(random).nextInt(300);
        assertEquals(3, nearbyRandom.nextInt(random, 20));
        verify(random).nextInt(10);
        assertEquals(4, nearbyRandom.nextInt(random, 7));
        verify(random).nextInt(7);

        nearbyRandom = new BlockDistributionNearbyRandom(100, 250, 1.0, 0.0);
        assertEquals(5, nearbyRandom.nextInt(random, 700));
        verify(random).nextInt(250);
        assertEquals(6, nearbyRandom.nextInt(random, 170));
        verify(random).nextInt(170);
        assertEquals(7, nearbyRandom.nextInt(random, 70));
        verify(random).nextInt(70);

        when(random.nextDouble()).thenReturn(0.3);
        nearbyRandom = new BlockDistributionNearbyRandom(100, 500, 0.5, 0.4);
        assertEquals(8, nearbyRandom.nextInt(random, 700));
        verify(random).nextInt(700);
        when(random.nextDouble()).thenReturn(0.5);
        assertEquals(9, nearbyRandom.nextInt(random, 700));
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
        assertEquals(-1, nearbyRandom.nextInt(random, 1));

        when(random.nextDouble()).thenReturn(threshold);
        when(random.nextInt(anyInt())).thenReturn(-2);
        when(random.nextInt(10)).thenReturn(-1);
        assertEquals(-1, nearbyRandom.nextInt(random, 10));
        assertEquals(-1, nearbyRandom.nextInt(random, 11));
        assertEquals(-1, nearbyRandom.nextInt(random, 20));
        assertEquals(-1, nearbyRandom.nextInt(random, 19));
        assertEquals(-1, nearbyRandom.nextInt(random, 21)); // Rounding
        assertEquals(-2, nearbyRandom.nextInt(random, 22));

        when(random.nextInt(anyInt())).thenReturn(-2);
        when(random.nextInt(100)).thenReturn(-1);
        when(random.nextInt(99)).thenReturn(-3);
        assertEquals(-1, nearbyRandom.nextInt(random, 200));
        assertEquals(-1, nearbyRandom.nextInt(random, 300));
        assertEquals(-1, nearbyRandom.nextInt(random, 1000));
        assertEquals(-3, nearbyRandom.nextInt(random, 199)); // Rounding
        assertEquals(-3, nearbyRandom.nextInt(random, 198));
        assertEquals(-2, nearbyRandom.nextInt(random, 197));

        when(random.nextInt(anyInt())).thenReturn(-2);
        when(random.nextInt(5)).thenReturn(-1);
        assertEquals(-1, nearbyRandom.nextInt(random, 5));
        assertEquals(-2, nearbyRandom.nextInt(random, 6));
        assertEquals(-2, nearbyRandom.nextInt(random, 4));
    }

}
