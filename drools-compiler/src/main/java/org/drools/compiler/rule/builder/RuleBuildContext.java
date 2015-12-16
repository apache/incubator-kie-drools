/*
 * Copyright 2006 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.rule.builder;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.beliefsystem.abductive.Abductive;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.AbductiveQuery;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryImpl;
import org.drools.core.spi.DeclarationScopeResolver;

/**
 * A context for the current build
 */
public class RuleBuildContext extends PackageBuildContext {

    // current rule
    private final RuleImpl rule;

    // current Rule descriptor
    private final RuleDescr ruleDescr;

    // available declarationResolver 
    private DeclarationScopeResolver declarationResolver;

    // a simple counter for patterns
    private int patternId = -1;

    private final DroolsCompilerComponentFactory compilerFactory;

    private boolean needStreamMode = false;

    private Pattern prefixPattern;

    /**
     * Default constructor
     */
    public RuleBuildContext(final KnowledgeBuilderImpl kBuilder,
                            final RuleDescr ruleDescr,
                            final DialectCompiletimeRegistry dialectCompiletimeRegistry,
                            final InternalKnowledgePackage pkg,
                            final Dialect defaultDialect) {
        this.declarationResolver = new DeclarationScopeResolver( kBuilder.getGlobals(), pkg );
        this.ruleDescr = ruleDescr;

        if ( ruleDescr instanceof QueryDescr ) {
            Abductive abductive = ruleDescr.getTypedAnnotation( Abductive.class );
            if ( abductive == null ) {
                this.rule = new QueryImpl( ruleDescr.getName() );
            } else {
                this.rule = new AbductiveQuery( ruleDescr.getName(), abductive.mode() );
            }
        } else {
            this.rule = new RuleImpl(ruleDescr.getName());
        }
        this.rule.setPackage(pkg.getName());
        this.rule.setDialect(ruleDescr.getDialect());
        this.rule.setLoadOrder( ruleDescr.getLoadOrder() );

        init(kBuilder, pkg, ruleDescr, dialectCompiletimeRegistry, defaultDialect, this.rule);

        if (this.rule.getDialect() == null) {
            this.rule.setDialect(getDialect().getId());
        }

        Dialect dialect = getDialect();
        if (dialect != null ) {
            dialect.init( ruleDescr );
        }

        compilerFactory = kBuilder.getBuilderConfiguration().getComponentFactory();
    }

    /**
     * Returns the current Rule being built
     */
    public RuleImpl getRule() {
        return this.rule;
    }

    /**
     * Returns the current RuleDescriptor
     */
    public RuleDescr getRuleDescr() {
        return this.ruleDescr;
    }

    /**
     * Returns the available declarationResolver instance
     */
    public DeclarationScopeResolver getDeclarationResolver() {
        return this.declarationResolver;
    }

    /**
     * Sets the available declarationResolver instance
     */
    public void setDeclarationResolver(final DeclarationScopeResolver declarationResolver) {
        this.declarationResolver = declarationResolver;
    }

    public int getNextPatternId() {
        return ++this.patternId;
    }

    public DroolsCompilerComponentFactory getCompilerFactory() {
        return compilerFactory;
    }

    public boolean needsStreamMode() {
        return needStreamMode;
    }

    public void setNeedStreamMode() {
        this.needStreamMode = true;
    }

    public void setPrefixPattern(Pattern prefixPattern) {
        this.prefixPattern = prefixPattern;
    }

    public Pattern getPrefixPattern() {
        return prefixPattern;
    }
}
