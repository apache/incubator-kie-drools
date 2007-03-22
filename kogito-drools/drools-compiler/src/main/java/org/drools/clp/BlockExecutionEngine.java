package org.drools.clp;

import java.util.Map;

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

    private FunctionCaller[] functions;

    private int        index;               

    public void addFunction(FunctionCaller function) {
        if (this.functions == null) {
            this.functions = new FunctionCaller[] { function };
        } else {
            FunctionCaller[] temp =  new FunctionCaller[ functions.length + 1 ];
            System.arraycopy( this.functions, 0, temp, 0, this.functions.length );
            temp[ temp.length - 1] = function;
            this.functions = temp;             
        }                     
    } 
    
    public FunctionCaller[] getFunctions() {
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
        if ( this.functions == null ) {
            return;
        }
        
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

    public void replaceTempTokens(Map variables) {
        for ( int i = 0, length = functions.length; i < length; i++ ) {
            this.functions[i].replaceTempTokens( variables );
        }
    }

}
