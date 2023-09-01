package org.drools.verifier.core.relations;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OperatorTest {

    @Test
    void testOperators() throws Exception {
        assertThat(Operator.resolve("==")).isEqualTo(Operator.EQUALS);
        assertThat(Operator.resolve(">")).isEqualTo(Operator.GREATER_THAN);
        assertThat(Operator.resolve("<")).isEqualTo(Operator.LESS_THAN);
        assertThat(Operator.resolve(">=")).isEqualTo(Operator.GREATER_OR_EQUAL);
        assertThat(Operator.resolve("<=")).isEqualTo(Operator.LESS_OR_EQUAL);
        assertThat(Operator.resolve("!=")).isEqualTo(Operator.NOT_EQUALS);
        assertThat(Operator.resolve("in")).isEqualTo(Operator.IN);
        assertThat(Operator.resolve("not in")).isEqualTo(Operator.NOT_IN);
        assertThat(Operator.resolve("after")).isEqualTo(Operator.AFTER);
        assertThat(Operator.resolve("before")).isEqualTo(Operator.BEFORE);
        assertThat(Operator.resolve("coincides")).isEqualTo(Operator.COINCIDES);
        assertThat(Operator.resolve("matches")).isEqualTo(Operator.MATCHES);
        assertThat(Operator.resolve("soundslike")).isEqualTo(Operator.SOUNDSLIKE);

    }
}