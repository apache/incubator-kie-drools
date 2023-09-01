package org.kie.internal.utils;

import org.junit.Test;

public class KieMetaTest {

    @Test
    public void isProductized() {
        // Check if the class static constructor behaves correctly.
        // Both return types are valid (otherwise we break productized tests)
        KieMeta.isProductized();
    }

}
