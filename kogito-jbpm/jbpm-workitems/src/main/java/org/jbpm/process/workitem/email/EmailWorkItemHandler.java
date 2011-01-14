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

package org.jbpm.process.workitem.email;

import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

/**
 * WorkItemHandler for sending email.
 * 
 * Expects the following parameters:
 *  - "From" (String): sends an email from the given the email address
 *  - "To" (String): sends the email to the given email address(es),
 *                   multiple addresses must be separated using a semi-colon (';') 
 *  - "Subject" (String): the subject of the email
 *  - "Text" (String): the body of the email (using HTML)
 * Is completed immediately and does not return any result parameters.  
 * 
 * Sending an email cannot be aborted.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */	
public class EmailWorkItemHandler implements WorkItemHandler {

	private Connection connection;
	
	public void setConnection(String host, String port, String userName, String password) {
		connection = new Connection();
		connection.setHost(host);
		connection.setPort(port);
		connection.setUserName(userName);
		connection.setPassword(password);
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		if (connection == null) {
			throw new IllegalArgumentException(
				"Connection not initialized for Email");
		}
		Email email = new Email();
		Message message = new Message();
		message.setFrom((String) workItem.getParameter("From"));
		message.setReplyTo( (String) workItem.getParameter("Reply-To"));
		Recipients recipients = new Recipients();
		String to = (String) workItem.getParameter("To");
		if ( to == null || to.trim().length() == 0 ) {
		    throw new RuntimeException( "Email must have one or more to adresses" );
		}
		for (String s: to.split(";")) {
			if (s != null && !"".equals(s)) {
				Recipient recipient = new Recipient();
				recipient.setEmail(s);
                recipient.setType( "To" );
				recipients.addRecipient(recipient);
			}
		}
        String cc = (String) workItem.getParameter("Cc");
        if ( cc != null && cc.trim().length() > 0 ) {
            for (String s: cc.split(";")) {
                if (s != null && !"".equals(s)) {
                    Recipient recipient = new Recipient();
                    recipient.setEmail(s);
                    recipient.setType( "Cc" );
                    recipients.addRecipient(recipient);
                }
            }		
        }
        String bcc = (String) workItem.getParameter("Bcc");
        if ( bcc != null && bcc.trim().length() > 0 ) {
            for (String s: bcc.split(";")) {
                if (s != null && !"".equals(s)) {
                    Recipient recipient = new Recipient();
                    recipient.setEmail(s);
                    recipient.setType( "Bcc" );
                    recipients.addRecipient(recipient);
                }
            }		
        }
		message.setRecipients(recipients);
		message.setSubject((String) workItem.getParameter("Subject"));
		message.setBody((String) workItem.getParameter("Body"));
		email.setMessage(message);
		email.setConnection(connection);
		SendHtml.sendHtml(email);
		manager.completeWorkItem(workItem.getId(), null);
	}

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// Do nothing, email cannot be aborted
	}

}
