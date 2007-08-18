package org.drools.clp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.clp.valuehandlers.IndexedLocalVariableValue;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;

public class BlockExecutionEngine
    implements
    Consequence,
    ExecutionEngine {
    private FunctionCaller[] functions;

    private int              index;

    public void addFunction(FunctionCaller function) {
        if ( this.functions == null ) {
            this.functions = new FunctionCaller[]{function};
        } else {
            FunctionCaller[] temp = new FunctionCaller[functions.length + 1];
            System.arraycopy( this.functions,
                              0,
                              temp,
                              0,
                              this.functions.length );
            temp[temp.length - 1] = function;
            this.functions = temp;
        }
    }

    public FunctionCaller[] getFunctions() {
        return this.functions;
    }

    public void execute(InternalWorkingMemory workingMemory,
                        ReteTuple tuple,
                        Object object) {
        execute( new ExecutionContextImpl( workingMemory,
                                       tuple,
                                       object,
                                       this.index - 1 ) );
    }

    public void execute(InternalWorkingMemory workingMemory,
                        ReteTuple tuple) {
        execute( new ExecutionContextImpl( workingMemory,
                                       tuple,
                                       this.index - 1 ) );
    }

    public void execute(ExecutionContext context) {
        if ( this.functions == null ) {
            return;
        }

        for ( int i = 0, length = functions.length; i < length; i++ ) {
            this.functions[i].getValue( context );
        }
    }

    public void evaluate(KnowledgeHelper knowledgeHelper,
                         WorkingMemory workingMemory) throws Exception {
        ExecutionContext context = new ExecutionContextImpl( (InternalWorkingMemory) workingMemory,
                                                         (ReteTuple) knowledgeHelper.getTuple(),
                                                         this.index );
        execute( context );
    }

    public void replaceTempTokens(Map variables) {
        for ( int i = 0, length = functions.length; i < length; i++ ) {
            this.functions[i].replaceTempTokens( variables );
        }
    }

    public VariableValueHandler createLocalVariable(String identifier) {
        return new IndexedLocalVariableValue( identifier,
                                              this.index++ );
    }

}
