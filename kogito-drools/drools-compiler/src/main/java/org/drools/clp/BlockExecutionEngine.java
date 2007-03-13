package org.drools.clp;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.Consequence;
import org.drools.spi.EvalExpression;
import org.drools.spi.FieldValue;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PredicateExpression;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;

public class BlockExecutionEngine
    implements
    Consequence,
    ExecutionEngine {
    //private Map variableMap = new HashMap();

    private Function[] functions;

    private int        index;               

    public void addFunction(Function function) {
        Function[] temp = new Function[functions.length];
        System.arraycopy( this.functions,
                          0,
                          temp,
                          0,
                          this.functions.length - 1 );
        temp[temp.length - 1] = function;
        this.functions = temp;
    } 
    
    public Function[] getFunctions() {
        return this.functions;
    }    

    public int getNextIndex() {
        return  this.index++;
    }    
    
    public void execute(InternalWorkingMemory workingMemory,
                        ReteTuple tuple,
                        Object object) {
        execute( new ExecutionContext( workingMemory,
                                       tuple,
                                       object,
                                       this.index - 1 ) );
    }

    public void execute(InternalWorkingMemory workingMemory,
                        ReteTuple tuple) {
        execute( new ExecutionContext( workingMemory,
                                       tuple,
                                       this.index - 1 ) );
    }

    public void execute(ExecutionContext context) {
        for ( int i = 0, length = functions.length; i < length; i++ ) {
            this.functions[i].getValue( context );
        }
    }

    public void evaluate(KnowledgeHelper knowledgeHelper,
                         WorkingMemory workingMemory) throws Exception {
        ExecutionContext context = new ExecutionContext( (InternalWorkingMemory) workingMemory,
                                                         (ReteTuple) knowledgeHelper.getTuple(),
                                                         this.index -1 );
        execute( context );
    }

    public void getVariableValueHandler(String name) {
        // TODO Auto-generated method stub
        
    }

}
