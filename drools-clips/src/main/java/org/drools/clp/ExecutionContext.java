package org.drools.clp;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;

public class ExecutionContext {
    private InternalWorkingMemory workingMemory;
    private ReteTuple             tuple;
    private Object                object;
    private ValueHandler[]        localVariables;

    public ExecutionContext(InternalWorkingMemory workingMemory,
                            ReteTuple tuple,
                            int localVariableSize) {
        this( workingMemory,
              tuple,
              null,
              localVariableSize );
    }

    public ExecutionContext(InternalWorkingMemory workingMemory,
                            ReteTuple tuple,
                            Object object,
                            int localVariableSize) {
        this.workingMemory = workingMemory;
        this.tuple = tuple;
        this.object = object;
        this.localVariables = new ValueHandler[localVariableSize];
    }

    public ReteTuple getTuple() {
        return tuple;
    }

    public InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    public Object getObject() {
        return this.object;
    }

    public ValueHandler getLocalVariable(int index) {
        return localVariables[index];
    }

    public void setLocalVariable(int index,
                                 ValueHandler valueHandler) {
        this.localVariables[index] = valueHandler;
    }

}
