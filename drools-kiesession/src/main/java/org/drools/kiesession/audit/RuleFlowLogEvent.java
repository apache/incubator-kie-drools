package org.drools.kiesession.audit;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.kie.api.runtime.process.ProcessInstance;

/**
 * A ruleflow event logged by the WorkingMemoryLogger.
 * It is a snapshot of the event as it was thrown by the working memory.
 * It contains the process name and id.
 */
public class RuleFlowLogEvent extends LogEvent {

    private String processId;
    private String processName;
    private Object processInstanceId;

    public RuleFlowLogEvent(final int type,
                            ProcessInstance processInstance) {
        this(type, processInstance.getProcessId(), processInstance.getProcessName(), processInstance.getId());
    }

    /**
     * Create a new ruleflow log event.
     * 
     * @param type The type of event.  This can only be RULEFLOW_CREATED,
     *        RULEFLOW_COMPLETED, RULEFLOW_NODE_START or RULEFLOW_NODE_END.
     * @param processId The id of the process
     * @param processName The name of the process
     */
    public RuleFlowLogEvent(final int type,
                            final String processId,
                            final String processName,
                            final Object processInstanceId) {
        super( type );
        this.processId = processId;
        this.processName = processName;
        this.processInstanceId = processInstanceId;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        processId    = (String)in.readObject();
        processName    = (String)in.readObject();
        processInstanceId = in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(processId);
        out.writeObject(processName);
        out.writeObject(processInstanceId);
    }

    public String getProcessId() {
        return this.processId;
    }

    public String getProcessName() {
        return this.processName;
    }
    
    public Object getProcessInstanceId() {
        return this.processInstanceId;
    }

    public String toString() {
        String msg = null;
        switch ( this.getType() ) {
            case BEFORE_RULEFLOW_CREATED :
                msg = "BEFORE RULEFLOW STARTED";
                break;
            case AFTER_RULEFLOW_CREATED :
                msg = "AFTER RULEFLOW STARTED";
                break;
            case BEFORE_RULEFLOW_COMPLETED :
                msg = "BEFORE RULEFLOW COMPLETED";
                break;
            case AFTER_RULEFLOW_COMPLETED :
                msg = "AFTER RULEFLOW COMPLETED";
                break;
        }
        return msg + " process:" + this.processName + "[id=" + this.processId + "]";
    }
}
