package org.drools.compiler.rule.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Declaration;
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
    public Map getDeclarations( RuleImpl rule ) {
        return this.declarations;
    }
    
    @Override
    public Declaration getDeclaration( RuleImpl rule, String name) {
        return ( Declaration ) this.declarations.get( name );
    }
}
