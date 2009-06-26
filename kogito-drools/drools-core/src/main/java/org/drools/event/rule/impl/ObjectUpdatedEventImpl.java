package org.drools.event.rule.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalWorkingMemory;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.FactHandle;
import org.drools.runtime.rule.WorkingMemory;

public class ObjectUpdatedEventImpl  extends WorkingMemoryEventImpl implements ObjectUpdatedEvent {
    private FactHandle  factHandle;
    private Object      object;
    private Object      oldObject;
    
    public ObjectUpdatedEventImpl(org.drools.event.ObjectUpdatedEvent event) {
        super( ((InternalWorkingMemory) event.getWorkingMemory() ).getKnowledgeRuntime(), event.getPropagationContext() );
        factHandle = event.getFactHandle();
        object = event.getObject();
        oldObject = event.getOldObject();
    }    
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( factHandle );
        out.writeObject( object );
        out.writeObject( oldObject );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.factHandle = ( FactHandle ) in.readObject();
        this.object = in.readObject();
        this.oldObject = in.readObject();
    }      
    
    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    public Object getObject() {
        return this.object;
    }

    public Object getOldObject() {
        return this.oldObject;
    }

}
