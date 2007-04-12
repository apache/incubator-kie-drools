package org.drools.rule.builder;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jfdi.interpreter.TypeResolver;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.evaluators.DateFactory;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.GroupElement;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.builder.dialect.java.BuildUtils;
import org.drools.rule.builder.dialect.java.DeclarationTypeFixer;
import org.drools.rule.builder.dialect.java.JavaExprAnalyzer;
import org.drools.rule.builder.dialect.java.KnowledgeHelperFixer;

/**
 * This builds the rule structure from an AST.
 * Generates semantic code where necessary if semantics are used.
 * This is an internal API.
 */
public class RuleBuilder {

    // the current build context
    private BuildContext       context;

    // the current build utils
    private BuildUtils         utils;

    // a map of registered builders
    private Map                builders;

    // the builder for columns
    private ColumnBuilder      columnBuilder;

    // the builder for the consequence
    private ConsequenceBuilder consequenceBuilder;

    // the builder for the rule class
    private RuleClassBuilder   classBuilder;

    private Dialect            dialect;

    // Constructor
    public RuleBuilder(final TypeResolver typeResolver,
                       final ClassFieldExtractorCache cache,
                       final Dialect dialect) {

        this.dialect = dialect;

        // statically adding all builders to the map
        // but in the future we can move that to a configuration
        // if we want to
        this.builders = new HashMap();

        this.builders.put( CollectDescr.class,
                      new CollectBuilder() );

        this.builders.put( ForallDescr.class,
                      new ForallBuilder() );
        final GroupElementBuilder gebuilder = new GroupElementBuilder();
        this.builders.put( AndDescr.class,
                      gebuilder );
        this.builders.put( OrDescr.class,
                      gebuilder );
        this.builders.put( NotDescr.class,
                      gebuilder );
        this.builders.put( ExistsDescr.class,
                      gebuilder );

        // dialect specific        
        this.columnBuilder = new ColumnBuilder( this.dialect );

        this.builders.put( FromDescr.class,
                      this.dialect.getFromBuilder() );

        this.builders.put( AccumulateDescr.class,
                      this.dialect.getAccumulateBuilder() );

        this.builders.put( EvalDescr.class,
                      this.dialect.getEvalBuilder() );

        this.consequenceBuilder = this.dialect.getConsequenceBuilder();

        this.classBuilder = this.dialect.getRuleClassBuilder();

        this.utils = new BuildUtils( new KnowledgeHelperFixer(),
                                     new DeclarationTypeFixer(),
                                     new JavaExprAnalyzer(),
                                     typeResolver,
                                     cache,
                                     this.builders );
    }

    public Map getInvokers() {
        return (this.context == null) ? null : this.context.getInvokers();
    }

    public Map getDescrLookups() {
        return (this.context == null) ? null : this.context.getDescrLookups();
    }

    public String getRuleClass() {
        return (this.context == null) ? null : this.context.getRuleClass();
    }

    public Map getInvokerLookups() {
        return (this.context == null) ? null : this.context.getInvokerLookups();
    }

    public List getErrors() {
        return (this.context == null) ? null : this.context.getErrors();
    }

    public Rule getRule() {
        if ( this.context == null ) {
            return null;
        }
        if ( !this.context.getErrors().isEmpty() ) {
            this.context.getRule().setSemanticallyValid( false );
        }
        return this.context.getRule();
    }

    public Package getPackage() {
        return this.context.getPkg();
    }

    /**
     * Build the give rule into the 
     * @param pkg
     * @param ruleDescr
     * @return
     */
    public synchronized Rule build(final Package pkg,
                                   final RuleDescr ruleDescr) {
        this.context = new BuildContext( pkg,
                                         ruleDescr );

        // Assign attributes
        setAttributes( this.context.getRule(),
                       ruleDescr.getAttributes() );

        final ConditionalElementBuilder builder = this.utils.getBuilder( ruleDescr.getLhs().getClass() );
        if ( builder != null ) {
            final GroupElement ce = (GroupElement) builder.build( this.context,
                                                            this.utils,
                                                            this.columnBuilder,
                                                            ruleDescr.getLhs() );
            this.context.getRule().setLhs( ce );
        } else {
            throw new RuntimeDroolsException( "BUG: builder not found for descriptor class " + ruleDescr.getLhs().getClass() );
        }

        // Build the consequence and generate it's invoker/s
        // generate the main rule from the previously generated s.
        if ( !(ruleDescr instanceof QueryDescr) ) {
            // do not build the consequence if we have a query
            this.consequenceBuilder.build( this.context,
                                           this.utils,
                                           ruleDescr );
        }
        this.classBuilder.buildRule( this.context,
                                     this.utils,
                                     ruleDescr );

        return this.context.getRule();
    }

    /**
     * Sets rule Attributes
     * 
     * @param rule
     * @param attributes
     */
    public void setAttributes(final Rule rule,
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

            } else if ( name.equals( "language" ) ) {
                //@todo: we don't currently  support multiple languages
            }
        }
    }

}