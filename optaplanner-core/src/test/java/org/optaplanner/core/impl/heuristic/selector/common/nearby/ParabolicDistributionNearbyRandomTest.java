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

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.util.TestRandom;

public class ParabolicDistributionNearbyRandomTest {

    @Test
    public void sizeMaximumTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ParabolicDistributionNearbyRandom(-10));
    }

    @Test
    public void nextInt() {
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
    public void cornerCase() {
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
