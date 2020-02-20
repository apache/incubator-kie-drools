/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.test.util.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FileUtilsTest {

    private static final String TEST_FILE = "TestFile.txt";
    private static final String NOTEXISTING_FILE = "NotExisting.txt";

    @Test
    public void getFileExisting() {
        final File retrieved = FileUtils.getFile(TEST_FILE);
        assertTrue(retrieved.exists());
        assertEquals(TEST_FILE, retrieved.getName());
    }

    @Test(expected = AssertionError.class)
    public void getFileNotExisting() {
        FileUtils.getFile(NOTEXISTING_FILE);
    }

    @Test
    public void getFileInputStreamExisting() throws IOException {
        final FileInputStream retrieved = FileUtils.getFileInputStream(TEST_FILE);
        assertNotNull(retrieved);
        retrieved.close();
    }

    @Test(expected = AssertionError.class)
    public void getFileInputStreamNotExisting() throws IOException {
        FileUtils.getFileInputStream(NOTEXISTING_FILE);
    }
}