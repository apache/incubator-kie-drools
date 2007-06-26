package org.drools.brms.server.rules;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.Person;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

import junit.framework.TestCase;

public class SuggestionCompletionLoaderTest extends TestCase {    

    public void testSuggestionCompLoader() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person", new ArrayList(), new ArrayList() );
        assertNotNull(eng);
        
    }
    
    public void testLoaderWithExistingClassloader() throws Exception {
        MockClassLoader mcl = new MockClassLoader();
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader(mcl);
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.foo.Bar", new ArrayList(), new ArrayList() );
        assertNotNull(eng);
        assertTrue(mcl.called);
        
    }
    
    static class MockClassLoader extends ClassLoader {
        
        public boolean called = false;
        
        public Class loadClass(String name) {
            called = true;
            return Object.class;
        }
        
    }    
    
}
