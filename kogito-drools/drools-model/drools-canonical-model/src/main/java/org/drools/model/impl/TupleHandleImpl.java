package org.drools.model.impl;

import org.drools.model.Tuple;
import org.drools.model.TupleHandle;
import org.drools.model.Variable;

public class TupleHandleImpl implements TupleHandle {

    private final Tuple parent;
    private final Object object;
    private final int size;
    private Variable variable;

    public TupleHandleImpl(Object object) {
        this(null, object, null);
    }

    public TupleHandleImpl(Object object, Variable variable) {
        this(null, object, variable);
    }

    public TupleHandleImpl(Tuple parent, Object object, Variable variable) {
        this.parent = parent;
        this.object = object;
        this.variable = variable;
        this.size = parent == null ? 1 : parent.size()+1;
    }

    @Override
    public Tuple getParent() {
        return parent;
    }

    @Override
    public <T> T get(Variable<T> variable) {
        if (this.variable != null && this.variable.equals(variable)) {
            return (T) object;
        }
        return parent == null ? null : parent.get(variable);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Object getObject() {
        return object;
    }
}
