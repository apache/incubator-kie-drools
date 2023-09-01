package org.drools.base.reteoo;

import org.kie.api.runtime.rule.FactHandle;

public class AccumulateContextEntry {
    private Object key;
    private FactHandle resultFactHandle;
    private BaseTuple resultLeftTuple;
    private boolean propagated;
    private Object functionContext;
    private boolean toPropagate;
    private boolean empty = true;

    public AccumulateContextEntry(Object key) {
        this.key = key;
    }

    public FactHandle getResultFactHandle() {
        return resultFactHandle;
    }

    public void setResultFactHandle(FactHandle resultFactHandle) {
        this.resultFactHandle = resultFactHandle;
    }

    public BaseTuple getResultLeftTuple() {
        return resultLeftTuple;
    }

    public void setResultLeftTuple(BaseTuple resultLeftTuple) {
        this.resultLeftTuple = resultLeftTuple;
    }

    public boolean isPropagated() {
        return propagated;
    }

    public void setPropagated(boolean propagated) {
        this.propagated = propagated;
    }

    public boolean isToPropagate() {
        return toPropagate;
    }

    public void setToPropagate(boolean toPropagate) {
        this.toPropagate = toPropagate;
    }

    public Object getFunctionContext() {
        return functionContext;
    }

    public void setFunctionContext(Object context) {
        this.functionContext = context;
    }

    public Object getKey() {
        return this.key;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
