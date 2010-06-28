package org.drools.audit.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class RuleFlowVariableLogEvent extends RuleFlowLogEvent {
    
    private String variableId;
    private String variableInstanceId;
    private String objectToString;

    /**
     * Create a new ruleflow variable log event.
     */
    public RuleFlowVariableLogEvent(final int type,
                                    final String variableId,
                                    final String variableInstanceId,
                                    final String processId,
                                    final String processName,
                                    final long processInstanceId,
                                    final String objectToString) {
        super( type, processId, processName, processInstanceId );
        this.variableId = variableId;
        this.variableInstanceId = variableInstanceId;
        this.objectToString = objectToString;
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        variableId = (String) in.readObject();
        variableInstanceId = (String) in.readObject();
        objectToString = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(variableId);
        out.writeObject(variableInstanceId);
        out.writeObject(objectToString);
    }

    public String getVariableId() {
		return variableId;
	}

	public String getVariableInstanceId() {
		return variableInstanceId;
	}

	public String getObjectToString() {
		return objectToString;
	}

	public String toString() {
        String msg = null;
        switch ( this.getType() ) {
            case BEFORE_VARIABLE_INSTANCE_CHANGED :
                msg = "BEFORE RULEFLOW VARIABLE CHANGED";
                break;
            case AFTER_VARIABLE_INSTANCE_CHANGED :
                msg = "AFTER RULEFLOW VARIABLE CHANGED";
                break;
            default:
                return super.toString();
        }
        return msg + " " + variableId + "=" + objectToString + " process:" + getProcessName() + "[id=" + getProcessId() + "]";
    }

}
