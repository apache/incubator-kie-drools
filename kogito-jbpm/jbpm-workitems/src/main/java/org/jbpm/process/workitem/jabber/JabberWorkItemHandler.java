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

package org.jbpm.process.workitem.jabber;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.process.instance.WorkItemHandler;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;



/**
 *
 * @author salaboy
 */
public class JabberWorkItemHandler implements WorkItemHandler {
    private String user;
    private String password;
    private String server;
    private int port;
    private String service;
    private String text;

    private List<String> toUsers = new ArrayList<String>();
    

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        this.user = (String) workItem.getParameter("User");
        this.password = (String) workItem.getParameter("Password");
        this.server = (String) workItem.getParameter("Server");
        String portString = (String) workItem.getParameter("Port");
        if(portString != null && !portString.equals("")){
            this.port = Integer.valueOf((String) workItem.getParameter("Port"));
        }
        this.service = (String) workItem.getParameter("Service");
        this.text = (String) workItem.getParameter("Text");


        String to = (String) workItem.getParameter("To");
		if ( to == null || to.trim().length() == 0 ) {
		    throw new RuntimeException( "IM must have one or more to adresses" );
		}
		for (String s: to.split(";")) {
			if (s != null && !"".equals(s)) {
				this.toUsers.add(s);
			}
		}
        ConnectionConfiguration conf = new ConnectionConfiguration(server, port , service);
        XMPPConnection connection = null;
        try {

           if(server !=null && !server.equals("") && port != 0){
             connection = new XMPPConnection(conf);
           } else {
             connection = new XMPPConnection(service);
           }

           connection.connect();
           System.out.println("Connected to " + connection.getHost());

        } catch (XMPPException ex) {
            Logger.getLogger(JabberWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Failed to connect to " + connection.getHost());
            System.exit(1);

        }

        try{
            connection.login(user, password);
            System.out.println("Logged in as " + connection.getUser());
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);

        } catch (XMPPException ex){
            Logger.getLogger(JabberWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Failed to log in as " + connection.getUser());
            System.exit(1);

        }

        for(String toUser : toUsers){

            ChatManager chatmanager = connection.getChatManager();
            Chat chat = chatmanager.createChat(toUser, null);

            try {
                // google bounces back the default message types, you must use chat
                Message msg = new Message(toUser, Message.Type.chat);
                msg.setBody(text);
                chat.sendMessage(msg);
                System.out.println("Message Sended");
            } catch (XMPPException e) {
                System.out.println("Failed to send message");
                // handle this how?
            }
        }


        connection.disconnect();

        manager.completeWorkItem(workItem.getId(), null);

         
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
