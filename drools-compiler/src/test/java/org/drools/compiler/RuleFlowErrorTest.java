package org.drools.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class RuleFlowErrorTest {

    @Test
    public void testError() {
        ProcessLoadError err = new ProcessLoadError(null, "XXX", null);
        assertEquals("XXX", err.getMessage());
        
        Exception e = new RuntimeException("Q");
        err = new ProcessLoadError(null, "X", e);
        
        assertNotNull(err.getMessage());
        
        assertTrue(err instanceof DroolsError);
        
    }
    
}
