package org.drools.brms.modeldriven;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.brxml.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brxml.FactPattern;

public class CompositeFactPatternTest extends TestCase {

    public void testAddPattern() {
        CompositeFactPattern pat = new CompositeFactPattern();
        FactPattern x = new FactPattern();
        pat.addFactPattern( x  );
        assertEquals(1, pat.patterns.length);
        
        FactPattern y = new FactPattern();
        pat.addFactPattern( y  );
        assertEquals(2, pat.patterns.length);
        assertEquals(x, pat.patterns[0]);
        assertEquals(y, pat.patterns[1]);
    }
    
    
}
