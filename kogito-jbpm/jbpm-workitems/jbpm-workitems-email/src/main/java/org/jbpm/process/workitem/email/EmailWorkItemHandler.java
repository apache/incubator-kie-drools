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

package org.jbpm.process.workitem.email;

import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * WorkItemHandler for sending email.
 * 
 * Expects the following parameters:
 *  - "From" (String): sends an email from the given the email address
 *  - "To" (String): sends the email to the given email address(es),
 *                   multiple addresses must be separated using a semi-colon (';') 
 *  - "Subject" (String): the subject of the email
 *  - "Body" (String): the body of the email (using HTML)
 *  - "Template" (String): optional template to generate body of the email, template when given will override Body parameter
 * Is completed immediately and does not return any result parameters.  
 * 
 * Sending an email cannot be aborted.
 * 
 */	
public class EmailWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

	private Connection connection;
	private TemplateManager templateManager = TemplateManager.get();

	public EmailWorkItemHandler() {
	}

	public EmailWorkItemHandler(String host, String port, String userName, String password) {
		setConnection(host, port, userName, password);
	}

	public EmailWorkItemHandler(String host, String port, String userName, String password, String startTls) {
		setConnection(host, port, userName, password, startTls);
	}

	public void setConnection(String host, String port, String userName, String password) {
		connection = new Connection();
		connection.setHost(host);
		connection.setPort(port);
		connection.setUserName(userName);
		connection.setPassword(password);
	}

	public void setConnection(String host, String port, String userName, String password, String startTls) {
		connection = new Connection();
		connection.setHost(host);
		connection.setPort(port);
		connection.setUserName(userName);
		connection.setPassword(password);
		connection.setStartTls(Boolean.parseBoolean(startTls));
	}

	public Connection getConnection() {
		return connection;
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		try {
    		Email email = createEmail(workItem, connection);
    		SendHtml.sendHtml(email, getDebugFlag(workItem));
    		// avoid null pointer when used from deadline escalation handler
    	    if (manager != null) {
    	 	  manager.completeWorkItem(workItem.getId(), null);
    	    }
		} catch (Exception e) {
		    handleException(e);
		}
	}

	protected Email createEmail(WorkItem workItem, Connection connection) {
	    Email email = new Email();
        Message message = new Message();
        message.setFrom((String) workItem.getParameter("From"));
        message.setReplyTo( (String) workItem.getParameter("Reply-To"));

        // Set recipients
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

        // Set cc
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

        // Set bcc
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

        // Fill message
        String body = (String) workItem.getParameter("Body");
        String template = (String) workItem.getParameter("Template");
        if (template != null) {            
            body = templateManager.render(template, workItem.getParameters());
        }
        
        message.setRecipients(recipients);
        message.setSubject((String) workItem.getParameter("Subject"));
        message.setBody(body);

        // fill attachments
        String attachmentList = (String) workItem.getParameter("Attachments");
        if (attachmentList != null) {
            String[] attachments = attachmentList.split(",");
            message.setAttachments(java.util.Arrays.asList(attachments));
        }

        // setup email
        email.setMessage(message);
        email.setConnection(connection);

        return email;
	}

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// Do nothing, email cannot be aborted
	}

	protected boolean getDebugFlag(WorkItem workItem) {
	    Object debugParam  = workItem.getParameter("Debug");
	    if (debugParam == null) {
	        return false;
	    }
	    
	    return Boolean.parseBoolean(debugParam.toString());
	}

}
