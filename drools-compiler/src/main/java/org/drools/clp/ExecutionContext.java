package org.drools.clp;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;

public class ExecutionContext {
    private InternalWorkingMemory workingMemory;
    private ReteTuple             tuple;
    private Object                object;
    private Object[]              localVariables;

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
        this.localVariables = new Object[localVariableSize];
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

    public Object getLocalVariable(int index) {
        return localVariables[index];
    }

    public void setLocalVariable(int index,
                                 Object object) {
        this.localVariables[index] = object;
    }

}
