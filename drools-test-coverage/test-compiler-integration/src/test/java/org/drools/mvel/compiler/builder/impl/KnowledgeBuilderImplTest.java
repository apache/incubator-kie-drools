package org.drools.mvel.compiler.builder.impl;

import java.io.File;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class KnowledgeBuilderImplTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testCreateDumpDrlGeneratedFileRemovingInvalidCharacters() throws Exception {
        final File dumpDir = temporaryFolder.getRoot();
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "xxx", ".drl")).hasName("xxx.drl");
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x?x?", ".drl")).hasName("x_x_.drl");
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x/x/", ".drl")).hasName("x_x_.drl");
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x\\x\\", ".drl")).hasName("x_x_.drl");
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x*x*", ".drl")).hasName("x_x_.drl");
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "aa.AA01-_", ".drl")).hasName("aa.AA01-_.drl");
    }

}