package org.kie.test.util;

import static org.kie.test.util.StaticMethodTestHelper.*;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StaticMethodTestHelperTest {

    @Ignore("Not working with \"quarkus-3-SNAPSHOT\" maven version")
    @Test
    public void versionIsLessThanProjectVersion() {
        double version = 6.2d;
        assertThat(projectVersionIsLessThan(version)).isFalse();

        assertThat(isLessThanProjectVersion("7.0.0.Beta1", version)).isFalse();
        assertThat(isLessThanProjectVersion("7.0.0.20160123-098765", version)).isFalse();
        assertThat(isLessThanProjectVersion("7.0.0-SNAPSHOT", version)).isFalse();
    }
}
