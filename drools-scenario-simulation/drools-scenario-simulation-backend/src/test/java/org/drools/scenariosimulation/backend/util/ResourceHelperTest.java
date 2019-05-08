/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.backend.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import static org.drools.scenariosimulation.backend.util.ResourceHelper.getClassPathElements;
import static org.drools.scenariosimulation.backend.util.ResourceHelper.getResourcesByExtension;
import static org.drools.scenariosimulation.backend.util.ResourceHelper.getResourcesFromDirectory;
import static org.drools.scenariosimulation.backend.util.ResourceHelper.internalGetResources;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResourceHelperTest {

    @Test
    public void getClassPathElementsTest() {
        List<String> classPathElements = Arrays.asList(getClassPathElements());
        List<String> notJar = classPathElements.stream().filter(elem -> !elem.contains(".jar")).collect(Collectors.toList());
        assertTrue(notJar.stream().anyMatch(elem -> elem.contains("test-classes")));
    }

    @Test
    public void getResourcesByExtensionTest() {
        Stream<String> txtResources = getResourcesByExtension("txt");
        List<String> resources = txtResources.collect(Collectors.toList());
        assertEquals(1, resources.size());
        assertTrue(resources.stream().anyMatch(elem -> elem.endsWith("testFile.txt")));
    }

    @Test
    public void getResourcesFromDirectoryTest() {
        List<String> classPathElements = Arrays.asList(getClassPathElements());
        Optional<String> testFolder = classPathElements.stream().filter(elem -> elem.contains("test-classes")).findFirst();
        assertTrue(testFolder.isPresent());
        File dir = new File(testFolder.get());
        List<String> filesFound = getResourcesFromDirectory(dir, Pattern.compile(".*testFile.txt"))
                .collect(Collectors.toList());
        assertEquals(1, filesFound.size());

        assertEquals(0, getResourcesFromDirectory(null, null).count());
        assertEquals(0, getResourcesFromDirectory(dir, Pattern.compile("noMatch")).count());
    }

    @Test
    public void internalGetResourcesTest() {
        List<String> classPathElements = Arrays.asList(getClassPathElements());
        Optional<String> testFolder = classPathElements.stream().filter(elem -> elem.contains("test-classes")).findFirst();
        assertTrue(testFolder.isPresent());
        File dir = new File(testFolder.get());
        List<String> filesFound = internalGetResources(testFolder.get(), Pattern.compile(".*\\.txt$")).collect(Collectors.toList());
        assertEquals(1, filesFound.size());

        assertEquals(0, internalGetResources(filesFound.get(0), Pattern.compile(".*\\.txt$")).count());
    }
}