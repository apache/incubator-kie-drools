/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.casemgmt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.Task;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalI18NText;

public class UpdateTaskNameCommand implements GenericCommand<Object> {
    
    private static final long serialVersionUID = 7323092505416116457L;
    
    private TaskService taskService;
    private long processInstanceId;
    private Task task;
    private String newName;

    public UpdateTaskNameCommand(TaskService taskService, long processInstanceId, Task task, String newName) {
        this.taskService = taskService;
        this.processInstanceId = processInstanceId;
        this.task = task;
        this.newName = newName;
    }

    

    @Override
    public Object execute(Context context) {
        KieSession kieSession = ((KnowledgeCommandContext) context).getKieSession();
        
        Collection<NodeInstance> nodes = ((WorkflowProcessInstance) kieSession.getProcessInstance(processInstanceId)).getNodeInstances();
        for (NodeInstance ni : nodes) {
            if (ni.getNodeName().equals(task.getName())) {
                ((NodeImpl)ni.getNode()).setName(newName);
            }
        }
        
        List<I18NText> updatedNames = new ArrayList<I18NText>();
        I18NText updatedName = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) updatedName).setLanguage(task.getNames().get(0).getLanguage());
        ((InternalI18NText) updatedName).setText(newName);
        updatedNames.add(updatedName);

        ((InternalTaskService) taskService).setTaskNames(task.getId(), updatedNames);
        
        return null;
    }

}
