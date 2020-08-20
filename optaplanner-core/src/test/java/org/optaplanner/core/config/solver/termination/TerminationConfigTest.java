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

package org.optaplanner.core.config.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class TerminationConfigTest {

    @Test
    void overwriteSpentLimit() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setMinutesSpentLimit(1L);
        assertThat(terminationConfig.getMinutesSpentLimit()).isNotNull();
        terminationConfig.overwriteSpentLimit(Duration.ofHours(2L));
        assertThat(terminationConfig.getMinutesSpentLimit()).isNull();
    }

    @Test
    void overwriteUnimprovedSpentLimit() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setUnimprovedMinutesSpentLimit(1L);
        assertThat(terminationConfig.getUnimprovedMinutesSpentLimit()).isNotNull();
        terminationConfig.overwriteUnimprovedSpentLimit(Duration.ofHours(2L));
        assertThat(terminationConfig.getUnimprovedMinutesSpentLimit()).isNull();
    }

}
