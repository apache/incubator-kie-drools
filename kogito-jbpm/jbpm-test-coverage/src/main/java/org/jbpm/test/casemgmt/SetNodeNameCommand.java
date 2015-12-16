/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.command.Context;

public class SetNodeNameCommand implements GenericCommand<Object> {
    
    private static final long serialVersionUID = 7323092505416116457L;
    
    private long processInstanceId;
    private String oldName;
    private String newName;

    public SetNodeNameCommand(long processInstanceId, String oldName, String newName) {
        this.processInstanceId = processInstanceId;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public Object execute(Context context) {
        KieSession kieSession = ((KnowledgeCommandContext) context).getKieSession();
        
        Collection<NodeInstance> nodes = ((WorkflowProcessInstance) kieSession.getProcessInstance(processInstanceId)).getNodeInstances();
        for (NodeInstance ni : nodes) {
            if (ni.getNodeName().equals(oldName)) {
                ((NodeImpl)ni.getNode()).setName(newName);
            }
        }
        
        return null;
    }

}
