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

package org.jbpm.workflow.instance;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * This exception provides the context information of the error in execution of the flow. <br/>
 * It would be helpful to located the error instead of confusing stack trace
 */
public class WorkflowRuntimeException extends RuntimeException {

    /** Generated serial version uid */
    private static final long serialVersionUID = 8210449548783940188L;

    private long processInstanceId;
    private String processId;
    private long nodeInstanceId;
    private long nodeId;
    private String nodeName;
    private String deploymentId;

    private Map<String, Object> variables;

    public WorkflowRuntimeException(NodeInstance nodeInstance, ProcessInstance processInstance, String message) {
        super(message);
        initialize(nodeInstance, processInstance);
    }

    public WorkflowRuntimeException(NodeInstance nodeInstance, ProcessInstance processInstance, String message, Throwable e) {
        super(message, e);
        initialize(nodeInstance, processInstance);
    }

    public WorkflowRuntimeException(NodeInstance nodeInstance, ProcessInstance processInstance, Exception e) {
        super(e);
        initialize(nodeInstance, processInstance);
    }

    private void initialize(NodeInstance nodeInstance, ProcessInstance processInstance) {
        this.processInstanceId = processInstance.getId();
        this.processId = processInstance.getProcessId();
        this.setDeploymentId(((ProcessInstanceImpl)processInstance).getDeploymentId());
        if( nodeInstance != null ) { 
            this.nodeInstanceId = nodeInstance.getId();
            this.nodeId = nodeInstance.getNodeId();
            if( ((ProcessInstanceImpl) processInstance).getKnowledgeRuntime() != null ) { 
                this.nodeName = nodeInstance.getNodeName();
            }
        }
        
        VariableScopeInstance variableScope =  (VariableScopeInstance) 
                ((org.jbpm.process.instance.ProcessInstance) processInstance).getContextInstance( 
                        VariableScope.VARIABLE_SCOPE );
            // set input parameters
        if( variableScope != null ) { 
            this.variables = variableScope.getVariables();
        } else { 
            this.variables = new HashMap<String, Object>(0);
        }
    }

    /**
     * @return the processInstanceId
     */
    public long getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * @param processInstanceId the processInstanceId to set
     */
    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    /**
     * @return the processId
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * @param processId the processId to set
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * @return the nodeInstanceId
     */
    public long getNodeInstanceId() {
        return nodeInstanceId;
    }

    /**
     * @param nodeInstanceId the nodeInstanceId to set
     */
    public void setNodeInstanceId(long nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    /**
     * @return the nodeId
     */
    public long getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName the nodeName to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format("[{0}:{4} - {1}:{2}] -- {3}", 
                getProcessId(),
                (getNodeName() == null ? "?" : getNodeName()), 
                (getNodeId() == 0 ? "?" : getNodeId()), 
                (getCause() == null ? getMessage() : getCause().getMessage()), 
                getProcessInstanceId());
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

}
