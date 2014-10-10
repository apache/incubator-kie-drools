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

public class BlockDistributionNearbyRandomTest {

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

}
