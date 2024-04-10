package org.drools.model.operators;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MatchesOperatorTest {
    @Test
    public void testMatchesOperator() {
        MatchesOperator instance = MatchesOperator.INSTANCE;
        // Regular expression is second parameter
        assertThat(instance.eval("a","a")).isTrue();
        assertThat(instance.eval("a","b")).isFalse();
        assertThat(instance.eval("a","a")).isTrue();
        assertThat(instance.eval("b","b")).isTrue();

        // s1 maybe null
        assertThat(instance.eval(null,"anything")).isFalse();

    }

}
