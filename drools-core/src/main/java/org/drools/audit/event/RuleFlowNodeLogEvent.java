package org.drools.audit.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class RuleFlowNodeLogEvent extends RuleFlowLogEvent {
    
    private String nodeId;
    private String nodeName;

    /**
     * Create a new ruleflow node log event.
     * 
     * @param type The type of event.  This can only be RULEFLOW_NODE_START or RULEFLOW_NODE_END.
     * @param processId The id of the process
     * @param processName The name of the process
     */
    public RuleFlowNodeLogEvent(final int type,
                                final String nodeId,
                                final String nodeName,
                                final String processId,
                                final String processName,
                                final long processInstanceId) {
        super( type, processId, processName, processInstanceId );
        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        nodeId    = (String)in.readObject();
        nodeName    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(nodeId);
        out.writeObject(nodeName);
    }

    public String getNodeId() {
        return nodeId;
    }
    
    public String getNodeName() {
        return nodeName;
    }
    
    public String toString() {
        String msg = null;
        switch ( this.getType() ) {
            case BEFORE_RULEFLOW_NODE_TRIGGERED :
                msg = "BEFORE RULEFLOW NODE TRIGGERED";
                break;
            case AFTER_RULEFLOW_NODE_TRIGGERED :
                msg = "AFTER RULEFLOW NODE TRIGGERED";
                break;
            default:
                return super.toString();
        }
        return msg + " node:" + nodeName + "[id=" + nodeId + "] process:" + getProcessName() + "[id=" + getProcessId() + "]";
    }

}
