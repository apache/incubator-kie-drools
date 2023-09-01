package org.drools.scenariosimulation.backend.runner.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueWrapperTest {

    @Test
    public void orElse() {
        assertThat(ValueWrapper.of(1).orElse(3)).isEqualTo((Integer) 1);
        assertThat(ValueWrapper.errorWithValidValue(null, null).orElse(3)).isEqualTo(3);
        assertThat(ValueWrapper.of(null).orElse(3)).isNull();
    }

    @Test
    public void orElseGet() {
        assertThat(ValueWrapper.of(1).orElseGet(() -> 3)).isEqualTo((Integer) 1);
        assertThat(ValueWrapper.errorWithValidValue(null, null).orElseGet(() -> 3)).isEqualTo(3);
        assertThat(ValueWrapper.of(null).orElseGet(() -> 3)).isNull();
    }
}