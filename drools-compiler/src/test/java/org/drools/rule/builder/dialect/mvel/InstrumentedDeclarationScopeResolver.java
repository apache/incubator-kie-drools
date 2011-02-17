/**
 * 
 */
package org.drools.rule.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.spi.DeclarationScopeResolver;

public class InstrumentedDeclarationScopeResolver extends DeclarationScopeResolver {
    private Map declarations;

    public InstrumentedDeclarationScopeResolver() {
        super( new HashMap() );
    }

    public void setDeclarations(final Map map) {
        this.declarations = map;
    }

    @Override
    public Map getDeclarations( Rule rule ) {
        return this.declarations;
    }
    
    @Override
    public Declaration getDeclaration( Rule rule, String name) {
        return ( Declaration ) this.declarations.get( name );
    }
}