/**
 * 
 */
package org.drools.clp;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;

public class CLPEval
    implements
    EvalExpression,
    ExecutionEngine {
    private Function function;
    private int      index;
    
    public CLPEval() {
        
    }
    
    public CLPEval(Function function) {
        this.function = function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public void addFunction(Function function) {
        setFunction( function );        
    }
    
    public int getNextIndex() {
        return  this.index++;
    }    
    
    public boolean evaluate(Tuple tuple,
                            Declaration[] requiredDeclarations,
                            WorkingMemory workingMemory) throws Exception {
        ExecutionContext context = new ExecutionContext( (InternalWorkingMemory) workingMemory,
                                                         (ReteTuple) tuple,
                                                         this.index-1 );
        return this.function.getBooleanValue( context );
    }

}