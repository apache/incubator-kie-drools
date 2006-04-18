package org.drools.lang.dsl;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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