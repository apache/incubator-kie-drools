/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.ftp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.drools.process.instance.WorkItemHandler;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.process.workitem.email.Connection;



/**
 *
 * @author salaboy
 */
public class FTPUploadWorkItemHandler implements WorkItemHandler {
    private String user;
    private String password;
    private String server;
    private String filePath;
    private int port;
    private FTPClient client; 
   
    private Connection connection;
    private boolean result = false;

	public void setConnection(String host, String port, String userName, String password) {
		connection = new Connection();
		connection.setHost(host);
		connection.setPort(port);
		connection.setUserName(userName);
		connection.setPassword(password);
	}
    



    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        

        this.user = (String) workItem.getParameter("User");
        this.password = (String) workItem.getParameter("Password");
        this.filePath = (String) workItem.getParameter("FilePath");
        
        client = new FTPClient();
        try {
            if(connection != null){
                client.connect(connection.getHost(), Integer.parseInt(connection.getPort()));
                int reply = client.getReplyCode();

                if (FTPReply.isPositiveCompletion(reply))
                {
            
                    if(client.login(user, password)){

                        InputStream input;
                        input = new FileInputStream(filePath);
                        client.setFileType(FTP.BINARY_FILE_TYPE);
                        this.setResult(client.storeFile(filePath, input));
                         client.logout();
                    }
                }

            }
        } catch (SocketException ex) {
            Logger.getLogger(FTPUploadWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FTPUploadWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       
       

        manager.completeWorkItem(workItem.getId(), null);

         
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the result
     */
    public boolean isResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(boolean result) {
        this.result = result;
    }

}
