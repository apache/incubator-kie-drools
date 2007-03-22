package org.drools.clp;

import junit.framework.TestCase;

public class FunctionFactoryTest extends TestCase {
    public void testInit() {
        FunctionRegistry factory = new FunctionRegistry( BuiltinFunctions.getInstance() );
        assertEquals( 3, factory.getFunctionSize() );
        
        assertSame( "+", factory.getFunction( "+" ).getName() );
        assertSame( "bind" , factory.getFunction( "bind" ).getName() );
    }
}
