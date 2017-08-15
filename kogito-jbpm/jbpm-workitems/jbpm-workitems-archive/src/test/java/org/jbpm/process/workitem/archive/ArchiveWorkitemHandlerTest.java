/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.workitem.archive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class ArchiveWorkitemHandlerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testNoFilesSpecified() {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Archive",
                              "testfile.txt");
        workItem.setParameter("Files",
                              new ArrayList<File>());

        ArchiveWorkItemHandler archiveWorkItemHandler = new ArchiveWorkItemHandler();
        archiveWorkItemHandler.setLogThrownException(true);
        archiveWorkItemHandler.executeWorkItem(workItem,
                                               manager);

        assertEquals(1,
                     manager.getAbortedWorkItems().size());
        assertTrue(manager.getAbortedWorkItems().contains(workItem.getId()));
    }

    @Test
    public void testWithTempFile() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId(123L);

        File tempFile = tempFolder.newFile("tempFile1.txt");
        FileUtils.writeStringToFile(tempFile,
                                    "temp file content",
                                    "UTF-8");
        File tempFileTwo = tempFolder.newFile("tempFile2.txt");
        FileUtils.writeStringToFile(tempFileTwo,
                                    "temp file2 content",
                                    "UTF-8");

        List<java.io.File> filesList = new ArrayList<>();
        filesList.add(tempFile);
        filesList.add(tempFileTwo);

        workItem.setParameter("Archive",
                              "testfile.txt");
        workItem.setParameter("Files",
                              filesList);

        ArchiveWorkItemHandler archiveWorkItemHandler = new ArchiveWorkItemHandler();
        archiveWorkItemHandler.setLogThrownException(true);
        archiveWorkItemHandler.executeWorkItem(workItem,
                                               manager);

        assertNotNull(manager.getResults());
        assertEquals(1,
                     manager.getResults().size());
        assertTrue(manager.getResults().containsKey(workItem.getId()));
    }
}
