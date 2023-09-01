package org.drools.mvel.compiler.kie.builder.impl;

import java.util.Collection;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.builder.KieFileSystem;
import org.drools.util.PortablePath;

import static org.assertj.core.api.Assertions.assertThat;

public class KieFileSystemImplTest {

    KieFileSystemImpl kieFileSystem;

    @Before
    public void setup() {
        kieFileSystem = new KieFileSystemImpl();
    }

    @Test
    public void testClone() {
        KieFileSystem clonedKieFileSystem = kieFileSystem.clone();
        MemoryFileSystem clonedMfs = ( (KieFileSystemImpl) clonedKieFileSystem ).getMfs();
        Collection<PortablePath> clonedFileNames = clonedMfs.getFilePaths();

        assertThat(kieFileSystem != clonedKieFileSystem).isTrue();
        assertThat(kieFileSystem.getMfs() != clonedMfs).isTrue();
        assertThat(clonedFileNames).isEqualTo(kieFileSystem.getMfs().getFilePaths());
    }
}
