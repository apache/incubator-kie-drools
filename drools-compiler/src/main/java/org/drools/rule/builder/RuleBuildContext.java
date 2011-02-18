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

package org.drools.rule.builder;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.drools.base.EnabledBoolean;
import org.drools.compiler.Dialect;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.core.util.DateUtils;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.DeclarationScopeResolver;

/**
 * A context for the current build
 * 
 */
public class RuleBuildContext extends PackageBuildContext {

    // current rule
    private Rule                        rule;

    // a stack for the rule building used
    // for declarations resolution
    private Stack<RuleConditionElement> buildStack;

    // current Rule descriptor
    private RuleDescr                   ruleDescr;

    // available declarationResolver 
    private DeclarationScopeResolver    declarationResolver;

    // a simple counter for patterns
    private int                         patternId = -1;

    /**
     * Default constructor
     */
    public RuleBuildContext(final PackageBuilder pkgBuilder,                                 
                            final RuleDescr ruleDescr,
                            final DialectCompiletimeRegistry dialectCompiletimeRegistry,
                            final Package pkg,
                            final Dialect defaultDialect) {
        this.buildStack = new Stack<RuleConditionElement>();

        this.declarationResolver = new DeclarationScopeResolver( pkgBuilder.getGlobals(),
                                                                 this.buildStack );
        this.declarationResolver.setPackage( pkg );
        this.ruleDescr = ruleDescr;

        if ( ruleDescr instanceof QueryDescr ) {
            this.rule = new Query( ruleDescr.getName() );
        } else {
            this.rule = new Rule( ruleDescr.getName() );
        }
        this.rule.setPackage( pkg.getName() );
        this.rule.setDialect( ruleDescr.getDialect() );
        
        init(pkgBuilder, pkg, ruleDescr, dialectCompiletimeRegistry, defaultDialect, this.rule );
        
        if ( this.rule.getDialect() == null ) {
            this.rule.setDialect( getDialect().getId() );
        }

        Dialect dialect = getDialect();
        if ( dialect != null ) {
            dialect.init( ruleDescr );
        }
        
      
    }

    /**
     * Returns the current Rule being built
     * @return
     */
    public Rule getRule() {
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


}
