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

import org.apache.commons.io.IOUtils;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.compiler.io.memory.MemoryFolder;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.wiring.api.ResourceProvider;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.drools.util.PortablePath;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryKieModuleResourceProviderTest {

    @Mock
    ReleaseId releaseId;
    @Mock
    KieModuleModel kieModuleModel;

    @Test
    public void testGetResourceForEmptyFolder() throws Exception {
        MemoryFileSystem mfs = new MemoryFileSystem();
        mfs.createFolder(new MemoryFolder(mfs, PortablePath.of( "src/main/java" )));

        MemoryKieModule mkm = new MemoryKieModule(releaseId, kieModuleModel, mfs);
        ResourceProvider resourceProvider = mkm.createResourceProvider();

        String folderContents = IOUtils.toString(resourceProvider.getResource("src/main/java").openStream());
        assertThat(folderContents).isEmpty();
    }

    @Test
    public void testGetResourceForFolderWithOnlySubFolders() throws Exception {
        MemoryFileSystem mfs = new MemoryFileSystem();
        mfs.createFolder(new MemoryFolder(mfs, PortablePath.of( "src/main/java" )));
        mfs.createFolder(new MemoryFolder(mfs, PortablePath.of( "src/main/java/org" )));
        mfs.createFolder(new MemoryFolder(mfs, PortablePath.of( "src/main/java/com" )));

        MemoryKieModule mkm = new MemoryKieModule(releaseId, kieModuleModel, mfs);
        ResourceProvider resourceProvider = mkm.createResourceProvider();

        String folderContents = IOUtils.toString(resourceProvider.getResource("src/main/java").openStream());
        assertThat(folderContents).hasLineCount(2).contains("com", "org");
    }

    @Test
    public void testGetResourceForFolderWithFilesAndSubFolders() throws Exception {
        MemoryFileSystem mfs = new MemoryFileSystem();
        mfs.createFolder(new MemoryFolder(mfs, PortablePath.of( "src/main/java" )));
        mfs.createFolder(new MemoryFolder(mfs, PortablePath.of( "src/main/java/org" )));
        mfs.createFolder(new MemoryFolder(mfs, PortablePath.of( "src/main/java/com" )));
        mfs.setFileContents(new MemoryFile(mfs, "my-file1", mfs.getFolder("src/main/java")), new byte[10]);
        mfs.setFileContents(new MemoryFile(mfs, "my-file2", mfs.getFolder("src/main/java")), new byte[10]);

        MemoryKieModule mkm = new MemoryKieModule(releaseId, kieModuleModel, mfs);
        ResourceProvider resourceProvider = mkm.createResourceProvider();

        String folderContents = IOUtils.toString(resourceProvider.getResource("src/main/java").openStream());
        assertThat(folderContents).hasLineCount(4).contains("com", "org", "my-file1", "my-file2");
    }

    @Test
    public void testGetResourceAsStreamForFile() throws Exception {
        MemoryFileSystem mfs = new MemoryFileSystem();
        mfs.setFileContents(new MemoryFile(mfs, "my-file1", mfs.getFolder("src/main/resources")), new byte[] {65, 66});

        MemoryKieModule mkm = new MemoryKieModule(releaseId, kieModuleModel, mfs);
        ResourceProvider resourceProvider = mkm.createResourceProvider();

        String folderContents = IOUtils.toString(resourceProvider.getResourceAsStream("src/main/resources/my-file1"));
        assertThat(folderContents).hasLineCount(1).contains("AB"); // "AB" == new byte[] {65, 66}
    }

    @Test
    public void testGetResourceAsStreamFolderWithOnlySubFolders() throws Exception {
        MemoryFileSystem mfs = new MemoryFileSystem();
        mfs.createFolder(new MemoryFolder(mfs, PortablePath.of( "src/main/java" )));
        mfs.createFolder(new MemoryFolder(mfs, PortablePath.of( "src/main/java/org" )));
        mfs.createFolder(new MemoryFolder(mfs, PortablePath.of( "src/main/java/com" )));

        MemoryKieModule mkm = new MemoryKieModule(releaseId, kieModuleModel, mfs);
        ResourceProvider resourceProvider = mkm.createResourceProvider();

        String folderContents = IOUtils.toString(resourceProvider.getResourceAsStream("src/main/java"));
        assertThat(folderContents).hasLineCount(2).contains("com", "org");
    }

    @Test
    public void testGetResourceTrailingSlashIgnored() throws Exception {
        MemoryFileSystem mfs = new MemoryFileSystem();
        mfs.setFileContents(new MemoryFile(mfs, "my-file1", mfs.getFolder("src/main/resources")), new byte[] {65, 66});

        MemoryKieModule mkm = new MemoryKieModule(releaseId, kieModuleModel, mfs);
        ResourceProvider resourceProvider = mkm.createResourceProvider();

        String noTrailingSlashContents = IOUtils.toString(resourceProvider.getResourceAsStream("src/main/resources/my-file1"));
        assertThat(noTrailingSlashContents).hasLineCount(1).contains("AB"); // "AB" == new byte[] {65, 66}

        String withTrailingSlashContents = IOUtils.toString(resourceProvider.getResourceAsStream("src/main/resources/my-file1/"));
        assertThat(withTrailingSlashContents).hasLineCount(1).contains("AB"); // "AB" == new byte[] {65, 66}
    }

}
