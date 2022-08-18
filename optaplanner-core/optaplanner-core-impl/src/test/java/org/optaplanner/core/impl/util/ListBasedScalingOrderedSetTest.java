package org.optaplanner.core.impl.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.util.ListBasedScalingOrderedSet.LIST_SIZE_THRESHOLD;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ListBasedScalingOrderedSetTest {

    @Test
    void addRemoveAroundThreshold() {
        Set<String> set = new ListBasedScalingOrderedSet<>();
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

        for (int i = 0; i < LIST_SIZE_THRESHOLD - 3; i++) {
            set.add("filler " + i);
        }
        assertThat(set.add("s2")).isFalse();
        assertThat(set.add("s3")).isTrue();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.size()).isEqualTo(LIST_SIZE_THRESHOLD);
        assertThat(set.add("s4")).isTrue();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.add("s3")).isFalse();
        assertThat(set.add("s4")).isFalse();
        assertThat(set.size()).isEqualTo(LIST_SIZE_THRESHOLD + 1);
        assertThat(set.remove("s4")).isTrue();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.add("s3")).isFalse();
        assertThat(set.size()).isEqualTo(LIST_SIZE_THRESHOLD);
        assertThat(set.add("s5")).isTrue();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.add("s3")).isFalse();
        assertThat(set.size()).isEqualTo(LIST_SIZE_THRESHOLD + 1);
        assertThat(set.add("s6")).isTrue();
        assertThat(set.add("s2")).isFalse();
        assertThat(set.add("s3")).isFalse();
        assertThat(set.size()).isEqualTo(LIST_SIZE_THRESHOLD + 2);
        assertThat(set.contains("s1")).isTrue();
        assertThat(set.contains("s2")).isTrue();
        assertThat(set.contains("s3")).isTrue();
        assertThat(set.contains("s4")).isFalse();
        assertThat(set.contains("s5")).isTrue();
        assertThat(set.contains("s6")).isTrue();
    }

    @Test
    void addAllAroundThreshold() {
        Set<String> set = new ListBasedScalingOrderedSet<>();
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

        for (int i = 0; i < LIST_SIZE_THRESHOLD - 7; i++) {
            set.add("filler " + i);
        }
        assertThat(set.size()).isEqualTo(LIST_SIZE_THRESHOLD - 2);
        assertThat(set.addAll(Arrays.asList("s6", "s7", "s2", "s3", "s8", "s9"))).isTrue();
        assertThat(set.size()).isEqualTo(LIST_SIZE_THRESHOLD + 2);
        assertThat(set.remove("s1")).isTrue();
        assertThat(set.remove("s5")).isTrue();
        assertThat(set.size()).isEqualTo(LIST_SIZE_THRESHOLD);
        assertThat(set.addAll(Arrays.asList("s1", "s2", "s10"))).isTrue();
        assertThat(set.size()).isEqualTo(LIST_SIZE_THRESHOLD + 2);
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
