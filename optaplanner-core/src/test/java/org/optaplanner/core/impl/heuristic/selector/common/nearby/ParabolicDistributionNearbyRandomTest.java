/*
 * Copyright 2014 JBoss Inc
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

public class ParabolicDistributionNearbyRandomTest {

    @Test(expected = IllegalArgumentException.class)
    public void sizeMaximumTooLow() {
        NearbyRandom nearbyRandom = new ParabolicDistributionNearbyRandom(-10);
    }

    @Test
    public void nextInt() {
        Random random = mock(Random.class);
        NearbyRandom nearbyRandom = new ParabolicDistributionNearbyRandom(100);

        when(random.nextDouble()).thenReturn(0.0);
        assertEquals(0, nearbyRandom.nextInt(random, 500));
//        when(random.nextDouble()).thenReturn((99.0 * 99.0) * 3.0 / 1000000.0);
//        assertEquals(1, nearbyRandom.nextInt(random, 500));
//        when(random.nextDouble()).thenReturn((99.0 * 99.0 + 98.0 * 98.0) * 3.0 / 1000000.0);
//        assertEquals(2, nearbyRandom.nextInt(random, 500));
    }

}
