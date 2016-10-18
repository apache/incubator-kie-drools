/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.compiler.io.memory;

import org.drools.compiler.compiler.io.File;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MemoryFileSystemTest {

    private MemoryFileSystem memoryFileSystem;

    @Before
    public void setup() {
        memoryFileSystem = new MemoryFileSystem();
    }

    @Test
    public void testGetEnglishFileName() throws Exception {
        final File file = memoryFileSystem.getFile( "path/path/File.java" );

        assertEquals( "File.java", file.getName() );
    }

    @Test
    public void testGetJapaneseFileName() throws Exception {
        final File file = memoryFileSystem.getFile( "path/path/%E3%81%82%E3%81%84%E3%81%86%E3%81%88%E3%81%8A.java" );

        assertEquals( "あいうえお.java", file.getName() );
    }
}
