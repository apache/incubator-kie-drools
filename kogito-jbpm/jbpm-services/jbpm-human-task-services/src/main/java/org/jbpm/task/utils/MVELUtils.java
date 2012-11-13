/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.utils;

import java.io.IOException; 
import java.io.Reader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.kie.command.Command;
import org.jbpm.task.AccessType;
import org.jbpm.task.AllowedToDelegate;
import org.jbpm.task.Attachment;
import org.jbpm.task.BooleanExpression;
import org.jbpm.task.CommandName;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.ContentData;
import org.jbpm.task.Deadline;
import org.jbpm.task.Deadlines;
import org.jbpm.task.Delegation;
import org.jbpm.task.EmailNotification;
import org.jbpm.task.EmailNotificationHeader;
import org.jbpm.task.Escalation;
import org.jbpm.task.Group;
import org.jbpm.task.I18NText;
import org.jbpm.task.Notification;
import org.jbpm.task.NotificationType;
import org.jbpm.task.Operation;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Reassignment;
import org.jbpm.task.Status;
import org.jbpm.task.StatusChange;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.UserInfo;
import org.jbpm.task.internals.lifecycle.Allowed;
import org.jbpm.task.internals.lifecycle.OperationCommand;
import org.jbpm.task.query.DeadlineSummary;
import org.jbpm.task.query.TaskSummary;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

/**
 *
 */
public class MVELUtils { 
     private static Map<String, Class<?>> inputs = new HashMap<String, Class<?>>();
    
    public static Map<String, Class<?>> getInputs() {
        synchronized (inputs) {
            if (inputs.isEmpty()) {
                // org.jbpm.task
                inputs.put("AccessType", AccessType.class);
                inputs.put("AllowedToDelegate", AllowedToDelegate.class);
                inputs.put("Attachment", Attachment.class);
                inputs.put("BooleanExpression", BooleanExpression.class);
                inputs.put("Comment", Comment.class);
                inputs.put("Content", Content.class);
                inputs.put("Deadline", Deadline.class);
                inputs.put("Deadlines", Deadlines.class);
                inputs.put("Delegation", Delegation.class);
                inputs.put("EmailNotification", EmailNotification.class);
                inputs.put("EmailNotificationHeader", EmailNotificationHeader.class);
                inputs.put("Escalation", Escalation.class);
                inputs.put("Group", Group.class);
                inputs.put("I18NText", I18NText.class);
                inputs.put("Notification", Notification.class);
                inputs.put("NotificationType", NotificationType.class);
                inputs.put("OrganizationalEntity", OrganizationalEntity.class);
                inputs.put("PeopleAssignments", PeopleAssignments.class);
                inputs.put("Reassignment", Reassignment.class);
                inputs.put("Status", Status.class);
                inputs.put("StatusChange", StatusChange.class);
                inputs.put("Task", Task.class);
                inputs.put("TaskData", TaskData.class);
                inputs.put("User", User.class);
                inputs.put("UserInfo", UserInfo.class);
                

                // org.jbpm.task.service
                inputs.put("Allowed", Allowed.class);
                inputs.put("Command", Command.class);
                inputs.put("CommandName", CommandName.class);
                inputs.put("ContentData", ContentData.class);
                inputs.put("Operation", Operation.class);
                inputs.put("Operation.Claim", Operation.class);
                inputs.put("OperationCommand", OperationCommand.class);

                // org.drools.task.query
                inputs.put("DeadlineSummary", DeadlineSummary.class);
                inputs.put("TaskSummary", TaskSummary.class);
            }
            return inputs;
        }
    }
    public static Object eval(Reader reader, Map<String, Object> vars) {
        try {
            return eval(toString(reader), vars);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown",e);
        }
    }
    public static Object eval(Reader reader) { 
        try {
            return eval(toString(reader), null);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown",e);
        }
    }
    
    public static Object eval(String str, Map<String, Object> vars) {
    	ParserConfiguration pconf = new ParserConfiguration();
    	pconf.addPackageImport("org.jbpm.task");
//    	pconf.addPackageImport("org.jbpm.task.service");
        
    	pconf.addPackageImport("org.jbpm.task.query");
    	pconf.addPackageImport("java.util");
    	
    	for(String entry : getInputs().keySet()){
    		pconf.addImport(entry, getInputs().get(entry));
        }
    	ParserContext context = new ParserContext(pconf);
        Serializable s = MVEL.compileExpression(str.trim(), context);

        if( vars != null ) { 
        return MVEL.executeExpression(s, vars);
    }
        else { 
            return MVEL.executeExpression(s);
        }
    }
    public static String toString(Reader reader) throws IOException {
        int charValue  ;
        StringBuffer sb = new StringBuffer(1024);
        while ((charValue = reader.read()) != -1) {
            sb.append((char) charValue);
        }
        return sb.toString();
    }
    
}
