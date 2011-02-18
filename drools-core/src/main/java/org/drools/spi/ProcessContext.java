/*
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

package org.drools.spi;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;

public class ProcessContext implements org.drools.runtime.process.ProcessContext {
    
    private KnowledgeRuntime kruntime;
    private ProcessInstance processInstance;
    private NodeInstance nodeInstance;
    
    public ProcessContext(KnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
    }

    public ProcessInstance getProcessInstance() {
        if (processInstance != null) {
        	return processInstance;
        }
        if (nodeInstance != null) {
        	return (ProcessInstance) nodeInstance.getProcessInstance();
        }
        return null;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }
    
    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

    public void setNodeInstance(NodeInstance nodeInstance) {
        this.nodeInstance = nodeInstance;
    }
    
    public Object getVariable(String variableName) {
        if (nodeInstance != null) {
            return nodeInstance.getVariable(variableName);
        } else {
        	return ((WorkflowProcessInstance) getProcessInstance()).getVariable(variableName);
        }
    }
    
    public void setVariable(String variableName, Object value) {
        if (nodeInstance != null) {
        	nodeInstance.setVariable(variableName, value);
        } else {
        	((WorkflowProcessInstance) getProcessInstance()).setVariable(variableName, value);
        }
    }

    public KnowledgeRuntime getKnowledgeRuntime() {
        return kruntime;
    }
    
}
