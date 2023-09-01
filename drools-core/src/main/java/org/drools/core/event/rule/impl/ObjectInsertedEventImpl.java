package org.drools.core.event.rule.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.common.PropagationContext;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.FactHandle;

public class ObjectInsertedEventImpl extends RuleRuntimeEventImpl
    implements
    ObjectInsertedEvent {
    private FactHandle  factHandle;
    private Object      object;

    public ObjectInsertedEventImpl(final KieRuntime kruntime,
                               final PropagationContext propagationContext,
                               final FactHandle handle,
                               final Object object) {
        super( kruntime, propagationContext );
        this.factHandle = handle;
        this.object = object;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ObjectInsertedEventImpl() {
        super();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( factHandle );
        out.writeObject( object );
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.factHandle = ( FactHandle ) in.readObject();
        this.object = in.readObject();
    }

    @Override
    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    @Override
    public Object getObject() {
        return this.object;
    }

    @Override
    public String toString() {
        return "==>[ObjectInsertedEventImpl: getFactHandle()=" + getFactHandle() + ", getObject()=" + getObject()
                + ", getKnowledgeRuntime()=" + getKieRuntime() + ", getPropagationContext()="
                + getPropagationContext() + "]";
    }
}
