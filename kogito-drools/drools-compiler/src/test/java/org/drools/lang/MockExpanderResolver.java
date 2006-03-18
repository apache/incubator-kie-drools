package org.drools.lang;

import java.util.HashMap;
import java.util.Map;

public class MockExpanderResolver
    implements
    ExpanderResolver {

    private Map resolveCalls = new HashMap();
    private MockExpander expander = new MockExpander();
    
    public Expander get(String name,
                        String config) {
        resolveCalls.put( name, config );
        return expander;
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
    
    public boolean checkExpanded(String patternOriginal) {
        return expander.checkPattern( patternOriginal );
    }
}
