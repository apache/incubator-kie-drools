/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.compiler.io.memory;

import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryFileSystemTest {

    private MemoryFileSystem memoryFileSystem;

    @Before
    public void setup() {
        memoryFileSystem = new MemoryFileSystem();
    }

    @Test
    public void testGetEnglishFileName() throws Exception {
        final File file = memoryFileSystem.getFile( "path/path/File.java" );

        assertThat(file.getName()).isEqualTo("File.java");
    }

    @Test
    public void testGetJapaneseFileName() throws Exception {
        final File file = memoryFileSystem.getFile( "path/path/%E3%81%82%E3%81%84%E3%81%86%E3%81%88%E3%81%8A.java" );

        assertThat(file.getName()).isEqualTo("%E3%81%82%E3%81%84%E3%81%86%E3%81%88%E3%81%8A.java");
    }
}
