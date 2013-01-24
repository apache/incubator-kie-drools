package org.droolsjbpm.services.impl.event;

import java.io.Serializable;

import org.kie.event.process.ProcessNodeEvent;

public class NodeInstanceEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    private long processInstanceId;
    private long nodeInstanceId;
    private String nodeName;
    private long nodeId;
    
    public NodeInstanceEvent(ProcessNodeEvent event) {
        this.nodeInstanceId = event.getNodeInstance().getId();
        this.nodeName = event.getNodeInstance().getNodeName();
        this.nodeId = event.getNodeInstance().getNodeId();
        this.processInstanceId = event.getProcessInstance().getId();
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public long getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(long nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
    
}
