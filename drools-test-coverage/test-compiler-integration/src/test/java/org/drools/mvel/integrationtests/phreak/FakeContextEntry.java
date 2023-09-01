package org.drools.mvel.integrationtests.phreak;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.base.ValueResolver;
import org.drools.core.common.InternalFactHandle;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.ContextEntry;
import org.kie.api.runtime.rule.FactHandle;

public class FakeContextEntry implements ContextEntry {

    private BaseTuple tuple;
    private FactHandle handle;

    private transient ValueResolver valueResolver;

    public void updateFromTuple(ValueResolver valueResolver, BaseTuple tuple) {
        this.tuple = tuple;
        this.valueResolver = valueResolver;
    }

    public void updateFromFactHandle(ValueResolver valueResolver, FactHandle handle) {
        this.valueResolver = valueResolver;
        this.handle = handle;
    }

    public void resetTuple() {
        tuple = null;
    }

    public void resetFactHandle() {
        valueResolver = null;
        handle = null;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(tuple);
        out.writeObject(handle);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tuple = (BaseTuple) in.readObject();
        handle = (InternalFactHandle) in.readObject();
    }

    public BaseTuple getTuple() {
        return tuple;
    }

    public FactHandle getHandle() {
        return handle;
    }

    public ContextEntry getNext() {
        throw new UnsupportedOperationException();
    }

    public void setNext(final ContextEntry entry) {
        throw new UnsupportedOperationException();
    }

    public ValueResolver getValueResolver() {
        return this.valueResolver;
    }
}
