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

package org.drools.compiler.kie.builder.impl;

import java.util.Collection;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.builder.KieFileSystem;

import static org.junit.Assert.*;

public class KieFileSystemImplTest {

    KieFileSystemImpl kieFileSystem;

    @Before
    public void setup() {
        kieFileSystem = new KieFileSystemImpl();
    }

    @Test
    public void testClone() throws Exception {
        KieFileSystem clonedKieFileSystem = kieFileSystem.clone();
        MemoryFileSystem clonedMfs = ( (KieFileSystemImpl) clonedKieFileSystem ).getMfs();
        Collection<String> clonedFileNames = clonedMfs.getFileNames();

        assertTrue( kieFileSystem != clonedKieFileSystem );
        assertTrue( kieFileSystem.getMfs() != clonedMfs );
        assertEquals( kieFileSystem.getMfs().getFileNames(), clonedFileNames );
    }
}
