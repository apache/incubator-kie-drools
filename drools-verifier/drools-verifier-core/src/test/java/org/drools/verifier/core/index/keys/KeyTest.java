package org.drools.verifier.core.index.keys;

import org.drools.verifier.core.maps.KeyDefinition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyTest {

    @Test
    void testEqual() throws Exception {
        final Key a = new Key(KeyDefinition.newKeyDefinition().withId("id").build(), 2);
        final Key b = new Key(KeyDefinition.newKeyDefinition().withId("id").build(), 2);
        
        assertThat(a.compareTo(b)).isEqualTo(0);
    }
}