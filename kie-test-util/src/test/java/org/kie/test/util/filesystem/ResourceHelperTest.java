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
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.test.util.filesystem.ResourceHelper.getResourcesByExtension;
import static org.kie.test.util.filesystem.ResourceHelper.getResourcesFromDirectory;

public class ResourceHelperTest {

    private static final String TEST_FILE = "TestFile.txt";

    @Test
    public void getResourcesByExtensionExisting() {
        final Stream<File> retrieved = getResourcesByExtension("txt");
        commonVerifyStream(retrieved, TEST_FILE);
    }

    @Test
    public void getResourcesByExtensionNotExisting() {
        final Stream<File> retrieved = getResourcesByExtension("arg");
        commonVerifyStream(retrieved, null);
    }

    @Test
    public void getResourcesFromDirectoryExisting() {
        File directory = new File("." + File.separator + "target" + File.separator + "test-classes");
        Pattern pattern = Pattern.compile(".*txt");
        final Stream<File> retrieved = getResourcesFromDirectory(directory, pattern);
        commonVerifyStream(retrieved, TEST_FILE);
    }

    @Test
    public void getResourcesFromDirectoryNotExisting() {
        File directory = new File("." + File.separator + "target" + File.separator + "test-classes");
        Pattern pattern = Pattern.compile(".*arg");
        final Stream<File> retrieved = getResourcesFromDirectory(directory, pattern);
        commonVerifyStream(retrieved, null);
    }

    @Test
    public void getClassPathElements() {
        String[] retrieved = ResourceHelper.getClassPathElements();
        assertNotNull(retrieved);
        assertFalse(retrieved.length == 0);
    }

    @Test
    public void internalGetResourcesExisting() {
        String path = "." + File.separator + "target" + File.separator + "test-classes";
        Pattern pattern = Pattern.compile(".*txt");
        final Stream<File> retrieved = ResourceHelper.internalGetResources(path, pattern);
        commonVerifyStream(retrieved, TEST_FILE);
    }

    @Test
    public void internalGetResourcesNotExisting() {
        String path = "." + File.separator + "target" + File.separator + "test-classes";
        Pattern pattern = Pattern.compile(".*arg");
        final Stream<File> retrieved = ResourceHelper.internalGetResources(path, pattern);
        commonVerifyStream(retrieved, null);
    }

    private void commonVerifyStream(final Stream<File> toVerify, String expectedFile) {
        assertNotNull(toVerify);
        final List<File> retrieved = toVerify.collect(Collectors.toList());
        if (expectedFile != null) {
            assertEquals(1, retrieved.size());
            assertTrue(retrieved.get(0).exists());
            assertEquals(expectedFile, retrieved.get(0).getName());
        } else {
            assertTrue(retrieved.isEmpty());
        }
    }
}