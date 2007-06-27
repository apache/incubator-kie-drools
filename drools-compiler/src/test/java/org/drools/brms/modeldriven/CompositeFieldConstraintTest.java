package org.drools.brms.modeldriven;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.brxml.CompositeFieldConstraint;
import org.drools.brms.client.modeldriven.brxml.SingleFieldConstraint;

public class CompositeFieldConstraintTest extends TestCase {

    public void testCompositeType() {
        CompositeFieldConstraint con = new CompositeFieldConstraint();
        assertEquals(null, con.compositeJunctionType);
    }
    
    public void testAddConstraint() {
        final CompositeFieldConstraint p = new CompositeFieldConstraint();
        final SingleFieldConstraint x = new SingleFieldConstraint( "x" );
        p.addConstraint( x );

        assertEquals( 1,
                      p.constraints.length );
        assertEquals( x,
                      p.constraints[0] );

        final SingleFieldConstraint y = new SingleFieldConstraint( "y" );

        p.addConstraint( y );
        assertEquals( 2,
                      p.constraints.length );
        assertEquals( x,
                      p.constraints[0] );
        assertEquals( y,
                      p.constraints[1] );

    }

    public void testRemoveConstraint() {
        final CompositeFieldConstraint p = new CompositeFieldConstraint();
        final SingleFieldConstraint x = new SingleFieldConstraint( "x" );
        p.addConstraint( x );
        final CompositeFieldConstraint y = new CompositeFieldConstraint(  );
        p.addConstraint( y );

        assertEquals( 2,
                      p.constraints.length );

        p.removeConstraint( 1 );

        assertEquals( 1,
                      p.constraints.length );

        assertEquals( x,
                      p.constraints[0] );

    }
    
    
    
}
