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
package org.jbpm.services.task.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.MVELSafeHelper;
import org.jbpm.services.task.internals.lifecycle.Allowed;
import org.jbpm.services.task.internals.lifecycle.OperationCommand;
import org.kie.api.command.Command;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.AllowedToDelegate;
import org.kie.internal.task.api.model.CommandName;
import org.kie.internal.task.api.model.NotificationType;
import org.kie.internal.task.api.model.Operation;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

/**
 *
 */
public class MVELUtils { 
    private static Map<String, Class<?>> inputs = new HashMap<String, Class<?>>();
    private static TaskModelFactory factory = TaskModelProvider.getFactory(); 
    
    public static Map<String, Class<?>> getInputs() {
        synchronized (inputs) {
            if (inputs.isEmpty()) {
                // org.jbpm.services.task
                inputs.put("AccessType", AccessType.class);
                inputs.put("AllowedToDelegate", AllowedToDelegate.class);
                inputs.put("Attachment", factory.newAttachment().getClass());
                inputs.put("BooleanExpression", factory.newBooleanExpression().getClass());
                inputs.put("Comment", factory.newComment().getClass());
                inputs.put("Content", factory.newContent().getClass());
                inputs.put("Deadline", factory.newDeadline().getClass());
                inputs.put("Deadlines", factory.newDeadlines().getClass());
                inputs.put("Delegation", factory.newDelegation().getClass());
                inputs.put("EmailNotification", factory.newEmialNotification().getClass());
                inputs.put("EmailNotificationHeader", factory.newEmailNotificationHeader().getClass());
                inputs.put("Escalation", factory.newEscalation().getClass());
                inputs.put("Group", factory.newGroup().getClass());
                inputs.put("I18NText", factory.newI18NText().getClass());
                inputs.put("Notification", factory.newNotification().getClass());
                inputs.put("NotificationType", NotificationType.class);
                inputs.put("OrganizationalEntity", OrganizationalEntity.class);
                inputs.put("PeopleAssignments", factory.newPeopleAssignments().getClass());
                inputs.put("Reassignment", factory.newReassignment().getClass());
                inputs.put("Status", Status.class);
                inputs.put("Task", factory.newTask().getClass());
                inputs.put("TaskData", factory.newTaskData().getClass());
                inputs.put("User", factory.newUser().getClass());
                inputs.put("UserInfo", UserInfo.class);
                inputs.put("SubTasksStrategy",SubTasksStrategy.class);
                inputs.put("Language", factory.newLanguage().getClass());
                

                // org.jbpm.services.task.service
                inputs.put("Allowed", Allowed.class);
                inputs.put("Command", Command.class);
                inputs.put("CommandName", CommandName.class);
                inputs.put("ContentData", factory.newContentData().getClass());
                inputs.put("Operation", Operation.class);
                inputs.put("Operation.Claim", Operation.class);
                inputs.put("Operation.Delegate", Operation.class);
                inputs.put("OperationCommand", OperationCommand.class);

                // org.drools.task.query
                inputs.put("DeadlineSummary", factory.newDeadline().getClass());
                inputs.put("TaskSummary", factory.newTaskSummary().getClass());
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
    	pconf.addPackageImport("org.jbpm.services.task");
//    	pconf.addPackageImport("org.jbpm.services.task.service");
        
    	pconf.addPackageImport("org.jbpm.services.task.query");
    	pconf.addPackageImport("java.util");
    	
    	for(String entry : getInputs().keySet()){
    		pconf.addImport(entry, getInputs().get(entry));
        }
    	ParserContext context = new ParserContext(pconf);
        Serializable s = MVEL.compileExpression(str.trim(), context);

        if( vars != null ) { 
        return MVELSafeHelper.getEvaluator().executeExpression(s, vars);
    }
        else { 
            return MVELSafeHelper.getEvaluator().executeExpression(s);
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
