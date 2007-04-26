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

import org.drools.base.evaluators.DateFactory;
import org.drools.compiler.DialectRegistry;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.DeclarationScopeResolver;

/**
 * A context for the current build
 * 
 * @author etirelli
 */
public class RuleBuildContext {

    // current package
    private Package                  pkg;

    // current rule
    private Rule                     rule;

    // a stack for the rule building used
    // for declarations resolution
    private Stack                    buildStack;

    // current Rule descriptor
    private RuleDescr                ruleDescr;

    // available declarationResolver 
    private DeclarationScopeResolver declarationResolver;

    // a simple counter for patterns
    private int                      patternId = -1;

    // errors found when building the current context
    private List                     errors;

    // list of generated methods
    private List                     methods;

    // map<String invokerClassName, String invokerCode> of generated invokers
    private Map                      invokers;

    // map<String invokerClassName, ConditionalElement ce> of generated invoker lookups
    private Map                      invokerLookups;

    // map<String invokerClassName, BaseDescr descr> of descriptor lookups
    private Map                      descrLookups;

    // a simple counter for generated names
    private int                      counter;
    
    private Dialect                  dialect;

    /**
     * Default constructor
     */
    public RuleBuildContext(final Package pkg,
                            final RuleDescr ruleDescr,
                            final DialectRegistry registry) {
        this.pkg = pkg;
        
        this.methods = new ArrayList();
        this.invokers = new HashMap();
        this.invokerLookups = new HashMap();
        this.descrLookups = new HashMap();
        this.errors = new ArrayList();
        this.buildStack = new Stack();
        this.declarationResolver = new DeclarationScopeResolver( new Map[]{this.pkg.getGlobals()},
                                                                 this.buildStack );
        this.ruleDescr = ruleDescr;

        if ( ruleDescr instanceof QueryDescr ) {
            this.rule = new Query( ruleDescr.getName() );
        } else {
            this.rule = new Rule( ruleDescr.getName() );
        }
        
        // Assign attributes
        setAttributes( this.rule,
                       ruleDescr.getAttributes() );
        
        
        String dialectName = ( this.rule.getDialect() != null ) ? this.rule.getDialect() : "default"; 
        this.dialect = registry.getDialect( dialectName );
        
        this.dialect.init( ruleDescr );
    }
    
    public Dialect getDialect() {
        return this.dialect;
    }

    /**
     * Returns the list of errors found while building the current context
     * @return
     */
    public List getErrors() {
        return this.errors;
    }

    /**
     * Returns the current package being built
     * @return
     */
    public Package getPkg() {
        return this.pkg;
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

    /**
     * Returns the Map<String invokerClassName, BaseDescr descr> of descriptor lookups
     * @return
     */
    public Map getDescrLookups() {
        return this.descrLookups;
    }

    public void setDescrLookups(final Map descrLookups) {
        this.descrLookups = descrLookups;
    }

    /**
     * Returns the Map<String invokerClassName, ConditionalElement ce> of generated invoker lookups
     * @return
     */
    public Map getInvokerLookups() {
        return this.invokerLookups;
    }

    public void setInvokerLookups(final Map invokerLookups) {
        this.invokerLookups = invokerLookups;
    }

    /**
     * Returns the Map<String invokerClassName, String invokerCode> of generated invokers
     * @return
     */
    public Map getInvokers() {
        return this.invokers;
    }

    public void setInvokers(final Map invokers) {
        this.invokers = invokers;
    }

    /**
     * Returns the list of generated methods
     * @return
     */
    public List getMethods() {
        return this.methods;
    }

    public void setMethods(final List methods) {
        this.methods = methods;
    }

    /**
     * Returns current counter value for generated method names
     * @return
     */
    public int getCurrentId() {
        return this.counter;
    }

    public int getNextId() {
        return this.counter++;
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
                              final List attributes) {

        for ( final Iterator it = attributes.iterator(); it.hasNext(); ) {
            final AttributeDescr attributeDescr = (AttributeDescr) it.next();
            final String name = attributeDescr.getName();
            if ( name.equals( "salience" ) ) {
                rule.setSalience( Integer.parseInt( attributeDescr.getValue() ) );
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
                cal.setTime( DateFactory.parseDate( attributeDescr.getValue() ) );
                rule.setDateEffective( cal );
            } else if ( name.equals( "date-expires" ) ) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime( DateFactory.parseDate( attributeDescr.getValue() ) );
                rule.setDateExpires( cal );
            } else if ( name.equals( "dialect" ) ) {
                rule.setDialect( attributeDescr.getValue() );
            }
        }
    }

}
