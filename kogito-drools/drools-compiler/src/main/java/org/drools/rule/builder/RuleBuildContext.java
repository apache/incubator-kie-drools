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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.drools.compiler.Dialect;
import org.drools.compiler.DialectRegistry;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.DeclarationScopeResolver;
import org.drools.util.DateUtils;

/**
 * A context for the current build
 * 
 * @author etirelli
 */
public class RuleBuildContext extends PackageBuildContext {

    // current rule
    private Rule                        rule;

    // a stack for the rule building used
    // for declarations resolution
    private Stack                       buildStack;

    // current Rule descriptor
    private RuleDescr                   ruleDescr;

    // available declarationResolver 
    private DeclarationScopeResolver    declarationResolver;

    // a simple counter for patterns
    private int                         patternId = -1;

    /**
     * Default constructor
     */
    public RuleBuildContext(final PackageBuilderConfiguration configuration,
                            final Package pkg,
                            final RuleDescr ruleDescr,
                            final DialectRegistry dialectRegistry,
                            final Dialect defaultDialect) {
        this.buildStack = new Stack();
        this.declarationResolver = new DeclarationScopeResolver( new Map[]{pkg.getGlobals()},
                                                                 this.buildStack );
        this.ruleDescr = ruleDescr;

        if ( ruleDescr instanceof QueryDescr ) {
            this.rule = new Query( ruleDescr.getName() );
        } else {
            this.rule = new Rule( ruleDescr.getName() );
        }        
        this.rule.setPackage( pkg.getName() );

        // Assign attributes
        setAttributes( this.rule,
                       ruleDescr,
                       ruleDescr.getAttributes() );
        
        init(configuration, pkg, ruleDescr, dialectRegistry, defaultDialect, this.rule );
        
        if ( this.rule.getDialect() == null ) {
            this.rule.setDialect( getDialect().getId() );
        }

        getDialect().init( ruleDescr );
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
     * @param declarationResolver
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

    public Stack getBuildStack() {
        return this.buildStack;
    }

    /**
     * Sets rule Attributes
     * 
     * @param rule
     * @param attributes
     */
    public static void setAttributes(final Rule rule,
                                     final RuleDescr ruleDescr,
                                     final List attributes) {

        for ( final Iterator it = attributes.iterator(); it.hasNext(); ) {
            final AttributeDescr attributeDescr = (AttributeDescr) it.next();
            final String name = attributeDescr.getName();
            if ( name.equals( "salience" ) ) {
                try {
                    ruleDescr.setSalience( attributeDescr.getValue() );
                } catch ( Exception e ) {

                }
            } else if ( name.equals( "no-loop" ) ) {
                if ( attributeDescr.getValue() == null ) {
                    rule.setNoLoop( true );
                } else {
                    rule.setNoLoop( Boolean.valueOf( attributeDescr.getValue() ).booleanValue() );
                }
            } else if ( name.equals( "auto-focus" ) ) {
                if ( attributeDescr.getValue() == null ) {
                    rule.setAutoFocus( true );
                } else {
                    rule.setAutoFocus( Boolean.valueOf( attributeDescr.getValue() ).booleanValue() );
                }
            } else if ( name.equals( "agenda-group" ) ) {
                rule.setAgendaGroup( attributeDescr.getValue() );
            } else if ( name.equals( "activation-group" ) ) {
                rule.setActivationGroup( attributeDescr.getValue() );
            } else if ( name.equals( "ruleflow-group" ) ) {
                rule.setRuleFlowGroup( attributeDescr.getValue() );
            } else if ( name.equals( "lock-on-active" ) ) {
                if ( attributeDescr.getValue() == null ) {
                    rule.setLockOnActive( true );
                } else {
                    rule.setLockOnActive( Boolean.valueOf( attributeDescr.getValue() ).booleanValue() );
                }
            } else if ( name.equals( "duration" ) ) {
                rule.setDuration( Long.parseLong( attributeDescr.getValue() ) );
                rule.setAgendaGroup( "" );
            } else if ( name.equals( "enabled" ) ) {
                if ( attributeDescr.getValue() == null ) {
                    rule.setEnabled( true );
                } else {
                    rule.setEnabled( Boolean.valueOf( attributeDescr.getValue() ).booleanValue() );
                }
            } else if ( name.equals( "date-effective" ) ) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime( DateUtils.parseDate( attributeDescr.getValue() ) );
                rule.setDateEffective( cal );
            } else if ( name.equals( "date-expires" ) ) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime( DateUtils.parseDate( attributeDescr.getValue() ) );
                rule.setDateExpires( cal );
            } else if ( name.equals( "dialect" ) ) {
                rule.setDialect( attributeDescr.getValue() );
            }
        }
    }

}
