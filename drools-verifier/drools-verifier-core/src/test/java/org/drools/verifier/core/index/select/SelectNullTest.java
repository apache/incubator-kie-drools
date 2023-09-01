package org.drools.verifier.core.index.select;

import org.drools.verifier.core.maps.MultiMapFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SelectNullTest {

    @Test
    void testAll() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            new Select<>(MultiMapFactory.make(),
                    null);
        });
    }
}