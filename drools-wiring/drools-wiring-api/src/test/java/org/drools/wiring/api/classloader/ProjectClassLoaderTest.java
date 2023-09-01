package org.drools.wiring.api.classloader;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectClassLoaderTest {

    @Test
    public void testNonExistingClassCacheIsEnabled() {
        assertThat(ProjectClassLoader.CACHE_NON_EXISTING_CLASSES).isTrue();
    }
}
