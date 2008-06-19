package org.drools.guvnor.modeldriven;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;

public class ConstraintTest extends TestCase {

    public void testAdd() {
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.addNewConnective();

        assertEquals( 1,
                      con.connectives.length );
        assertNotNull( con.connectives[0] );

        con.addNewConnective();

        assertEquals( 2,
                      con.connectives.length );
        assertNotNull( con.connectives[1] );

    }

}
