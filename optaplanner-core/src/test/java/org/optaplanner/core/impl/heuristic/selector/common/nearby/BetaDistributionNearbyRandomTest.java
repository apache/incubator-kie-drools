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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class BetaDistributionNearbyRandomTest {

    @Test
    public void betaDistributionAlphaTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BetaDistributionNearbyRandom(-0.2, 0.3));
    }

    @Test
    public void betaDistributionBetaTooLow() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BetaDistributionNearbyRandom(0.2, -0.3));
    }

    @Test
    public void nextIntUniform() {
        Random random = mock(Random.class);
        NearbyRandom nearbyRandom = new BetaDistributionNearbyRandom(1.0, 1.0);

        when(random.nextDouble()).thenReturn(0.0);
        assertThat(nearbyRandom.nextInt(random, 500)).isEqualTo(0);
        when(random.nextDouble()).thenReturn(1.0 / 500.0);
        assertThat(nearbyRandom.nextInt(random, 500)).isEqualTo(1);
        when(random.nextDouble()).thenReturn(2.0 / 500.0);
        assertThat(nearbyRandom.nextInt(random, 500)).isEqualTo(2);
        when(random.nextDouble()).thenReturn(3.0 / 500.0);
        assertThat(nearbyRandom.nextInt(random, 500)).isEqualTo(3);
    }

}
