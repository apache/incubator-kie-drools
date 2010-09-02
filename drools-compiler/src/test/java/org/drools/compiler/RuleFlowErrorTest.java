package org.drools.compiler;

import junit.framework.TestCase;


public class RuleFlowErrorTest extends TestCase {

    public void testError() {
        ProcessLoadError err = new ProcessLoadError("XXX", null);
        assertEquals("XXX", err.getMessage());
        
        Exception e = new RuntimeException("Q");
        err = new ProcessLoadError("X", e);
        
        assertNotNull(err.getMessage());
        
        assertTrue(err instanceof DroolsError);
        
    }
    
}
