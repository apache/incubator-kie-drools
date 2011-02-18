package org.drools.rule.builder.dialect.mvel;

import org.drools.compiler.Dialect;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.DeclarationScopeResolver;

public class InstrumentedBuildContent extends RuleBuildContext {
    private DeclarationScopeResolver declarationScopeResolver;

    public InstrumentedBuildContent(final PackageBuilder pkgBuilder,
                                    final RuleDescr ruleDescr,
                                    final DialectCompiletimeRegistry registry,
                                    final Package pkg,                                    
                                    final Dialect dialect) {
        super( pkgBuilder, 
               ruleDescr,
               registry,
               pkg,               
               dialect );
    }

    public void setDeclarationResolver(final DeclarationScopeResolver declarationScopeResolver) {
        this.declarationScopeResolver = declarationScopeResolver;
    }

    public DeclarationScopeResolver getDeclarationResolver() {
        return this.declarationScopeResolver;
    }

}
