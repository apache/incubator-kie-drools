/**
 * 
 */
package org.drools.rule.builder.dialect.mvel;

import org.drools.compiler.Dialect;
import org.drools.compiler.DialectRegistry;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.DeclarationScopeResolver;

public class InstrumentedBuildContent extends RuleBuildContext {
    private DeclarationScopeResolver declarationScopeResolver;

    public InstrumentedBuildContent(final PackageBuilderConfiguration conf,
                                    final Package pkg,
                                    final RuleDescr ruleDescr,
                                    final DialectRegistry registry,
                                    final Dialect dialect) {
        super( conf, 
               pkg,
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