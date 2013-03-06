package org.jbpm.workflow.instance;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.runtime.process.NodeInstance;

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
    
    private Map<String, Object> variables;

    public WorkflowRuntimeException(Exception e) {
        super(e);
    }

    public WorkflowRuntimeException(NodeInstance nodeInstance, String message, Exception e) {
        super(message, e);
        initialize(nodeInstance);
    }
    
    public WorkflowRuntimeException(NodeInstance nodeInstance, Exception e) {
        super(e);
        initialize(nodeInstance);
    }
    
    private void initialize(NodeInstance nodeInstance) {
        this.processInstanceId = nodeInstance.getProcessInstance().getId();
        this.processId = nodeInstance.getProcessInstance().getProcessId();
        this.nodeInstanceId = nodeInstance.getId();
        this.nodeId = nodeInstance.getNodeId();
        this.nodeName = nodeInstance.getNodeName();
        
        VariableScopeInstance variableScope 
            = (VariableScopeInstance) ((NodeImpl) ((NodeInstanceImpl) nodeInstance).getNode()).getContext(VariableScope.VARIABLE_SCOPE);
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
        return MessageFormat.format("[{0}:{4} - {1}:{2}] -- {3}", getProcessId(),
                getNodeName(), getNodeId(), getCause().getMessage(), getProcessInstanceId());
    }

}
