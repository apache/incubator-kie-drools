package org.drools.clp;

import org.drools.Person;
import org.drools.clp.functions.ModifyFunction;

import junit.framework.TestCase;

public class ModifyFunctionTest extends TestCase {
    public void test1() {
        Function function = new ModifyFunction();
        
       Person p = new Person("mark");
       ExecutionContext context = new ExecutionContext(null, null, 1);
       //context.setLocalVariable( 0, p );
       
       LocalVariableValue var = new LocalVariableValue("p", 0);
       var.setValue( context, new ObjectValueHandler( p ) );
       
       ListValueHandler list = new ListValueHandler();
       list.add( new ObjectValueHandler( "name") );
       list.add( new ObjectValueHandler( "bob") );
       
       
       function.execute( new ValueHandler[] { var, list }, context );
       
       assertEquals( "bob", p.getName() );
    }
}
