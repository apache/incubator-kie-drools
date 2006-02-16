package org.drools.lang;

import junit.framework.TestCase;

public class ExpanderContextTest extends TestCase {

    public void testExpander() {
        ExpanderContext ctx = ExpanderContext.getInstance();
        ctx.registerExpander(new MockExpander("mock1"), "mock1");
        MockExpander exp = (MockExpander) ctx.getExpander("mock1");
        assertEquals("mock1", exp.returnVal);
        
        //test re-register
        ctx.registerExpander(new MockExpander("mock2"), "mock1");
        exp = (MockExpander) ctx.getExpander("mock1");
        assertEquals("mock2", exp.returnVal);
        
    }
    
    static class MockExpander implements Expander {

        String returnVal;
        
        MockExpander(String val) {
            returnVal = val;
        }
        
        public String expand(String pattern,
                             Parser context) {
            
            return null;
        }
        
    }
    
}
