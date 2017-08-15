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

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.email.Connection;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

public class FTPUploadWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(FTPUploadWorkItemHandler.class);

    private String user;
    private String password;
    private String server;
    private String filePath;
    private File file;
    private int port;
    private FTPClient client;

    private Connection connection;
    private boolean result = false;

    public void setConnection(String host,
                              String port,
                              String userName,
                              String password) {
        connection = new Connection();
        connection.setHost(host);
        connection.setPort(port);
        connection.setUserName(userName);
        connection.setPassword(password);
    }

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {

        this.user = (String) workItem.getParameter("User");
        this.password = (String) workItem.getParameter("Password");
        this.filePath = (String) workItem.getParameter("FilePath");
        this.file = (File) workItem.getParameter("File");


        if(client == null) {
            client = new FTPClient();
        }

        try {
            if (connection != null) {
                client.connect(connection.getHost(),
                               Integer.parseInt(connection.getPort()));
                int reply = client.getReplyCode();

                if (FTPReply.isPositiveCompletion(reply)) {

                    if (client.login(user,
                                     password)) {

                        InputStream input;
                        if(filePath != null) {
                            input = new FileInputStream(filePath);
                        } else {
                            input = new FileInputStream(file);
                        }

                        client.setFileType(FTP.BINARY_FILE_TYPE);
                        this.setResult(client.storeFile(filePath,
                                                        input));
                        client.logout();

                        manager.completeWorkItem(workItem.getId(),
                                                 null);
                    } else {
                        logger.warn("Could not logon to FTP server, status returned {}",
                                    client.getStatus());
                    }
                } else {
                    logger.warn("Could not connect to FTP server, status returned {}",
                                client.getStatus());
                }
            }
        } catch (Exception ex) {
            handleException(ex);
            abortWorkItem(workItem,
                          manager);
        }
    }

    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    // for testing
    public void setFTPClient(FTPClient client) {
        this.client = client;
    }

    // for testing
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}

