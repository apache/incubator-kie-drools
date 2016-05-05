/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.drools.compiler.builder.impl;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertThat;

public class KnowledgeBuilderImplTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testCreateDumpDrlGeneratedFileRemovingInvalidCharacters() throws Exception {
        final File dumpDir = temporaryFolder.getRoot();
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
