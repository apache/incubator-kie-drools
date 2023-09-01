package org.drools.mvel.compiler.rule.builder.dialect.mvel;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.DeclarationScopeResolver;

import java.util.Map;

public class InstrumentedDeclarationScopeResolver extends DeclarationScopeResolver {
    private Map declarations;

    public void setDeclarations(final Map map) {
        this.declarations = map;
    }

    @Override
    public Map getDeclarations( RuleImpl rule ) {
        return this.declarations;
    }
    
    @Override
    public Declaration getDeclaration( String name) {
        return ( Declaration ) this.declarations.get( name );
    }
}
