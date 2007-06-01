/**
 * 
 */
package org.drools.rule.builder.dialect.mvel;

import org.drools.compiler.DialectRegistry;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.Dialect;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.DeclarationScopeResolver;

public class InstrumentedBuildContent extends RuleBuildContext {
    private DeclarationScopeResolver declarationScopeResolver;

    public InstrumentedBuildContent(final Package pkg,
                                    final RuleDescr ruleDescr,
                                    final DialectRegistry registry,
                                    final Dialect dialect) {
        super( pkg,
               ruleDescr,
               registry,
               dialect );
    }

    public void setDeclarationResolver(final DeclarationScopeResolver declarationScopeResolver) {
        this.declarationScopeResolver = declarationScopeResolver;
    }

    public DeclarationScopeResolver getDeclarationResolver() {
        return this.declarationScopeResolver;
    }

}