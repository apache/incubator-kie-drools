package org.drools.compiler;

import junit.framework.TestCase;


public class RuleFlowErrorTest extends TestCase {

    public void testError() {
        RuleFlowLoadError err = new RuleFlowLoadError("XXX", null);
        assertEquals("XXX", err.getMessage());
        
        Exception e = new RuntimeException("Q");
        err = new RuleFlowLoadError("X", e);
        
        assertNotNull(err.getMessage());
        
        assertTrue(err instanceof DroolsError);
        
    }
    
}
