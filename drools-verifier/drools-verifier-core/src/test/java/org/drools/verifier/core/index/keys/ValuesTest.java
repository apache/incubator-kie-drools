package org.drools.verifier.core.index.keys;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValuesTest {


    @Test
    void testNull() throws Exception {
        final Values<Comparable> values = new Values<>();
        values.add(null);

        assertThat(values).isNotEmpty();
        assertThat(values.iterator().next()).isEqualTo(null);
    }

    @Test
    void testChanges() throws Exception {
        final Values a = new Values();
        final Values b = new Values();

        assertThat(a.isThereChanges(b)).isFalse();
    }
}