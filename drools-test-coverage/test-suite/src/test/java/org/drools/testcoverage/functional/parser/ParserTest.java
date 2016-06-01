/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.functional.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests ability to parse resources. To add a test to this class simply add a
 * drl file into test-resource/parser-tests/ to appropriate directory.
 * 
 * Directories: - to test DRL files use drl directory - to test DSL use dsl
 * directory, both DSL and DSLR files must have the same name to be matched
 * together - for files that should be part of smoke test use smoke directory
 * (these does not necessarily have to be DRL) - in case of any other file type
 * new folder can be added
 * 
 */
@RunWith(Parameterized.class)
public abstract class ParserTest {
    private static final String PARSER_RESOURCES_DIR_PATH = "src/test/resources/org/drools/testcoverage/functional/parser";
    private static final File PARSER_RESOURCES_DIR = new File(PARSER_RESOURCES_DIR_PATH);

    protected final File file;

    public ParserTest(File file) {
        this.file = file;
    }

    protected static List<File> getFiles(final String directory) {
        return getFiles(directory, null);
    }

    protected static List<File> getFiles(final String directory, final String extension) {
        final List<File> result = new ArrayList<File>();

        final File[] files = new File(PARSER_RESOURCES_DIR, directory).listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.getName().startsWith(".") && (extension == null || f.getName().endsWith("." + extension))) {
                    result.add(f);
                }
            }
        }

        return result;
    }

    protected static Collection<Object[]> getTestParamsFromFiles(Collection<File> files) {
        final Set<Object[]> set = new HashSet<>();

        for (File file : files) {
            set.add(new Object[] { file });
        }

        return set;
    }
}
