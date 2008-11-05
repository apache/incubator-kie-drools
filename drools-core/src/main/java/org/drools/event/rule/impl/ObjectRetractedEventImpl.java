package org.drools.event.rule.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.FactHandle;
import org.drools.runtime.rule.WorkingMemory;

public class ObjectRetractedEventImpl  extends WorkingMemoryEventImpl implements ObjectRetractedEvent {
    private FactHandle factHandle;
    private Object oldbOject;
    
    public ObjectRetractedEventImpl( org.drools.event.ObjectRetractedEvent event) {
        super( (WorkingMemory) new StatefulKnowledgeSessionImpl( (ReteooStatefulSession) event.getWorkingMemory() ), event.getPropagationContext() );
        factHandle = event.getFactHandle();
        oldbOject = event.getOldObject();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( factHandle );
        out.writeObject( oldbOject );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.factHandle = ( FactHandle ) in.readObject();
        this.oldbOject = in.readObject();
    }    
    
    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    public Object getOldObject() {
        return this.oldbOject;
    }
}
