package org.drools.semantics.java;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jfdi.interpreter.TypeResolver;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Column;
import org.drools.rule.ConditionalElement;
import org.drools.rule.GroupElement;
import org.drools.rule.GroupElementFactory;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.semantics.java.builder.AccumulateBuilder;
import org.drools.semantics.java.builder.BuildContext;
import org.drools.semantics.java.builder.BuildUtils;
import org.drools.semantics.java.builder.CollectBuilder;
import org.drools.semantics.java.builder.ColumnBuilder;
import org.drools.semantics.java.builder.ConditionalElementBuilder;
import org.drools.semantics.java.builder.ConsequenceBuilder;
import org.drools.semantics.java.builder.EvalBuilder;
import org.drools.semantics.java.builder.FromBuilder;
import org.drools.semantics.java.builder.RuleClassBuilder;

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

    private ConsequenceBuilder consequenceBuilder;
    
    private RuleClassBuilder   classBuilder;

    public RuleBuilder(final TypeResolver typeResolver,
                       final FunctionFixer functionFixer,
                       final ClassFieldExtractorCache cache) {
        this.utils = new BuildUtils( functionFixer,
                                     new KnowledgeHelperFixer(),
                                     new JavaExprAnalyzer(),
                                     typeResolver,
                                     cache );

        this.columnBuilder = new ColumnBuilder();

        this.consequenceBuilder = new ConsequenceBuilder();
        
        this.classBuilder = new RuleClassBuilder();

        // statically adding all builders to the map
        // but in the future we can move that to a configuration
        // if we want to
        this.builders = new HashMap();
        builders.put( EvalDescr.class,
                      new EvalBuilder() );
        builders.put( FromDescr.class,
                      new FromBuilder() );
        builders.put( CollectDescr.class,
                      new CollectBuilder() );
        builders.put( AccumulateDescr.class,
                      new AccumulateBuilder() );

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

    public synchronized Rule build(final Package pkg,
                                   final RuleDescr ruleDescr) {
        this.context = new BuildContext( pkg,
                                         ruleDescr );

        // Assign attributes
        setAttributes( this.context.getRule(),
                       ruleDescr.getAttributes() );

        // Build the left hand side
        // generate invokers
        build( ruleDescr );

        return this.context.getRule();
    }

    private void build(final RuleDescr ruleDescr) {

        for ( final Iterator it = ruleDescr.getLhs().getDescrs().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof ConditionalElementDescr ) {
                if ( object.getClass() == AndDescr.class ) {
                    final GroupElement and = GroupElementFactory.newAndInstance();
                    build( this.context.getRule(),
                           (ConditionalElementDescr) object,
                           and,
                           false, // do not decrement offset
                           false ); // do not decrement first offset
                    this.context.getRule().addPattern( and );
                } else if ( object.getClass() == OrDescr.class ) {
                    final GroupElement or = GroupElementFactory.newOrInstance();
                    build( this.context.getRule(),
                           (ConditionalElementDescr) object,
                           or,
                           true, // when OR is used, offset MUST be decremented
                           false ); // do not decrement first offset
                    this.context.getRule().addPattern( or );
                } else if ( object.getClass() == NotDescr.class ) {
                    // We cannot have declarations created inside a not visible outside it, so track no declarations so they can be removed
                    context.setInnerDeclarations( new HashMap() );
                    final GroupElement not = GroupElementFactory.newNotInstance();
                    build( this.context.getRule(),
                           (ConditionalElementDescr) object,
                           not,
                           true, // when NOT is used, offset MUST be decremented
                           true ); // when NOT is used, offset MUST be decremented for first column
                    this.context.getRule().addPattern( not );

                    // remove declarations bound inside not node
                    for ( final Iterator notIt = context.getInnerDeclarations().keySet().iterator(); notIt.hasNext(); ) {
                        context.getDeclarations().remove( notIt.next() );
                    }

                    context.setInnerDeclarations( null );
                } else if ( object.getClass() == ExistsDescr.class ) {
                    // We cannot have declarations created inside exists visible outside it, 
                    // so track declarations in a way they can be removed
                    context.setInnerDeclarations( new HashMap() );
                    final GroupElement exists = GroupElementFactory.newExistsInstance();
                    build( this.context.getRule(),
                           (ConditionalElementDescr) object,
                           exists,
                           true, // when EXIST is used, offset MUST be decremented
                           true ); // when EXIST is used, offset MUST be decremented for first column
                    // remove declarations bound inside not node
                    for ( final Iterator notIt = context.getInnerDeclarations().keySet().iterator(); notIt.hasNext(); ) {
                        context.getDeclarations().remove( notIt.next() );
                    }

                    context.setInnerDeclarations( null );
                    this.context.getRule().addPattern( exists );
                } else {
                    ConditionalElementBuilder builder = (ConditionalElementBuilder) this.builders.get( object.getClass() );
                    ConditionalElement ce = builder.build( this.context,
                                                           this.utils,
                                                           this.columnBuilder,
                                                           (BaseDescr) object );
                    if ( ce != null ) {
                        this.context.getRule().addPattern( ce );
                    }
                }
            } else if ( object.getClass() == ColumnDescr.class ) {
                final Column column = this.columnBuilder.build( this.context,
                                                                this.utils,
                                                                (ColumnDescr) object );
                if ( column != null ) {
                    this.context.getRule().addPattern( column );
                }
            }
        }

        // Build the consequence and generate it's invoker/s
        // generate the main rule from the previously generated s.
        if ( !(ruleDescr instanceof QueryDescr) ) {
            // do not build the consequence if we have a query
            this.consequenceBuilder.buildConsequence( this.context,
                                                      this.utils,
                                                      ruleDescr );
        }
        this.classBuilder.buildRule( this.context, this.utils, ruleDescr );
    }

    private void build(final Rule rule,
                       final ConditionalElementDescr descr,
                       final GroupElement group,
                       final boolean decrementOffset,
                       boolean decrementFirst) {
        for ( final Iterator it = descr.getDescrs().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof ConditionalElementDescr ) {
                if ( object.getClass() == AndDescr.class ) {
                    final GroupElement and = GroupElementFactory.newAndInstance();
                    build( rule,
                           (ConditionalElementDescr) object,
                           and,
                           false, // do not decrement offset
                           false ); // do not decrement first offset
                    group.addChild( and );
                } else if ( object.getClass() == OrDescr.class ) {
                    final GroupElement or = GroupElementFactory.newOrInstance();
                    build( rule,
                           (ConditionalElementDescr) object,
                           or,
                           true, // when OR is used, offset MUST be decremented
                           false ); // do not decrement first offset
                    group.addChild( or );
                } else if ( object.getClass() == NotDescr.class ) {
                    final GroupElement not = GroupElementFactory.newNotInstance();
                    build( rule,
                           (ConditionalElementDescr) object,
                           not,
                           true, // when NOT is used, offset MUST be decremented
                           true ); // when NOT is used, offset MUST be decremented for first column
                    group.addChild( not );
                } else if ( object.getClass() == ExistsDescr.class ) {
                    final GroupElement exists = GroupElementFactory.newExistsInstance();
                    build( rule,
                           (ConditionalElementDescr) object,
                           exists,
                           true, // when EXIST is used, offset MUST be decremented
                           true ); // when EXIST is used, offset MUST be decremented for first column
                    group.addChild( exists );
                } else {
                    ConditionalElementBuilder builder = (ConditionalElementBuilder) this.builders.get( object.getClass() );
                    ConditionalElement ce = builder.build( this.context,
                                                           this.utils,
                                                           this.columnBuilder,
                                                           (BaseDescr) object );
                    if ( ce != null ) {
                        this.context.getRule().addPattern( ce );
                    }
                }
            } else if ( object.getClass() == ColumnDescr.class ) {
                if ( decrementOffset && decrementFirst ) {
                    this.context.setColumnOffset( this.context.getColumnOffset() - 1 );
                } else {
                    decrementFirst = true;
                }
                final Column column = this.columnBuilder.build( this.context, this.utils, (ColumnDescr) object );
                if ( column != null ) {
                    group.addChild( column );
                }
            }
        }
    }

    private void setAttributes(final Rule rule,
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
                rule.setXorGroup( attributeDescr.getValue() );
            } else if ( name.equals( "duration" ) ) {
                rule.setDuration( Long.parseLong( attributeDescr.getValue() ) );
                rule.setAgendaGroup( "" );
            } else if ( name.equals( "language" ) ) {
                //@todo: we don't currently  support multiple languages
            }
        }
    }

}