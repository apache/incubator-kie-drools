package org.drools.core.event.rule.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.common.PropagationContext;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.FactHandle;

public class ObjectDeletedEventImpl extends RuleRuntimeEventImpl implements ObjectDeletedEvent {
    private FactHandle factHandle;
    private Object oldbOject;
    
    public ObjectDeletedEventImpl(final KieRuntime kruntime,
                                   final PropagationContext propagationContext,
                                   final FactHandle handle,
                                   final Object object) {
        super( kruntime, propagationContext );
        this.factHandle = handle;
        this.oldbOject = object;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ObjectDeletedEventImpl() {
        super();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( factHandle );
        out.writeObject( oldbOject );
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.factHandle = ( FactHandle ) in.readObject();
        this.oldbOject = in.readObject();
    }
    
    @Override
    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    @Override
    public Object getOldObject() {
        return this.oldbOject;
    }

    @Override
    public String toString() {
        return "==>[ObjectDeletedEventImpl: getFactHandle()=" + getFactHandle() + ", getOldObject()="
                + getOldObject() + ", getKnowledgeRuntime()=" + getKieRuntime() + ", getPropagationContext()="
                + getPropagationContext() + "]";
    }
}
