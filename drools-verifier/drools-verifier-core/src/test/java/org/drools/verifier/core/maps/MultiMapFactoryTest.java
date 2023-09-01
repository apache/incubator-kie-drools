package org.drools.verifier.core.maps;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiMapFactoryTest {

    @Test
    void testDefault() throws Exception {
        assertThat(MultiMapFactory.make() instanceof ChangeHandledMultiMap).isFalse();
    }

    @Test
    void normal() throws Exception {
        assertThat(MultiMapFactory.make(false) instanceof ChangeHandledMultiMap).isFalse();
    }

    @Test
    void changeHandled() throws Exception {
        assertThat(MultiMapFactory.make(true) instanceof ChangeHandledMultiMap).isTrue();
    }
}