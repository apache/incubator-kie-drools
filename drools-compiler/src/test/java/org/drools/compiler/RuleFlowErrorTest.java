package org.drools.compiler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


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
