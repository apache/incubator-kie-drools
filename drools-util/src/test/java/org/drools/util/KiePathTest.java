package org.drools.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class KiePathTest {

    protected final boolean isWindowsSeparator;
    protected final String fileSeparator;

    public KiePathTest( boolean isWindowsSeparator ) {
        this.isWindowsSeparator = isWindowsSeparator;
        this.fileSeparator = isWindowsSeparator ? "\\" : "/";
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[] { true, false };
    }

    @Test
    public void testAsString() throws Exception {
        assertThat(PortablePath.of("src", isWindowsSeparator).asString()).isEqualTo("src");
        assertThat(PortablePath.of("src" + fileSeparator + "test", isWindowsSeparator).asString()).isEqualTo("src/test");
        assertThat(PortablePath.of("src" + fileSeparator + "test", isWindowsSeparator).asString()).isEqualTo("src/test");
        assertThat(PortablePath.of("src" + fileSeparator + "test" + fileSeparator + "folder", isWindowsSeparator).asString()).isEqualTo("src/test/folder");
    }

    @Test
    public void testParent() throws Exception {
        assertThat(PortablePath.of("src" + fileSeparator + "test" + fileSeparator + "folder", isWindowsSeparator).getParent().asString()).isEqualTo("src/test");
        assertThat(PortablePath.of("src" + fileSeparator + "test" + fileSeparator + "folder", isWindowsSeparator).getParent().getParent().asString()).isEqualTo("src");
        assertThat(PortablePath.of("src" + fileSeparator + "test" + fileSeparator + "folder", isWindowsSeparator).getParent().getParent().getParent().asString()).isEqualTo("");
    }

    @Test
    public void testResolve() throws Exception {
        assertThat(PortablePath.of("src" + fileSeparator + "test", isWindowsSeparator).resolve("folder").asString()).isEqualTo("src/test/folder");
        assertThat(PortablePath.of("src" + fileSeparator + "test", isWindowsSeparator).resolve(PortablePath.of("folder" + fileSeparator + "subfolder", isWindowsSeparator)).asString()).isEqualTo("src/test/folder/subfolder");
    }

    @Test
    public void testFileName() throws Exception {
        assertThat(PortablePath.of("src" + fileSeparator + "test" + fileSeparator + "folder", isWindowsSeparator).getFileName()).isEqualTo("folder");
    }
}