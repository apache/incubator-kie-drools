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

import org.jbpm.task.impl.model.AttachmentImpl;
import org.jbpm.task.impl.model.BooleanExpressionImpl;
import org.jbpm.task.impl.model.CommentImpl;
import org.jbpm.task.impl.model.ContentDataImpl;
import org.jbpm.task.impl.model.ContentImpl;
import org.jbpm.task.impl.model.DeadlineImpl;
import org.jbpm.task.impl.model.DeadlinesImpl;
import org.jbpm.task.impl.model.DelegationImpl;
import org.jbpm.task.impl.model.EmailNotificationHeaderImpl;
import org.jbpm.task.impl.model.EmailNotificationImpl;
import org.jbpm.task.impl.model.EscalationImpl;
import org.jbpm.task.impl.model.GroupImpl;
import org.jbpm.task.impl.model.I18NTextImpl;
import org.jbpm.task.impl.model.LanguageImpl;
import org.jbpm.task.impl.model.NotificationImpl;
import org.jbpm.task.impl.model.OrganizationalEntityImpl;
import org.jbpm.task.impl.model.PeopleAssignmentsImpl;
import org.jbpm.task.impl.model.ReassignmentImpl;
import org.jbpm.task.impl.model.TaskDataImpl;
import org.jbpm.task.impl.model.TaskImpl;
import org.jbpm.task.impl.model.UserImpl;
import org.jbpm.task.internals.lifecycle.Allowed;
import org.jbpm.task.internals.lifecycle.OperationCommand;
import org.jbpm.task.query.DeadlineSummaryImpl;
import org.jbpm.task.query.TaskSummaryImpl;
import org.kie.api.command.Command;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.AllowedToDelegate;
import org.kie.internal.task.api.model.CommandName;
import org.kie.internal.task.api.model.NotificationType;
import org.kie.internal.task.api.model.Operation;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.SubTasksStrategy;
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
                inputs.put("Attachment", AttachmentImpl.class);
                inputs.put("BooleanExpression", BooleanExpressionImpl.class);
                inputs.put("Comment", CommentImpl.class);
                inputs.put("Content", ContentImpl.class);
                inputs.put("Deadline", DeadlineImpl.class);
                inputs.put("Deadlines", DeadlinesImpl.class);
                inputs.put("Delegation", DelegationImpl.class);
                inputs.put("EmailNotification", EmailNotificationImpl.class);
                inputs.put("EmailNotificationHeader", EmailNotificationHeaderImpl.class);
                inputs.put("Escalation", EscalationImpl.class);
                inputs.put("Group", GroupImpl.class);
                inputs.put("I18NText", I18NTextImpl.class);
                inputs.put("Notification", NotificationImpl.class);
                inputs.put("NotificationType", NotificationType.class);
                inputs.put("OrganizationalEntity", OrganizationalEntityImpl.class);
                inputs.put("PeopleAssignments", PeopleAssignmentsImpl.class);
                inputs.put("Reassignment", ReassignmentImpl.class);
                inputs.put("Status", Status.class);
                inputs.put("Task", TaskImpl.class);
                inputs.put("TaskData", TaskDataImpl.class);
                inputs.put("User", UserImpl.class);
                inputs.put("UserInfo", UserInfo.class);
                inputs.put("SubTasksStrategy",SubTasksStrategy.class);
                inputs.put("Language",LanguageImpl.class);
                

                // org.jbpm.task.service
                inputs.put("Allowed", Allowed.class);
                inputs.put("Command", Command.class);
                inputs.put("CommandName", CommandName.class);
                inputs.put("ContentData", ContentDataImpl.class);
                inputs.put("Operation", Operation.class);
                inputs.put("Operation.Claim", Operation.class);
                inputs.put("Operation.Delegate", Operation.class);
                inputs.put("OperationCommand", OperationCommand.class);

                // org.drools.task.query
                inputs.put("DeadlineSummary", DeadlineSummaryImpl.class);
                inputs.put("TaskSummary", TaskSummaryImpl.class);
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
