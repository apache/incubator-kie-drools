/*
 * Copyright 2006 JBoss Inc
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
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.beliefsystem.abductive.Abductive;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.AbductiveQuery;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryImpl;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.DeclarationScopeResolver;

import java.util.Stack;

/**
 * A context for the current build
 */
public class RuleBuildContext extends PackageBuildContext {

    // current rule
    private RuleImpl rule;

    // a stack for the rule building used
    // for declarations resolution
    private Stack<RuleConditionElement> buildStack;

    // current Rule descriptor
    private RuleDescr ruleDescr;

    // available declarationResolver 
    private DeclarationScopeResolver declarationResolver;

    // a simple counter for patterns
    private int patternId = -1;

    private DroolsCompilerComponentFactory compilerFactory;

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
        this.buildStack = new Stack<RuleConditionElement>();

        this.declarationResolver = new DeclarationScopeResolver(kBuilder.getGlobals(),
                                                                this.buildStack);
        this.declarationResolver.setPackage(pkg);
        this.ruleDescr = ruleDescr;

        if ( ruleDescr instanceof QueryDescr ) {
            AnnotationDescr abductive = ruleDescr.getAnnotation( Abductive.class.getSimpleName() );
            if ( abductive == null ) {
                this.rule = new QueryImpl( ruleDescr.getName() );
            } else {
                this.rule = new AbductiveQuery( ruleDescr.getName(), abductive.getValue( "mode" ) );
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
     * @return
     */
    public RuleImpl getRule() {
        return this.rule;
    }

    /**
     * Returns the current RuleDescriptor
     * @return
     */
    public RuleDescr getRuleDescr() {
        return this.ruleDescr;
    }

    /**
     * Returns the available declarationResolver instance
     * @return
     */
    public DeclarationScopeResolver getDeclarationResolver() {
        return this.declarationResolver;
    }

    /**
     * Sets the available declarationResolver instance
     * @param variables
     */
    public void setDeclarationResolver(final DeclarationScopeResolver variables) {
        this.declarationResolver = variables;
    }

    public int getPatternId() {
        return this.patternId;
    }

    public int getNextPatternId() {
        return ++this.patternId;
    }

    public void setPatternId(final int patternId) {
        this.patternId = patternId;
    }

    public Stack<RuleConditionElement> getBuildStack() {
        return this.buildStack;
    }

    public DroolsCompilerComponentFactory getCompilerFactory() {
        return compilerFactory;
    }

    public void setCompilerFactory(DroolsCompilerComponentFactory compilerFactory) {
        this.compilerFactory = compilerFactory;
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
