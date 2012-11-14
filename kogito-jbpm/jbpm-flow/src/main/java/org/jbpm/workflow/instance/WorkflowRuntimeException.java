package org.jbpm.workflow.instance;

import java.text.MessageFormat;

import org.kie.runtime.process.NodeInstance;

/**
 * This exception provides the context information of the error in execution of the flow. <br/>
 * It would be helpful to located the error instead of confusing stack trace
 * 
 * @author tanxu
 * @date Mar 19, 2012
 * @since
 */
public class WorkflowRuntimeException extends RuntimeException {

    private long processInstanceId;
    private String processId;
    private long nodeInstanceId;
    private long nodeId;
    private String nodeName;

    public WorkflowRuntimeException(Exception e) {
        super(e);
    }

    public WorkflowRuntimeException(NodeInstance nodeInstance, Exception e) {
        super(e);
        this.processInstanceId = nodeInstance.getProcessInstance().getId();
        this.processId = nodeInstance.getProcessInstance().getProcessId();
        this.nodeInstanceId = nodeInstance.getId();
        this.nodeId = nodeInstance.getNodeId();
        this.nodeName = nodeInstance.getNodeName();
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

    @Override
    public String getMessage() {
        return MessageFormat.format("[{0}:{4} - {1}:{2}] -- {3}", getProcessId(),
                getNodeName(), getNodeId(), getCause().getMessage(), getProcessInstanceId());
    }
}
