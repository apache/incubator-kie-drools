package org.drools.verifier.core.index.keys;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueNullTest {

    @Test
    void testNull01() throws Exception {
        assertThat(new Value(null).compareTo(new Value(null))).isEqualTo(0);

    }

    @Test
    void testNull02() throws Exception {
        assertThat(new Value(-1)).isLessThan(new Value(0));
    }

    @Test
    void testNull03() throws Exception {
        assertThat(new Value(0)).isGreaterThan(new Value(null));

    }
}