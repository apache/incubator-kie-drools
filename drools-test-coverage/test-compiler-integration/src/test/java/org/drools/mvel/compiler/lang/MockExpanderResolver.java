package org.drools.mvel.compiler.lang;

import java.util.HashMap;
import java.util.Map;

import org.drools.drl.parser.lang.Expander;
import org.drools.drl.parser.lang.ExpanderResolver;

public class MockExpanderResolver
    implements
        ExpanderResolver {

    private final Map          resolveCalls = new HashMap();
    private final MockExpander expander     = new MockExpander();

    public Expander get( final String name,
                         final String config) {
        this.resolveCalls.put( name,
                               config );
        return this.expander;
    }

    /**
     * Check if it was called.
     */
    public boolean checkCalled(final String name) {
        return this.resolveCalls.containsKey( name );
    }

    public String getConfigFor(final String name) {
        return (String) this.resolveCalls.get( name );
    }

    public boolean checkExpanded(final String patternOriginal) {
        return this.expander.checkPattern( patternOriginal );
    }
}
