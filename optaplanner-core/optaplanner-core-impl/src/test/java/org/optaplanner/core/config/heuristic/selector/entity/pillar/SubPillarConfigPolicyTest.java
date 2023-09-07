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

package org.optaplanner.core.config.heuristic.selector.entity.pillar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

class SubPillarConfigPolicyTest {

    @Test
    void withoutSubpillars() {
        SubPillarConfigPolicy policy = SubPillarConfigPolicy.withoutSubpillars();
        assertSoftly(softly -> {
            softly.assertThat(policy.isSubPillarEnabled()).isFalse();
            softly.assertThat(policy.getEntityComparator()).isNull();
            softly.assertThat(policy.getMinimumSubPillarSize()).isLessThan(1);
            softly.assertThat(policy.getMaximumSubPillarSize()).isLessThan(1);
        });
    }

    @Test
    void withLimitedSubpillars() {
        SubPillarConfigPolicy policy = SubPillarConfigPolicy.withSubpillars(2, 10);
        assertSoftly(softly -> {
            softly.assertThat(policy.isSubPillarEnabled()).isTrue();
            softly.assertThat(policy.getEntityComparator()).isNull();
            softly.assertThat(policy.getMinimumSubPillarSize()).isEqualTo(2);
            softly.assertThat(policy.getMaximumSubPillarSize()).isEqualTo(10);
        });
    }

    @Test
    void unlimitedSequential() {
        SubPillarConfigPolicy policy = SubPillarConfigPolicy.sequentialUnlimited(mock(Comparator.class));
        assertSoftly(softly -> {
            softly.assertThat(policy.isSubPillarEnabled()).isTrue();
            softly.assertThat(policy.getEntityComparator()).isNotNull();
            softly.assertThat(policy.getMinimumSubPillarSize()).isEqualTo(1);
            softly.assertThat(policy.getMaximumSubPillarSize()).isEqualTo(Integer.MAX_VALUE);
        });
    }

    @Test
    void sequential() {
        SubPillarConfigPolicy policy = SubPillarConfigPolicy.sequential(3, 5, mock(Comparator.class));
        assertSoftly(softly -> {
            softly.assertThat(policy.isSubPillarEnabled()).isTrue();
            softly.assertThat(policy.getEntityComparator()).isNotNull();
            softly.assertThat(policy.getMinimumSubPillarSize()).isEqualTo(3);
            softly.assertThat(policy.getMaximumSubPillarSize()).isEqualTo(5);
        });
    }

    @Test
    void validation() {
        assertThatThrownBy(() -> SubPillarConfigPolicy.withSubpillars(0, 1)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SubPillarConfigPolicy.withSubpillars(1, 0)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SubPillarConfigPolicy.withSubpillars(2, 1)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SubPillarConfigPolicy.sequential(0, 1, mock(Comparator.class)))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SubPillarConfigPolicy.sequential(1, 0, mock(Comparator.class)))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SubPillarConfigPolicy.sequential(2, 1, mock(Comparator.class)))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SubPillarConfigPolicy.sequential(1, 2, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void equalitySizes() {
        SubPillarConfigPolicy policy = SubPillarConfigPolicy.withSubpillars(1, 2);
        assertThat(policy).isEqualTo(policy);
        SubPillarConfigPolicy policy2 = SubPillarConfigPolicy.withSubpillars(1, 2);
        assertThat(policy2).isEqualTo(policy);
        SubPillarConfigPolicy policy3 = SubPillarConfigPolicy.withSubpillars(1, 3);
        assertThat(policy3).isNotEqualTo(policy2);
        SubPillarConfigPolicy policy4 = SubPillarConfigPolicy.withSubpillars(2, 3);
        assertThat(policy4).isNotEqualTo(policy3);
    }

    @Test
    void equalityComparator() {
        Comparator<Object> comparator = (a, b) -> 0;
        SubPillarConfigPolicy policy = SubPillarConfigPolicy.sequentialUnlimited(comparator);
        assertThat(policy).isEqualTo(policy);
        SubPillarConfigPolicy policy2 = SubPillarConfigPolicy.sequentialUnlimited(comparator);
        assertThat(policy2).isEqualTo(policy);
        SubPillarConfigPolicy policy3 = SubPillarConfigPolicy.sequentialUnlimited((a, b) -> 0);
        assertThat(policy3).isNotEqualTo(policy);
    }

}
