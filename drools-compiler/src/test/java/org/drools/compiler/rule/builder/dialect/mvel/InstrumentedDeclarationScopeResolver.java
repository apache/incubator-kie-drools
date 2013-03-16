package org.drools.compiler.rule.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.rule.Declaration;
import org.drools.core.rule.Rule;
import org.drools.core.spi.DeclarationScopeResolver;

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
