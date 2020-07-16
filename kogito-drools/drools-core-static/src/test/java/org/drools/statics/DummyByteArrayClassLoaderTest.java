package org.drools.statics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DummyByteArrayClassLoaderTest {

    @Test
    public void defineClassShouldThrow() {
        StaticComponentsSupplier.DummyByteArrayClassLoader cl =
                new StaticComponentsSupplier.DummyByteArrayClassLoader();
        assertThatThrownBy(() -> cl.defineClass("test", null, null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("drools-core-static");
    }
}