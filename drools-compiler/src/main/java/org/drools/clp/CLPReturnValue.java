/**
 * 
 */
package org.drools.clp;

import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.base.FieldFactory;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.FieldValue;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;

public class CLPReturnValue
    implements
    ReturnValueExpression,
    ExecutionEngine {
    private FunctionCaller function;
    private int index;
    
    public CLPReturnValue() {
        
    }
    
    public CLPReturnValue(FunctionCaller function) {
        this.function = function;
    }

    public void setFunction(FunctionCaller function) {
        this.function = function;
    }
    
    public void addFunction(FunctionCaller function) {
        setFunction( function );        
    }  
    
    public FunctionCaller[] getFunctions() {
        return new FunctionCaller[] { this.function };
    }

    public int getNextIndex() {
        return  this.index++;
    }
    
    public FieldValue evaluate(Object object,
                               Tuple tuple,
                               Declaration[] previousDeclarations,
                               Declaration[] localDeclarations,
                               WorkingMemory workingMemory) throws Exception {
        ExecutionContext context = new ExecutionContext( (InternalWorkingMemory) workingMemory,
                                                         (ReteTuple) tuple,
                                                         object,
                                                         this.index-1 );
        return FieldFactory.getFieldValue( this.function.getBooleanValue( context ) );
    }
    
    public void replaceTempTokens(Map variables) {
        this.function.replaceTempTokens( variables );
    }    
}