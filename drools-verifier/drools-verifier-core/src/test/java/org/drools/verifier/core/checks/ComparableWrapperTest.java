package org.drools.verifier.core.checks;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComparableWrapperTest {

    private final ComparableWrapper theNull = new ComparableWrapper(null);
    private final ComparableWrapper one = new ComparableWrapper(1);
    private final ComparableWrapper ten = new ComparableWrapper(10);
    private final ComparableWrapper min = ComparableWrapper.MIN_VALUE;
    private final ComparableWrapper max = ComparableWrapper.MAX_VALUE;

    @Test
    void testSorting() {

        final ComparableWrapper[] unsorted = {one, ten, theNull, max, min};
        final ComparableWrapper[] sorted = {min, theNull, one, ten, max};

        Arrays.sort(unsorted);

        assertThat(sorted).isEqualTo(unsorted);
    }

    @Test
    void compareTo() {
        assertThat(theNull.compareTo(theNull)).isEqualTo(0);
        assertThat(one.compareTo(one)).isEqualTo(0);
        assertThat(ten.compareTo(ten)).isEqualTo(0);
        assertThat(min.compareTo(min)).isEqualTo(0);
        assertThat(max.compareTo(max)).isEqualTo(0);

        assertThat(one.compareTo(theNull) > 0).isTrue();
        assertThat(one.compareTo(ten) < 0).isTrue();
        assertThat(one.compareTo(min) > 0).isTrue();
        assertThat(one.compareTo(max) < 0).isTrue();
        
        assertThat(ten.compareTo(theNull) > 0).isTrue();
        assertThat(ten.compareTo(one) > 0).isTrue();
        assertThat(ten.compareTo(min) > 0).isTrue();
        assertThat(ten.compareTo(max) < 0).isTrue();

        assertThat(min.compareTo(theNull) < 0).isTrue();
        assertThat(min.compareTo(one) < 0).isTrue();
        assertThat(min.compareTo(ten) < 0).isTrue();
        assertThat(min.compareTo(max) < 0).isTrue();

        assertThat(max.compareTo(theNull) > 0).isTrue();
        assertThat(max.compareTo(one) > 0).isTrue();
        assertThat(max.compareTo(ten) > 0).isTrue();
        assertThat(max.compareTo(min) > 0).isTrue();
    }
}