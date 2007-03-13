package org.drools.clp;

import junit.framework.TestCase;

public class FunctionFactoryTest extends TestCase {
    public void testInit() {
        FunctionFactory factory = FunctionFactory.getInstance();
        assertEquals( 3, factory.getFunctionSize() );
        
        assertSame( AddFunction.class , factory.createFunction( "+" ).getClass() );
        assertSame( BindFunction.class , factory.createFunction( "bind" ).getClass() );
    }
}
