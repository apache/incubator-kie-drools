package org.optaplanner.core.config.heuristic.selector.entity.pillar;

import java.util.Comparator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;

public class SubPillarConfigPolicyTest {

    @Test
    public void withoutSubpillars() {
        SubPillarConfigPolicy policy = SubPillarConfigPolicy.withoutSubpillars();
        assertSoftly(softly -> {
            softly.assertThat(policy.isSubPillarEnabled()).isFalse();
            softly.assertThat(policy.getEntityComparator()).isNull();
            softly.assertThat(policy.getMinimumSubPillarSize()).isLessThan(1);
            softly.assertThat(policy.getMaximumSubPillarSize()).isLessThan(1);
        });
    }

    @Test
    public void withLimitedSubpillars() {
        SubPillarConfigPolicy policy = SubPillarConfigPolicy.withSubpillars(2, 10);
        assertSoftly(softly -> {
            softly.assertThat(policy.isSubPillarEnabled()).isTrue();
            softly.assertThat(policy.getEntityComparator()).isNull();
            softly.assertThat(policy.getMinimumSubPillarSize()).isEqualTo(2);
            softly.assertThat(policy.getMaximumSubPillarSize()).isEqualTo(10);
        });
    }

    @Test
    public void unlimitedSequential() {
        SubPillarConfigPolicy policy = SubPillarConfigPolicy.sequentialUnlimited(mock(Comparator.class));
        assertSoftly(softly -> {
            softly.assertThat(policy.isSubPillarEnabled()).isTrue();
            softly.assertThat(policy.getEntityComparator()).isNotNull();
            softly.assertThat(policy.getMinimumSubPillarSize()).isEqualTo(1);
            softly.assertThat(policy.getMaximumSubPillarSize()).isEqualTo(Integer.MAX_VALUE);
        });
    }

    @Test
    public void sequential() {
        SubPillarConfigPolicy policy = SubPillarConfigPolicy.sequential(3, 5, mock(Comparator.class));
        assertSoftly(softly -> {
            softly.assertThat(policy.isSubPillarEnabled()).isTrue();
            softly.assertThat(policy.getEntityComparator()).isNotNull();
            softly.assertThat(policy.getMinimumSubPillarSize()).isEqualTo(3);
            softly.assertThat(policy.getMaximumSubPillarSize()).isEqualTo(5);
        });
    }

    @Test
    public void validation() {
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

}
