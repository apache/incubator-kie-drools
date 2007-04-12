package org.drools.brms.modeldriven;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.brxml.Constraint;
import org.drools.brms.client.modeldriven.brxml.FactPattern;

public class FactPatternTest extends TestCase {

    public void testAddConstraint() {
        final FactPattern p = new FactPattern();
        final Constraint x = new Constraint( "x" );
        p.addConstraint( x );

        assertEquals( 1,
                      p.constraints.length );
        assertEquals( x,
                      p.constraints[0] );

        final Constraint y = new Constraint( "y" );

        p.addConstraint( y );
        assertEquals( 2,
                      p.constraints.length );
        assertEquals( x,
                      p.constraints[0] );
        assertEquals( y,
                      p.constraints[1] );

    }

    public void testRemoveConstraint() {
        final FactPattern p = new FactPattern();
        final Constraint x = new Constraint( "x" );
        p.addConstraint( x );
        final Constraint y = new Constraint( "y" );
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
