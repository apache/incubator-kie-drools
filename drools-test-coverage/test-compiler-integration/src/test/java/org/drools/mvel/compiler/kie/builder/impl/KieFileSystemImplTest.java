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
package org.drools.mvel.compiler.kie.builder.impl;

import java.util.Collection;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.builder.KieFileSystem;
import org.drools.util.PortablePath;

import static org.assertj.core.api.Assertions.assertThat;

public class KieFileSystemImplTest {

    KieFileSystemImpl kieFileSystem;

    @Before
    public void setup() {
        kieFileSystem = new KieFileSystemImpl();
    }

    @Test
    public void testClone() {
        KieFileSystem clonedKieFileSystem = kieFileSystem.clone();
        MemoryFileSystem clonedMfs = ( (KieFileSystemImpl) clonedKieFileSystem ).getMfs();
        Collection<PortablePath> clonedFileNames = clonedMfs.getFilePaths();

        assertThat(kieFileSystem != clonedKieFileSystem).isTrue();
        assertThat(kieFileSystem.getMfs() != clonedMfs).isTrue();
        assertThat(clonedFileNames).isEqualTo(kieFileSystem.getMfs().getFilePaths());
    }
}
