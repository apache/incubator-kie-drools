package org.drools.verifier.core.maps;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KeyDefinitionBuilderTest {

    @Test
    void testNoIdSet() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            KeyDefinition.newKeyDefinition().build();
        });
    }

    @Test
    void testDefaults() throws Exception {
        final KeyDefinition keyDefinition = KeyDefinition.newKeyDefinition().withId("test").build();
        
        assertThat(keyDefinition.isUpdatable()).isFalse();
    }

    @Test
    void testUpdatable() throws Exception {
        final KeyDefinition keyDefinition = KeyDefinition.newKeyDefinition()
                .withId("test")
                .updatable()
                .build();
        
        assertThat(keyDefinition.isUpdatable()).isTrue();
    }

}