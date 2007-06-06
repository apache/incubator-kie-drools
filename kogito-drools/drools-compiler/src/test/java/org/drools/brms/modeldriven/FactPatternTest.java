package org.drools.brms.modeldriven;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.brxml.CompositeFieldConstraint;
import org.drools.brms.client.modeldriven.brxml.SingleFieldConstraint;
import org.drools.brms.client.modeldriven.brxml.FactPattern;

public class FactPatternTest extends TestCase {

    public void testAddConstraint() {
        final FactPattern p = new FactPattern();
        final SingleFieldConstraint x = new SingleFieldConstraint( "x" );
        p.addConstraint( x );

        assertEquals( 1,
                      p.constraintList.constraints.length );
        assertEquals( x,
                      p.constraintList.constraints[0] );

        final SingleFieldConstraint y = new SingleFieldConstraint( "y" );

        p.addConstraint( y );
        assertEquals( 2,
                      p.constraintList.constraints.length );
        assertEquals( x,
                      p.constraintList.constraints[0] );
        assertEquals( y,
                      p.constraintList.constraints[1] );

    }
    
    public void testWithCompositeNesting() {
        final FactPattern p = new FactPattern();
        final SingleFieldConstraint x = new SingleFieldConstraint( "x" );
        p.addConstraint( x );

        assertEquals( 1,
                      p.constraintList.constraints.length );
        assertEquals( x,
                      p.constraintList.constraints[0] );

        final CompositeFieldConstraint y = new CompositeFieldConstraint();

        y.addConstraint( new SingleFieldConstraint("y") );
        y.addConstraint( new SingleFieldConstraint("z") );        
        p.addConstraint( y );
        
        assertEquals( 2,
                      p.constraintList.constraints.length );
        assertEquals( x,
                      p.constraintList.constraints[0] );
        assertEquals( y,
                      p.constraintList.constraints[1] );     
        
       
        
    }

    public void testRemoveConstraint() {
        final FactPattern p = new FactPattern();
        final SingleFieldConstraint x = new SingleFieldConstraint( "x" );
        p.addConstraint( x );
        final SingleFieldConstraint y = new SingleFieldConstraint( "y" );
        p.addConstraint( y );

        assertEquals( 2,
                      p.constraintList.constraints.length );

        p.removeConstraint( 1 );

        assertEquals( 1,
                      p.constraintList.constraints.length );

        assertEquals( x,
                      p.constraintList.constraints[0] );

        
        
    }
    
    public void testIsBound() {
        FactPattern pat = new FactPattern();
        pat.boundName = "x";
        assertTrue(pat.isBound());
        
        pat = new FactPattern();
        assertFalse(pat.isBound());
    }
    
    public void testGetFieldConstraints() {
        FactPattern pat = new FactPattern();
        assertEquals(0, pat.getFieldConstraints().length);
        assertNull(pat.constraintList);
        
        pat.addConstraint( new SingleFieldConstraint() );
        assertNotNull(pat.constraintList);
        assertEquals(1, pat.getFieldConstraints().length);
    }

}
