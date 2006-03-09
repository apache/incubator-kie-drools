package org.drools.lang;

import java.util.HashMap;
import java.util.Map;

public class MockExpanderResolver
    implements
    ExpanderResolver {

    private Map resolveCalls = new HashMap();
    
    public Expander get(String name,
                        String config) {
        resolveCalls.put( name, config );
        return new MockExpander();
    }

    /**
     * Check if it was called.
     */
    public boolean checkCalled(String name) {
        return resolveCalls.containsKey( name );
    }
    
    public String getConfigFor(String name) {
        return (String) resolveCalls.get( name );
    }
}
