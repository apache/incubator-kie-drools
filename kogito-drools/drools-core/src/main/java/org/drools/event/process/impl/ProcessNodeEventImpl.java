package org.drools.event.process.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.WorkingMemory;
import org.drools.event.RuleFlowNodeTriggeredEvent;
import org.drools.event.process.ProcessNodeEvent;
import org.drools.runtime.process.NodeInstance;

public class ProcessNodeEventImpl extends ProcessEventImpl implements ProcessNodeEvent {

    private NodeInstance nodeInstance;

    public ProcessNodeEventImpl(RuleFlowNodeTriggeredEvent event, WorkingMemory workingMemory) {
        super(event, workingMemory);
        nodeInstance = event.getRuleFlowNodeInstance();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( nodeInstance );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.nodeInstance = (NodeInstance) in.readObject();
    }

	public NodeInstance getNodeInstance() {
		return nodeInstance;
	}

	@Override
	public String toString() {
		return "==>[ProcessNodeEventImpl: getNodeInstance()=" + getNodeInstance() + ", getProcessInstance()="
				+ getProcessInstance() + ", getKnowledgeRuntime()=" + getKnowledgeRuntime() + "]";
	}
}
