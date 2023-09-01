package org.drools.mvel.compiler.compiler.io.memory;

import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryFileSystemTest {

    private MemoryFileSystem memoryFileSystem;

    @Before
    public void setup() {
        memoryFileSystem = new MemoryFileSystem();
    }

    @Test
    public void testGetEnglishFileName() throws Exception {
        final File file = memoryFileSystem.getFile( "path/path/File.java" );

        assertThat(file.getName()).isEqualTo("File.java");
    }

    @Test
    public void testGetJapaneseFileName() throws Exception {
        final File file = memoryFileSystem.getFile( "path/path/%E3%81%82%E3%81%84%E3%81%86%E3%81%88%E3%81%8A.java" );

        assertThat(file.getName()).isEqualTo("%E3%81%82%E3%81%84%E3%81%86%E3%81%88%E3%81%8A.java");
    }
}
