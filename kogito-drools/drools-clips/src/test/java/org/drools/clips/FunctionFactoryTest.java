package org.drools.clips;

import junit.framework.TestCase;

public class FunctionFactoryTest extends TestCase {
    public void testInit() {
        XFunctionRegistry factory = new XFunctionRegistry( BuiltinFunctions.getInstance() );
        
        // make sure some core functions are there
        assertSame( "+", factory.getFunction( "+" ).getName() );
        assertSame( "bind" , factory.getFunction( "bind" ).getName() );
        assertSame( "modify" , factory.getFunction( "modify" ).getName() );
    }
}
