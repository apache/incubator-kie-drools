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

import java.util.Optional;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.base.TypeResolver;
import org.drools.core.beliefsystem.abductive.Abductive;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.AbductiveQuery;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryImpl;
import org.drools.core.spi.DeclarationScopeResolver;
import org.kie.api.runtime.rule.RuleUnit;

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

    private boolean inXpath;

    /**
     * Default constructor
     */
    public RuleBuildContext(final KnowledgeBuilderImpl kBuilder,
                            final RuleDescr ruleDescr,
                            final DialectCompiletimeRegistry dialectCompiletimeRegistry,
                            final InternalKnowledgePackage pkg,
                            final Dialect defaultDialect) {
        this.ruleDescr = ruleDescr;

        if ( ruleDescr instanceof QueryDescr ) {
            Abductive abductive = ruleDescr.getTypedAnnotation( Abductive.class );
            if ( abductive == null ) {
                this.rule = new QueryImpl( ruleDescr.getName() );
            } else {
                this.rule = new AbductiveQuery( ruleDescr.getName(), abductive.mode() );
            }
        } else {
            this.rule = ruleDescr.toRule();
        }
        this.rule.setPackage(pkg.getName());
        this.rule.setDialect(ruleDescr.getDialect());
        this.rule.setLoadOrder( ruleDescr.getLoadOrder() );

        init(kBuilder, pkg, ruleDescr, dialectCompiletimeRegistry, defaultDialect, this.rule);

        if (this.rule.getDialect() == null) {
            this.rule.setDialect(getDialect().getId());
        }

        if (ruleDescr.getUnit() != null) {
            rule.setRuleUnitClassName( pkg.getName() + "." + ruleDescr.getUnit().getTarget() );
        }

        Dialect dialect = getDialect();
        if (dialect != null ) {
            dialect.init( ruleDescr );
        }

        this.compilerFactory = kBuilder.getBuilderConfiguration().getComponentFactory();
        this.declarationResolver = new DeclarationScopeResolver( kBuilder.getGlobals(), getPkg() );
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

    public boolean isInXpath() {
        return inXpath;
    }

    public void setInXpath( boolean inXpath ) {
        this.inXpath = inXpath;
    }

    public void initRule() {
        initRuleUnitClassName();
        declarationResolver.setRule( rule );
    }

    @Override
    public Class< ? > resolveVarType(String identifier) {
        return getDeclarationResolver().resolveVarType( identifier );
    }

    private void initRuleUnitClassName() {
        String ruleUnitClassName = rule.getRuleUnitClassName();
        boolean nameInferredFromResource = false;

        if ( ruleUnitClassName == null && rule.getResource() != null && rule.getResource().getSourcePath() != null ) {
            String drlPath = rule.getResource().getSourcePath();
            int lastSep = drlPath.lastIndexOf( '/' );
            if (lastSep >= 0) {
                drlPath = drlPath.substring( lastSep+1 );
            }
            ruleUnitClassName = rule.getPackage() + "." + drlPath.substring( 0, drlPath.lastIndexOf( '.' ) ).replace( '/', '.' );
            nameInferredFromResource = true;
        }

        if (ruleUnitClassName != null) {
            TypeResolver typeResolver = getPkg().getTypeResolver();
            boolean unitFound = false;
            try {
                unitFound = RuleUnit.class.isAssignableFrom( typeResolver.resolveType( ruleUnitClassName ) );
                if (unitFound && nameInferredFromResource) {
                    rule.setRuleUnitClassName( ruleUnitClassName );
                }
            } catch (ClassNotFoundException e) {
                // ignore
            }
            if (!unitFound && !nameInferredFromResource) {
                addError( new RuleBuildError( rule, getParentDescr(), null,
                                              ruleUnitClassName + " is not a valid RuleUnit class name" ) );
            }
        }
    }

    public Optional<EntryPointId> getEntryPointId(String name) {
        return getPkg().getRuleUnitRegistry().getRuleUnitFor( getRule() ).flatMap( ruDescr -> ruDescr.getEntryPointId(name) );
    }
}
