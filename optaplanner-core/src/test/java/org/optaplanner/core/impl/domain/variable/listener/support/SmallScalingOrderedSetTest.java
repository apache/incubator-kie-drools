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

package org.optaplanner.core.impl.domain.variable.listener.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class SmallScalingOrderedSetTest {

    @Test
    public void addRemoveAroundThreshold() {
        SmallScalingOrderedSet<String> set = new SmallScalingOrderedSet<>();
        assertThat(set.add("s1")).isTrue();
        assertThat(set.add("s1")).isFalse();
        assertThat(set.add("s2")).isTrue();
        assertThat(set.add("s1")).isFalse();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.remove("s2")).isTrue();
        assertThat(set.remove("s2")).isFalse();
        assertThat(set.add("s2")).isTrue();
        assertThat(set.size()).isEqualTo(2);
        assertThat(set.contains("s1")).isTrue();
        assertThat(set.contains("s2")).isTrue();

        for (int i = 0; i < SmallScalingOrderedSet.LIST_SIZE_THRESHOLD - 3; i++) {
            set.add("filler " + i);
        }
        assertThat(set.add("s2")).isFalse();
        assertThat(set.add("s3")).isTrue();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.size()).isEqualTo(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD);
        assertThat(set.add("s4")).isTrue();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.add("s3")).isFalse();
        assertThat(set.add("s4")).isFalse();
        assertThat(set.size()).isEqualTo(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD + 1);
        assertThat(set.remove("s4")).isTrue();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.add("s3")).isFalse();
        assertThat(set.size()).isEqualTo(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD);
        assertThat(set.add("s5")).isTrue();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.add("s3")).isFalse();
        assertThat(set.size()).isEqualTo(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD + 1);
        assertThat(set.add("s6")).isTrue();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.add("s3")).isFalse();
        assertThat(set.size()).isEqualTo(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD + 2);
        assertThat(set.contains("s1")).isTrue();
        assertThat(set.contains("s2")).isTrue();
        assertThat(set.contains("s3")).isTrue();
        assertThat(set.contains("s4")).isFalse();
        assertThat(set.contains("s5")).isTrue();
        assertThat(set.contains("s6")).isTrue();
    }

    @Test
    public void addAllAroundThreshold() {
        SmallScalingOrderedSet<String> set = new SmallScalingOrderedSet<>();
        assertThat(set.addAll(Arrays.asList("s1", "s2", "s3"))).isTrue();
        assertThat(set.size()).isEqualTo(3);
        assertThat(set.addAll(Arrays.asList("s1", "s3", "s4", "s5"))).isTrue();
        assertThat(set.addAll(Arrays.asList("s1", "s2", "s4"))).isFalse();
        assertThat(set.size()).isEqualTo(5);
        assertThat(set.contains("s1")).isTrue();
        assertThat(set.contains("s2")).isTrue();
        assertThat(set.contains("s3")).isTrue();
        assertThat(set.contains("s4")).isTrue();
        assertThat(set.contains("s5")).isTrue();

        for (int i = 0; i < SmallScalingOrderedSet.LIST_SIZE_THRESHOLD - 7; i++) {
            set.add("filler " + i);
        }
        assertThat(set.size()).isEqualTo(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD - 2);
        assertThat(set.addAll(Arrays.asList("s6", "s7", "s2", "s3", "s8", "s9"))).isTrue();
        assertThat(set.size()).isEqualTo(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD + 2);
        assertThat(set.remove("s1")).isTrue();
        assertThat(set.remove("s5")).isTrue();
        assertThat(set.size()).isEqualTo(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD);
        assertThat(set.addAll(Arrays.asList("s1", "s2", "s10"))).isTrue();
        assertThat(set.size()).isEqualTo(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD + 2);
        assertThat(set.contains("s1")).isTrue();
        assertThat(set.contains("s2")).isTrue();
        assertThat(set.contains("s3")).isTrue();
        assertThat(set.contains("s4")).isTrue();
        assertThat(set.contains("s5")).isFalse();
        assertThat(set.contains("s6")).isTrue();
        assertThat(set.contains("s7")).isTrue();
        assertThat(set.contains("s8")).isTrue();
        assertThat(set.contains("s9")).isTrue();
        assertThat(set.contains("s10")).isTrue();
    }

}
