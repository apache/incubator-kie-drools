package org.drools.event.process.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.WorkingMemory;
import org.drools.event.knowlegebase.impl.KnowledgeRuntimeEventImpl;
import org.drools.event.process.ProcessEvent;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.process.ProcessInstance;

public class ProcessEventImpl extends KnowledgeRuntimeEventImpl implements ProcessEvent {

    private ProcessInstance processInstance;

    public ProcessEventImpl(org.drools.event.ProcessEvent event, WorkingMemory workingMemory) {
        super( new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) workingMemory ));
        processInstance = event.getProcessInstance();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( processInstance );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.processInstance = (ProcessInstance) in.readObject();
    }

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

}
