/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.memorycompiler.resources;

import java.io.File;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KiePathTest {

    @Test
    public void testAsString() throws Exception {
        assertEquals("src", KiePath.of("src").asString());
        assertEquals("src/test", KiePath.of("src" + File.separator + "test").asString());
        assertEquals("src/test", KiePath.of("src" + File.separator + "test").asString());
        assertEquals("src/test/folder", KiePath.of("src" + File.separator + "test" + File.separator + "folder").asString());
    }

    @Test
    public void testParent() throws Exception {
        assertEquals("src/test", KiePath.of("src" + File.separator + "test" + File.separator + "folder").getParent().asString());
        assertEquals("src", KiePath.of("src" + File.separator + "test" + File.separator + "folder").getParent().getParent().asString());
        assertEquals("", KiePath.of("src" + File.separator + "test" + File.separator + "folder").getParent().getParent().getParent().asString());
    }

    @Test
    public void testResolve() throws Exception {
        assertEquals("src/test/folder", KiePath.of("src" + File.separator + "test").resolve("folder").asString());
        assertEquals("src/test/folder/subfolder", KiePath.of("src" + File.separator + "test").resolve("folder" + File.separator + "subfolder").asString());
    }

    @Test
    public void testFileName() throws Exception {
        assertEquals("folder", KiePath.of("src" + File.separator + "test" + File.separator + "folder").getFileName());
    }
}