package org.drools.agent;

import java.util.List;

import junit.framework.TestCase;

public class RuleBaseManagerTest extends TestCase {

    public void testLists() {
        RuleBaseAgent2 mgr = new RuleBaseAgent2();
        String s = "\tfoo.bar\n baz.bar\t whee ";
        List result = mgr.list( s );
        assertEquals(3, result.size());
        assertEquals("foo.bar", result.get( 0 ));
        assertEquals("baz.bar", result.get(1));
        assertEquals("whee", result.get(2));
        
        s = null;
        result = mgr.list( s );
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
}
