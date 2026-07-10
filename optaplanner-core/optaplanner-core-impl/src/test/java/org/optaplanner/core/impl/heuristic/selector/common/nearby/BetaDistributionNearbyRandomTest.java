/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
