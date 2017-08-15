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

package org.jbpm.process.workitem.ftp;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.jbpm.process.workitem.email.Connection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FTPUploadWorkItemHandlerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Mock
    FTPClient client;

    @Mock
    Connection connection;

    @Test
    public void testFTPUpload() throws Exception {
        doNothing().when(client).connect("abc",
                                         123);
        when(client.getReplyCode()).thenReturn(200);
        when(client.login(anyString(),
                          anyString())).thenReturn(true);
        when(client.setFileType(anyInt())).thenReturn(true);
        when(client.storeFile(anyString(),
                              anyObject())).thenReturn(true);
        when(client.logout()).thenReturn(true);

        when(connection.getHost()).thenReturn("abc");
        when(connection.getPort()).thenReturn("123");

        File tempFile = tempFolder.newFile("tempFile1.txt");
        FileUtils.writeStringToFile(tempFile,
                                    "temp file content",
                                    "UTF-8");

        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId(123L);

        workItem.setParameter("File",
                              tempFile);
        workItem.setParameter("User",
                              "someuser");
        workItem.setParameter("Password",
                              "somepassword");

        FTPUploadWorkItemHandler handler = new FTPUploadWorkItemHandler();
        handler.setLogThrownException(true);
        handler.setFTPClient(client);
        handler.setConnection(connection);

        handler.executeWorkItem(workItem,
                                manager);

        assertNotNull(manager.getResults());
        assertEquals(1,
                     manager.getResults().size());
        assertTrue(manager.getResults().containsKey(workItem.getId()));
    }
}
