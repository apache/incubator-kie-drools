package org.drools.lang.dsl;

import java.io.InputStreamReader;

import org.drools.lang.Expander;
import org.drools.lang.MockExpander;

import junit.framework.TestCase;

public class DefaultExpanderResolverTest extends TestCase {

    public void testResolve() throws Exception {
        DefaultExpanderResolver res = new DefaultExpanderResolver();
        Expander mock1 = new MockExpander();
        res.addExpander( "mock1.dsl", mock1 );
        assertEquals(mock1, res.get( "mock1.dsl", null ));
        res.addExpander( "mock2.dsl", new MockExpander() );
        assertNotSame( mock1, res.get( "mock2.dsl", null) );
        
        MockExpander def = new MockExpander();
        res.addExpander( "*", def);
        assertEquals(def, res.get( "nothing.here", null ));
        
    }
    
    public void testDefault() throws Exception {
        
        InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream( "test.dsl" ));
        
        DefaultExpanderResolver res = new DefaultExpanderResolver(reader);
        DefaultExpander exp = (DefaultExpander) res.get( "oooh", null );
        assertNotNull(exp);
        
        assertEquals("Person(name==\"foo\")", exp.expand( "when", "Person has name of foo" ));
        assertEquals("wild();", exp.expand( "then", "Go wild" ));
        assertEquals("something", exp.expand( "when", "nothing" ));
        assertEquals("something", exp.expand( "then", "nothing" ));        

        
        
    }
    
    public void testFindNone() {
        DefaultExpanderResolver exp = new DefaultExpanderResolver();
        try {
            exp.get( "nothing.dsl", "" );
            fail("should have thrown an exception when it can't find an expander.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
    
}
