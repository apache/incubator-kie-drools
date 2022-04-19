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

package org.drools.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class KiePathTest {

    protected final boolean isWindowsSeparator;
    protected final String fileSeparator;

    public KiePathTest( boolean isWindowsSeparator ) {
        this.isWindowsSeparator = isWindowsSeparator;
        this.fileSeparator = isWindowsSeparator ? "\\" : "/";
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[] { true, false };
    }

    @Test
    public void testAsString() throws Exception {
        assertEquals("src", PortablePath.of("src", isWindowsSeparator).asString());
        assertEquals("src/test", PortablePath.of("src" + fileSeparator + "test", isWindowsSeparator).asString());
        assertEquals("src/test", PortablePath.of("src" + fileSeparator + "test", isWindowsSeparator).asString());
        assertEquals("src/test/folder", PortablePath.of("src" + fileSeparator + "test" + fileSeparator + "folder", isWindowsSeparator).asString());
    }

    @Test
    public void testParent() throws Exception {
        assertEquals("src/test", PortablePath.of("src" + fileSeparator + "test" + fileSeparator + "folder", isWindowsSeparator).getParent().asString());
        assertEquals("src", PortablePath.of("src" + fileSeparator + "test" + fileSeparator + "folder", isWindowsSeparator).getParent().getParent().asString());
        assertEquals("", PortablePath.of("src" + fileSeparator + "test" + fileSeparator + "folder", isWindowsSeparator).getParent().getParent().getParent().asString());
    }

    @Test
    public void testResolve() throws Exception {
        assertEquals("src/test/folder", PortablePath.of("src" + fileSeparator + "test", isWindowsSeparator).resolve("folder").asString());
        assertEquals("src/test/folder/subfolder", PortablePath.of("src" + fileSeparator + "test", isWindowsSeparator).resolve(PortablePath.of("folder" + fileSeparator + "subfolder", isWindowsSeparator)).asString());
    }

    @Test
    public void testFileName() throws Exception {
        assertEquals("folder", PortablePath.of("src" + fileSeparator + "test" + fileSeparator + "folder", isWindowsSeparator).getFileName());
    }
}