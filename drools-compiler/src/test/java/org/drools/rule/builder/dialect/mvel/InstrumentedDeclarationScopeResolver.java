/**
 * 
 */
package org.drools.rule.builder.dialect.mvel;

import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.spi.DeclarationScopeResolver;

public class InstrumentedDeclarationScopeResolver extends DeclarationScopeResolver {
    private Map declarations;

    public InstrumentedDeclarationScopeResolver() {
        super( null );
    }

    public void setDeclarations(final Map map) {
        this.declarations = map;
    }

    public Map getDeclarations() {
        return this.declarations;
    }
    
    public Declaration getDeclaration(final String name) {
        return ( Declaration ) this.declarations.get( name );
    }
}