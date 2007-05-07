package org.drools.clp;

import java.math.BigDecimal;

import org.drools.clp.functions.PlusFunction;
import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.clp.valuehandlers.IndexedLocalVariableValue;
import org.drools.clp.valuehandlers.LongValueHandler;

import junit.framework.TestCase;

public class DeffunctionTest extends TestCase {
    public void test1() {
        Deffunction function = new Deffunction("x");
         
        ValueHandler a = function.addParameter( "a" );        
        ValueHandler b =function.addParameter( "b" );
        
        Function add = new PlusFunction();
        FunctionCaller caller = new FunctionCaller(add);
        caller.addParameter( a );
        caller.addParameter( b );
                
        function.addFunction( caller );
                
        ExecutionContext context = new ExecutionContext(null, null, 2);
        ValueHandler q = new IndexedLocalVariableValue("q", 0);
        q.setValue( context, new LongValueHandler( 10 ) );
        
        ValueHandler w = new IndexedLocalVariableValue("w", 1);
        w.setValue( context,  new LongValueHandler( 7 ) );
        
        assertEquals( new BigDecimal( 17 ), function.execute( new ValueHandler[] { q, w }, context ).getBigDecimalValue( context ) );
    }      
}
