package org.drools.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JarUtilsTest {

    @Test
    public void normalizeSpringBootResourceUrlPath() {
        String normalized = JarUtils.normalizeSpringBootResourceUrlPath("BOOT-INF/classes!/org/example/MyClass.class");
        assertThat(normalized).isEqualTo("BOOT-INF/classes/org/example/MyClass.class");
    }
}