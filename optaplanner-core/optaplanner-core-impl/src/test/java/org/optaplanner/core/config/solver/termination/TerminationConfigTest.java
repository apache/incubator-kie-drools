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

    /**
     * A child config without any time spent limit should inherit the limit
     * from a parent config with a limit.
     */
    @Test
    void childWithoutTimeSpentLimitShouldInheritLimitFromParent() {
        // Given a child config without a time spent limit,
        TerminationConfig child = new TerminationConfig();
        // and a parent with a seconds spent limit,
        TerminationConfig parent = new TerminationConfig()
                .withSecondsSpentLimit(1L);
        // when the child inherits from the parent,
        child.inherit(parent);
        // then the child should have the same limit.
        assertThat(child.getSecondsSpentLimit()).isEqualTo(1L);
    }

    /**
     * A child config with a seconds spent limit should ignore a seconds
     * spent limit when inheriting from another config.
     * In this case, there is a direct collision between a parameter
     * in the child and the same parameter in the parent.
     */
    @Test
    void childWithSecondsSpentLimitShouldNotInheritSecondsSpentFromParent() {
        // Given a child config with a seconds spent limit
        TerminationConfig child = new TerminationConfig()
                .withSecondsSpentLimit(2L);
        // and a parent with a seconds spent limit
        TerminationConfig parent = new TerminationConfig()
                .withSecondsSpentLimit(1L);
        // when the child inherits from the parent
        child.inherit(parent);
        // then the child should keep its limit.
        assertThat(child.getSecondsSpentLimit()).isEqualTo(2L);
    }

    /**
     * A child config with a time spent limit should ignore all time spent
     * limits of a parent config.
     * In this case, there is an indirect collision between a parameter
     * in the child and a semantically equivalent parameter in the parent.
     */
    @Test
    void childWithTimeSpentLimitShouldNotInheritTimeSpentLimitFromParent() {
        // Given a child config with a seconds spent limit
        TerminationConfig child = new TerminationConfig()
                .withSecondsSpentLimit(2L);
        // and a parent with a minutes spent limit
        TerminationConfig parent = new TerminationConfig()
                .withMinutesSpentLimit(1L);
        // when the child inherits from the parent
        child.inherit(parent);
        // then the child should keep its limit.
        assertThat(child.getSecondsSpentLimit()).isEqualTo(2L);
        assertThat(child.getMinutesSpentLimit()).isNull();
    }

}
