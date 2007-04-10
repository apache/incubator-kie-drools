package org.drools.brms.modeldriven;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.ConnectiveConstraint;

import junit.framework.TestCase;

public class ConnectiveConstraintTest extends TestCase {

    public void testConnectiveType() {
        ConnectiveConstraint con = new ConnectiveConstraint("| =", "x");
        assertTrue(con.isORConnective());
        assertFalse(con.isANDConnective());

        con = new ConnectiveConstraint("||<", "x");
        assertTrue(con.isORConnective());
        assertFalse(con.isANDConnective());

        con = new ConnectiveConstraint("||<", "x");
        assertTrue(con.isORConnective());
        assertFalse(con.isANDConnective());

        con = new ConnectiveConstraint("& !=", "x");
        assertFalse(con.isORConnective());
        assertTrue(con.isANDConnective());

        con = new ConnectiveConstraint("&& !=", "x");
        assertFalse(con.isORConnective());
        assertTrue(con.isANDConnective());
        
    }
    
}
