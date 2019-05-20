package org.drools.compiler.builder.impl;

import java.io.File;
import java.io.IOException;

import org.assertj.core.util.Files;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class KnowledgeBuilderImplTest {

    public File temporaryFolder = Files.temporaryFolder();

    @Test
    public void testCreateDumpDrlGeneratedFileRemovingInvalidCharacters() throws Exception {
        final File dumpDir = temporaryFolder;
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "xxx", ".drl"), fileEndsWith(File.separator + "xxx.drl"));
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x?x?", ".drl"), fileEndsWith(File.separator + "x_x_.drl"));
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x/x/", ".drl"), fileEndsWith(File.separator + "x_x_.drl"));
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x\\x\\", ".drl"), fileEndsWith(File.separator + "x_x_.drl"));
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x*x*", ".drl"), fileEndsWith(File.separator + "x_x_.drl"));
        assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "aa.AA01-_", ".drl"), fileEndsWith(File.separator + "aa.AA01-_.drl"));
    }

    private static FileEndsWithMatcher fileEndsWith(String endsWithString) {
        return new FileEndsWithMatcher(endsWithString);
    }

    private static class FileEndsWithMatcher extends BaseMatcher<File> {

        private final String endsWithString;

        private FileEndsWithMatcher(String endsWithString) {
            this.endsWithString = endsWithString;
        }

        @Override
        public boolean matches(Object item) {
            if (item instanceof File) {
                try {
                    return ((File) item).getCanonicalPath().endsWith(endsWithString);
                } catch (IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendValue(endsWithString);
        }
    }
}
