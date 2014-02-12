package org.drools.compiler.rule.builder.dialect.mvel;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.spi.DeclarationScopeResolver;

public class InstrumentedBuildContent extends RuleBuildContext {
    private DeclarationScopeResolver declarationScopeResolver;

    public InstrumentedBuildContent(final KnowledgeBuilderImpl pkgBuilder,
                                    final RuleDescr ruleDescr,
                                    final DialectCompiletimeRegistry registry,
                                    final InternalKnowledgePackage pkg,
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
