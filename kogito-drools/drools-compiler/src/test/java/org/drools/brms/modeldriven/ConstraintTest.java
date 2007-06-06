package org.drools.brms.modeldriven;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.brxml.SingleFieldConstraint;

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
