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

package org.optaplanner.core.config.localsearch;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

class LocalSearchPhaseConfigTest {

    @Test
    void withMethodCallsProperlyChain() {
        final int acceptedCountLimit = 5;
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig()
                .withLocalSearchType(LocalSearchType.TABU_SEARCH)
                .withTerminationConfig(new TerminationConfig().withBestScoreFeasible(true))
                .withForagerConfig(new LocalSearchForagerConfig().withAcceptedCountLimit(acceptedCountLimit));

        assertSoftly(softly -> {
            softly.assertThat(localSearchPhaseConfig.getLocalSearchType()).isEqualTo(LocalSearchType.TABU_SEARCH);
            softly.assertThat(localSearchPhaseConfig.getTerminationConfig()).isNotNull();
            softly.assertThat(localSearchPhaseConfig.getTerminationConfig().getBestScoreFeasible()).isTrue();
            softly.assertThat(localSearchPhaseConfig.getForagerConfig()).isNotNull();
            softly.assertThat(localSearchPhaseConfig.getForagerConfig().getAcceptedCountLimit())
                    .isEqualTo(acceptedCountLimit);
        });
    }
}
