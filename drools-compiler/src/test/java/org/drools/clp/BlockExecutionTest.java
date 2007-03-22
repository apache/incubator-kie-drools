package org.drools.clp;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.clp.functions.AddFunction;
import org.drools.clp.functions.BindFunction;
import org.drools.clp.functions.ModifyFunction;

public class BlockExecutionTest extends TestCase {
    public void test1() {
        BlockExecutionEngine engine = new BlockExecutionEngine();        
        ExecutionBuildContext build = new ExecutionBuildContext(engine);                
        
        FunctionCaller addCaller = new FunctionCaller( new AddFunction() );
        addCaller.addParameter( new ObjectLiteralValue( new BigDecimal( 20) ) );
        addCaller.addParameter( new LongLiteralValue( "11" ) );
                
        FunctionCaller bindCaller = new FunctionCaller( new BindFunction() );
        bindCaller.addParameter( build.createLocalVariable( "?x" ) );
        bindCaller.addParameter( addCaller );
        
        engine.addFunction( bindCaller );
        
        
        FunctionCaller modifyCaller = new FunctionCaller( new ModifyFunction() );        
        build.createLocalVariable( "?p" );        
        modifyCaller.addParameter( build.getVariableValueHandler( "?p" ) );
        modifyCaller.addParameter( new SlotNameValuePair("age", build.getVariableValueHandler( "?x" )) );
        
        ExecutionContext context = new ExecutionContext(null, null, 2);
        Person p = new Person("mark");
        context.setLocalVariable( 1, p );
        
        engine.addFunction( modifyCaller );
        
        engine.execute( context );
        
        assertEquals( 31, p.getAge() );
        
        
    }
}
