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

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.base.FieldFactory;
import org.drools.base.FieldImpl;
import org.drools.base.ValueType;
import org.drools.base.dataproviders.MethodDataProvider;
import org.drools.base.dataproviders.MethodInvoker;
import org.drools.base.evaluators.Operator;
import org.drools.base.resolvers.DeclarationVariable;
import org.drools.base.resolvers.GlobalVariable;
import org.drools.base.resolvers.LiteralValue;
import org.drools.base.resolvers.MapValue;
import org.drools.base.resolvers.ValueHandler;
import org.drools.compiler.RuleError;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateFieldExtractor;
import org.drools.facttemplates.FactTemplateObjectType;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.ArgumentValueDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.DeclarativeInvokerDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldAccessDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionCallDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.MethodAccessDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.RestrictionDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.rule.And;
import org.drools.rule.AndCompositeRestriction;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Exists;
import org.drools.rule.From;
import org.drools.rule.GroupElement;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.LiteralRestriction;
import org.drools.rule.MultiRestrictionFieldConstraint;
import org.drools.rule.Not;
import org.drools.rule.Or;
import org.drools.rule.OrCompositeRestriction;
import org.drools.rule.Package;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Query;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.Rule;
import org.drools.rule.VariableConstraint;
import org.drools.rule.VariableRestriction;
import org.drools.spi.DataProvider;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.ObjectType;
import org.drools.spi.Restriction;
import org.drools.spi.TypeResolver;

/**
 * This builds the rule structure from an AST.
 * Generates semantic code where necessary if semantics are used.
 * This is an internal API.
 */
public class RuleBuilder {
    private Package                           pkg;
    private Rule                              rule;
    private RuleDescr                         ruleDescr;

    public String                             ruleClass;
    public List                               methods;
    public Map                                invokers;

    private Map                               invokerLookups;

    private Map                               descrLookups;

    private Map                               declarations;

    private int                               counter;

    private ColumnCounter                     columnCounter;

    private int                               columnOffset;

    private List                              errors;

    private final TypeResolver                typeResolver;

    private Map                               notDeclarations;

    private static final StringTemplateGroup  ruleGroup            = new StringTemplateGroup( new InputStreamReader( RuleBuilder.class.getResourceAsStream( "javaRule.stg" ) ),
                                                                                              AngleBracketTemplateLexer.class );

    private static final StringTemplateGroup  invokerGroup         = new StringTemplateGroup( new InputStreamReader( RuleBuilder.class.getResourceAsStream( "javaInvokers.stg" ) ),
                                                                                              AngleBracketTemplateLexer.class );

    private static final KnowledgeHelperFixer knowledgeHelperFixer = new KnowledgeHelperFixer();
    private static final FunctionFixer        functionFixer        = new FunctionFixer();

    // @todo move to an interface so it can work as a decorator
    private final JavaExprAnalyzer            analyzer             = new JavaExprAnalyzer();
    private ClassFieldExtractorCache          classFieldExtractorCache;

    public RuleBuilder(TypeResolver resolver,
                       ClassFieldExtractorCache cache) {
        this.classFieldExtractorCache = cache;
        this.typeResolver = resolver;
        this.errors = new ArrayList();
    }

    public Map getInvokers() {
        return this.invokers;
    }

    public Map getDescrLookups() {
        return this.descrLookups;
    }

    public String getRuleClass() {
        return this.ruleClass;
    }

    public Map getInvokerLookups() {
        return this.invokerLookups;
    }

    public List getErrors() {
        return this.errors;
    }

    public Rule getRule() {
        if ( !this.errors.isEmpty() ) {
            this.rule.setSemanticallyValid( false );
        }
        return this.rule;
    }

    public Package getPackage() {
        return this.pkg;
    }

    public synchronized Rule build(final Package pkg,
                                   final RuleDescr ruleDescr) {
        this.pkg = pkg;
        this.methods = new ArrayList();
        this.invokers = new HashMap();
        this.invokerLookups = new HashMap();
        this.declarations = new HashMap();
        this.descrLookups = new HashMap();
        this.columnCounter = new ColumnCounter();

        this.ruleDescr = ruleDescr;

        if ( ruleDescr instanceof QueryDescr ) {
            this.rule = new Query( ruleDescr.getName() );
        } else {
            this.rule = new Rule( ruleDescr.getName() );
        }

        // Assign attributes
        setAttributes( this.rule,
                       ruleDescr.getAttributes() );

        // Build the left hand side
        // generate invoker, s
        build( ruleDescr );

        return this.rule;
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

    private void build(final RuleDescr ruleDescr) {

        for ( final Iterator it = ruleDescr.getLhs().getDescrs().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof ConditionalElementDescr ) {
                if ( object.getClass() == AndDescr.class ) {
                    final And and = new And();
                    this.columnCounter.setParent( and );
                    build( this.rule,
                           (ConditionalElementDescr) object,
                           and,
                           false, // do not decrement offset
                           false ); // do not decrement first offset
                    this.rule.addPattern( and );
                } else if ( object.getClass() == OrDescr.class ) {
                    final Or or = new Or();
                    this.columnCounter.setParent( or );
                    build( this.rule,
                           (ConditionalElementDescr) object,
                           or,
                           true, // when OR is used, offset MUST be decremented
                           false ); // do not decrement first offset
                    this.rule.addPattern( or );
                } else if ( object.getClass() == NotDescr.class ) {
                    // We cannot have declarations created inside a not visible outside it, so track no declarations so they can be removed
                    this.notDeclarations = new HashMap();
                    final Not not = new Not();
                    this.columnCounter.setParent( not );
                    build( this.rule,
                           (ConditionalElementDescr) object,
                           not,
                           true, // when NOT is used, offset MUST be decremented
                           true ); // when NOT is used, offset MUST be decremented for first column
                    this.rule.addPattern( not );

                    // remove declarations bound inside not node
                    for ( final Iterator notIt = this.notDeclarations.keySet().iterator(); notIt.hasNext(); ) {
                        this.declarations.remove( notIt.next() );
                    }

                    this.notDeclarations = null;
                } else if ( object.getClass() == ExistsDescr.class ) {
                    // We cannot have declarations created inside a not visible outside it, so track no declarations so they can be removed
                    this.notDeclarations = new HashMap();
                    final Exists exists = new Exists();
                    this.columnCounter.setParent( exists );
                    build( this.rule,
                           (ConditionalElementDescr) object,
                           exists,
                           true, // when EXIST is used, offset MUST be decremented
                           true ); // when EXIST is used, offset MUST be decremented for first column
                    // remove declarations bound inside not node
                    for ( final Iterator notIt = this.notDeclarations.keySet().iterator(); notIt.hasNext(); ) {
                        this.declarations.remove( notIt.next() );
                    }

                    this.notDeclarations = null;
                    this.rule.addPattern( exists );
                } else if ( object.getClass() == EvalDescr.class ) {
                    final EvalCondition eval = build( (EvalDescr) object );
                    if ( eval != null ) {
                        this.rule.addPattern( eval );
                    }
                } else if ( object.getClass() == FromDescr.class ) {
                    final From from = build( (FromDescr) object );
                    this.rule.addPattern( from );
                }
            } else if ( object.getClass() == ColumnDescr.class ) {
                final Column column = build( (ColumnDescr) object );
                if ( column != null ) {
                    this.rule.addPattern( column );
                }
            }
        }

        // Build the consequence and generate it's invoker/s
        // generate the main rule from the previously generated s.
        if ( !(ruleDescr instanceof QueryDescr) ) {
            // do not build the consequence if we have a query
            buildConsequence( ruleDescr );
        }
        buildRule( ruleDescr );
    }

    private void build(final Rule rule,
                       final ConditionalElementDescr descr,
                       final GroupElement ce,
                       final boolean decrementOffset,
                       boolean decrementFirst) {
        for ( final Iterator it = descr.getDescrs().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof ConditionalElementDescr ) {
                if ( object.getClass() == AndDescr.class ) {
                    final And and = new And();
                    this.columnCounter.setParent( and );
                    build( rule,
                           (ConditionalElementDescr) object,
                           and,
                           false, // do not decrement offset
                           false ); // do not decrement first offset
                    ce.addChild( and );
                } else if ( object.getClass() == OrDescr.class ) {
                    final Or or = new Or();
                    this.columnCounter.setParent( or );
                    build( rule,
                           (ConditionalElementDescr) object,
                           or,
                           true, // when OR is used, offset MUST be decremented
                           false ); // do not decrement first offset
                    ce.addChild( or );
                } else if ( object.getClass() == NotDescr.class ) {
                    final Not not = new Not();
                    this.columnCounter.setParent( not );
                    build( rule,
                           (ConditionalElementDescr) object,
                           not,
                           true, // when NOT is used, offset MUST be decremented
                           true ); // when NOT is used, offset MUST be decremented for first column
                    ce.addChild( not );
                } else if ( object.getClass() == ExistsDescr.class ) {
                    final Exists exists = new Exists();
                    this.columnCounter.setParent( exists );
                    build( rule,
                           (ConditionalElementDescr) object,
                           exists,
                           true, // when EXIST is used, offset MUST be decremented
                           true ); // when EXIST is used, offset MUST be decremented for first column
                    ce.addChild( exists );
                } else if ( object.getClass() == EvalDescr.class ) {
                    final EvalCondition eval = build( (EvalDescr) object );
                    if ( eval != null ) {
                        ce.addChild( eval );
                    }
                } else if ( object.getClass() == FromDescr.class ) {
                    final From from = build( (FromDescr) object );
                    this.rule.addPattern( from );
                }
            } else if ( object.getClass() == ColumnDescr.class ) {
                if ( decrementOffset && decrementFirst ) {
                    this.columnOffset--;
                } else {
                    decrementFirst = true;
                }
                final Column column = build( (ColumnDescr) object );
                if ( column != null ) {
                    ce.addChild( column );
                }
            }
        }
    }

    private Column build(final ColumnDescr columnDescr) {
        if ( columnDescr.getObjectType() == null || columnDescr.getObjectType().equals( "" ) ) {
            this.errors.add( new RuleError( this.rule,
                                            columnDescr,
                                            null,
                                            "ObjectType not correctly defined" ) );
            return null;
        }

        ObjectType objectType = null;

        FactTemplate factTemplate = this.pkg.getFactTemplate( columnDescr.getObjectType() );

        if ( factTemplate != null ) {
            objectType = new FactTemplateObjectType( factTemplate );
        } else {
            try {
                //clazz = Class.forName( columnDescr.getObjectType() );
                objectType = new ClassObjectType( this.typeResolver.resolveType( columnDescr.getObjectType() ) );
            } catch ( final ClassNotFoundException e ) {
                this.errors.add( new RuleError( this.rule,
                                                columnDescr,
                                                null,
                                                "Unable to resolve ObjectType '" + columnDescr.getObjectType() + "'" ) );
                return null;
            }
        }

        Column column;
        if ( columnDescr.getIdentifier() != null && !columnDescr.getIdentifier().equals( "" ) ) {
            column = new Column( this.columnCounter.getNext(),
                                 this.columnOffset,
                                 objectType,
                                 columnDescr.getIdentifier() );;
            this.declarations.put( column.getDeclaration().getIdentifier(),
                                   column.getDeclaration() );

            if ( this.notDeclarations != null ) {
                this.notDeclarations.put( column.getDeclaration().getIdentifier(),
                                          column.getDeclaration() );
            }
        } else {
            column = new Column( this.columnCounter.getNext(),
                                 this.columnOffset,
                                 objectType,
                                 null );
        }

        for ( final Iterator it = columnDescr.getDescrs().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof FieldBindingDescr ) {
                build( column,
                       (FieldBindingDescr) object );
            } else if ( object instanceof FieldConstraintDescr ) {
                build( column,
                       (FieldConstraintDescr) object );
            } else if ( object instanceof PredicateDescr ) {
                build( column,
                       (PredicateDescr) object );
            }
        }
        return column;
    }

    private void build(final Column column,
                       final FieldConstraintDescr fieldConstraintDescr) {

        final FieldExtractor extractor = getFieldExtractor( fieldConstraintDescr,
                                                            column.getObjectType(),
                                                            fieldConstraintDescr.getFieldName() );
        if ( extractor == null ) {
            // @todo log error
            return;
        }

        if ( fieldConstraintDescr.getRestrictions().size() == 1 ) {
            final Object object = fieldConstraintDescr.getRestrictions().get( 0 );

            Restriction restriction = buildRestriction( extractor,
                                                        fieldConstraintDescr,
                                                        (RestrictionDescr) object );
            if ( restriction == null ) {
                // @todo log errors
                return;
            }

            if ( object instanceof LiteralRestrictionDescr ) {
                column.addConstraint( new LiteralConstraint( extractor,
                                                             (LiteralRestriction) restriction ) );
            } else if ( object instanceof VariableRestrictionDescr ) {
                column.addConstraint( new VariableConstraint( extractor,
                                                              (VariableRestriction) restriction ) );
            } else if ( object instanceof ReturnValueRestrictionDescr ) {
                column.addConstraint( new ReturnValueConstraint( extractor,
                                                                 (ReturnValueRestriction) restriction ) );
            }

            return;
        }

        List orList = new ArrayList();
        List andList = null;

        RestrictionDescr currentRestriction = null;
        RestrictionDescr previousRestriction = null;

        List currentList = null;
        List previousList = null;

        for ( final Iterator it = fieldConstraintDescr.getRestrictions().iterator(); it.hasNext(); ) {
            Object object = it.next();

            // Process an and/or connective 
            if ( object instanceof RestrictionConnectiveDescr ) {

                // is the connective an 'and'?
                if ( ((RestrictionConnectiveDescr) object).getConnective() == RestrictionConnectiveDescr.AND ) {
                    // if andList is null, then we know its the first
                    if ( andList == null ) {
                        andList = new ArrayList();
                    }
                    previousList = currentList;
                    currentList = andList;
                } else {
                    previousList = currentList;
                    currentList = orList;
                }
            } else {
                Restriction restriction = null;
                if ( currentList != null ) {
                    // Are we are at the first operator? if so treat differently
                    if ( previousList == null ) {
                        restriction = buildRestriction( extractor,
                                                        fieldConstraintDescr,
                                                        previousRestriction );
                        if ( currentList == andList ) {
                            andList.add( restriction );
                        } else {
                            orList.add( restriction );
                        }
                    } else {
                        restriction = buildRestriction( extractor,
                                                        fieldConstraintDescr,
                                                        previousRestriction );

                        if ( previousList == andList && currentList == orList ) {
                            andList.add( restriction );
                            if ( andList.size() == 1 ) {
                                // Can't have an 'and' connective with one child, so add directly to the or list
                                orList.add( andList.get( 0 ) );
                            } else {
                                Restriction restrictions = new AndCompositeRestriction( (Restriction[]) andList.toArray( new Restriction[andList.size()] ) );
                                orList.add( restrictions );
                            }
                            andList = null;
                        } else if ( previousList == andList && currentList == andList ) {
                            andList.add( restriction );
                        } else if ( previousList == orList && currentList == andList ) {
                            andList.add( restriction );
                        } else if ( previousList == orList && currentList == orList ) {
                            orList.add( restriction );
                        }
                    }
                }
            }
            previousRestriction = currentRestriction;
            currentRestriction = (RestrictionDescr) object;
        }

        Restriction restriction = buildRestriction( extractor,
                                                    fieldConstraintDescr,
                                                    currentRestriction );
        currentList.add( restriction );

        Restriction restrictions = null;
        if ( currentList == andList && !orList.isEmpty() ) {
            // Check if it finished with an and, and process it
            if ( andList != null ) {
                if ( andList.size() == 1 ) {
                    // Can't have an 'and' connective with one child, so add directly to the or list
                    orList.add( andList.get( 0 ) );
                } else {
                    orList.add( new AndCompositeRestriction( (Restriction[]) andList.toArray( new Restriction[andList.size()] ) ) );
                }
                andList = null;
            }
        }

        if ( !orList.isEmpty() ) {
            restrictions = new OrCompositeRestriction( (Restriction[]) orList.toArray( new Restriction[orList.size()] ) );
        } else if ( andList != null && !andList.isEmpty() ) {
            restrictions = new AndCompositeRestriction( (Restriction[]) andList.toArray( new Restriction[andList.size()] ) );
        } else {
            // @todo throw error
        }

        column.addConstraint( new MultiRestrictionFieldConstraint( extractor,
                                                                   restrictions ) );
    }

    private Restriction buildRestriction(FieldExtractor extractor,
                                         FieldConstraintDescr fieldConstraintDescr,
                                         RestrictionDescr restrictionDescr) {
        Restriction restriction = null;
        if ( restrictionDescr instanceof LiteralRestrictionDescr ) {
            restriction = buildRestriction( extractor,
                                            fieldConstraintDescr,
                                            (LiteralRestrictionDescr) restrictionDescr );
        } else if ( restrictionDescr instanceof VariableRestrictionDescr ) {
            restriction = buildRestriction( extractor,
                                            fieldConstraintDescr,
                                            (VariableRestrictionDescr) restrictionDescr );
        } else if ( restrictionDescr instanceof ReturnValueRestrictionDescr ) {
            restriction = buildRestriction( extractor,
                                            fieldConstraintDescr,
                                            (ReturnValueRestrictionDescr) restrictionDescr );

        }

        return restriction;
    }

    private void build(final Column column,
                       final FieldBindingDescr fieldBindingDescr) {
        Declaration declaration = (Declaration) this.declarations.get( fieldBindingDescr.getIdentifier() );
        if ( declaration != null ) {
            // This declaration already  exists, so throw an Exception
            this.errors.add( new RuleError( this.rule,
                                            fieldBindingDescr,
                                            null,
                                            "Duplicate declaration for variable '" + fieldBindingDescr.getIdentifier() + "' in the rule '" + this.rule.getName() + "'" ) );
            return;
        }

        final FieldExtractor extractor = getFieldExtractor( fieldBindingDescr,
                                                            column.getObjectType(),
                                                            fieldBindingDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        declaration = column.addDeclaration( fieldBindingDescr.getIdentifier(),
                                             extractor );

        this.declarations.put( declaration.getIdentifier(),
                               declaration );

        if ( this.notDeclarations != null ) {
            this.notDeclarations.put( declaration.getIdentifier(),
                                      declaration );
        }
    }

    private VariableRestriction buildRestriction(final FieldExtractor extractor,
                                                 final FieldConstraintDescr fieldConstraintDescr,
                                                 final VariableRestrictionDescr variableRestrictionDescr) {
        if ( variableRestrictionDescr.getIdentifier() == null || variableRestrictionDescr.getIdentifier().equals( "" ) ) {
            this.errors.add( new RuleError( this.rule,
                                            variableRestrictionDescr,
                                            null,
                                            "Identifier not defined for binding field '" + fieldConstraintDescr.getFieldName() + "'" ) );
            return null;
        }

        final Declaration declaration = (Declaration) this.declarations.get( variableRestrictionDescr.getIdentifier() );

        if ( declaration == null ) {
            this.errors.add( new RuleError( this.rule,
                                            variableRestrictionDescr,
                                            null,
                                            "Unable to return Declaration for identifier '" + variableRestrictionDescr.getIdentifier() + "'" ) );
            return null;
        }

        final Evaluator evaluator = getEvaluator( variableRestrictionDescr,
                                                  extractor.getValueType(),
                                                  variableRestrictionDescr.getEvaluator() );
        if ( evaluator == null ) {
            return null;
        }

        return new VariableRestriction( declaration,
                                        evaluator );
    }

    private LiteralRestriction buildRestriction(final FieldExtractor extractor,
                                                final FieldConstraintDescr fieldConstraintDescr,
                                                final LiteralRestrictionDescr literalRestrictionDescr) {
        FieldValue field = null;
        if ( literalRestrictionDescr.isStaticFieldValue() ) {
            final int lastDot = literalRestrictionDescr.getText().lastIndexOf( '.' );
            final String className = literalRestrictionDescr.getText().substring( 0,
                                                                                  lastDot );
            final String fieldName = literalRestrictionDescr.getText().substring( lastDot + 1 );
            try {
                final Class staticClass = this.typeResolver.resolveType( className );
                field = new FieldImpl( staticClass.getField( fieldName ).get( null ) );
            } catch ( final ClassNotFoundException e ) {
                this.errors.add( new RuleError( this.rule,
                                                literalRestrictionDescr,
                                                e,
                                                e.getMessage() ) );
            } catch ( final Exception e ) {
                this.errors.add( new RuleError( this.rule,
                                                literalRestrictionDescr,
                                                e,
                                                "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + literalRestrictionDescr.getText() + "'" ) );
            }

        } else {
            try {
                field = FieldFactory.getFieldValue( literalRestrictionDescr.getText(),
                                                    extractor.getValueType() );
            } catch ( final Exception e ) {
                this.errors.add( new RuleError( this.rule,
                                                literalRestrictionDescr,
                                                e,
                                                "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + literalRestrictionDescr.getText() + "'" ) );
            }
        }

        final Evaluator evaluator = getEvaluator( literalRestrictionDescr,
                                                  extractor.getValueType(),
                                                  literalRestrictionDescr.getEvaluator() );
        if ( evaluator == null ) {
            return null;
        }

        return new LiteralRestriction( field,
                                       evaluator );
    }

    private ReturnValueRestriction buildRestriction(final FieldExtractor extractor,
                                                    final FieldConstraintDescr fieldConstraintDescr,
                                                    final ReturnValueRestrictionDescr returnValueRestrictionDescr) {
        final String className = "returnValue" + this.counter++;
        returnValueRestrictionDescr.setClassMethodName( className );

        final List[] usedIdentifiers = getUsedIdentifiers( returnValueRestrictionDescr,
                                                           returnValueRestrictionDescr.getText() );

        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedIdentifiers[0].get( i ) );
        }

        final Evaluator evaluator = getEvaluator( returnValueRestrictionDescr,
                                                  extractor.getValueType(),
                                                  returnValueRestrictionDescr.getEvaluator() );
        if ( evaluator == null ) {
            return null;
        }

        final ReturnValueRestriction returnValueRestriction = new ReturnValueRestriction( declarations,
                                                                                          evaluator );

        StringTemplate st = RuleBuilder.ruleGroup.getInstanceOf( "returnValueMethod" );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     returnValueRestrictionDescr.getText() );

        st.setAttribute( "methodName",
                         className );

        final String returnValueText = RuleBuilder.functionFixer.fix( returnValueRestrictionDescr.getText() );
        st.setAttribute( "text",
                         returnValueText );

        this.methods.add( st.toString() );

        st = RuleBuilder.invokerGroup.getInstanceOf( "returnValueInvoker" );

        st.setAttribute( "package",
                         this.pkg.getName() );
        st.setAttribute( "ruleClassName",
                         ucFirst( this.ruleDescr.getClassName() ) );
        st.setAttribute( "invokerClassName",
                         this.ruleDescr.getClassName() + ucFirst( className ) + "Invoker" );
        st.setAttribute( "methodName",
                         className );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     returnValueRestrictionDescr.getText() );

        st.setAttribute( "hashCode",
                         returnValueText.hashCode() );

        final String invokerClassName = this.pkg.getName() + "." + this.ruleDescr.getClassName() + ucFirst( className ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 returnValueRestriction );
        this.descrLookups.put( invokerClassName,
                               returnValueRestrictionDescr );

        return returnValueRestriction;
    }

    private void build(final Column column,
                       final PredicateDescr predicateDescr) {
        // generate 
        // generate Invoker
        final String className = "predicate" + this.counter++;
        predicateDescr.setClassMethodName( className );

        final FieldExtractor extractor = getFieldExtractor( predicateDescr,
                                                            column.getObjectType(),
                                                            predicateDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        final Declaration declaration = column.addDeclaration( predicateDescr.getDeclaration(),
                                                               extractor );

        this.declarations.put( declaration.getIdentifier(),
                               declaration );

        if ( this.notDeclarations != null ) {
            this.notDeclarations.put( declaration.getIdentifier(),
                                      declaration );
        }

        final List[] usedIdentifiers = getUsedIdentifiers( predicateDescr,
                                                           predicateDescr.getText() );
        // Don't include the focus declaration, that hasn't been merged into the tuple yet.
        usedIdentifiers[0].remove( predicateDescr.getDeclaration() );

        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedIdentifiers[0].get( i ) );
        }

        final PredicateConstraint predicateConstraint = new PredicateConstraint( declaration,
                                                                                 declarations );
        column.addConstraint( predicateConstraint );

        StringTemplate st = RuleBuilder.ruleGroup.getInstanceOf( "predicateMethod" );

        st.setAttribute( "declaration",
                         declaration );

        st.setAttribute( "declarationType",
                         declaration.getExtractor().getExtractToClass().getName().replace( '$',
                                                                                           '.' ) );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     predicateDescr.getText() );

        st.setAttribute( "methodName",
                         className );

        final String predicateText = RuleBuilder.functionFixer.fix( predicateDescr.getText() );
        st.setAttribute( "text",
                         predicateText );

        this.methods.add( st.toString() );

        st = RuleBuilder.invokerGroup.getInstanceOf( "predicateInvoker" );

        st.setAttribute( "package",
                         this.pkg.getName() );
        st.setAttribute( "ruleClassName",
                         ucFirst( this.ruleDescr.getClassName() ) );
        st.setAttribute( "invokerClassName",
                         this.ruleDescr.getClassName() + ucFirst( className ) + "Invoker" );
        st.setAttribute( "methodName",
                         className );

        st.setAttribute( "declaration",
                         declaration );
        st.setAttribute( "declarationType",
                         declaration.getExtractor().getExtractToClass().getName().replace( '$',
                                                                                           '.' ) );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     predicateDescr.getText() );

        st.setAttribute( "hashCode",
                         predicateText.hashCode() );

        final String invokerClassName = this.pkg.getName() + "." + this.ruleDescr.getClassName() + ucFirst( className ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 predicateConstraint );
        this.descrLookups.put( invokerClassName,
                               predicateDescr );
    }

    private From build(FromDescr fromDescr) {
        Column column = build( fromDescr.getReturnedColumn() );

        DeclarativeInvokerDescr invokerDescr = fromDescr.getDataSource();

        DataProvider dataProvider = null;

        if ( invokerDescr.getClass() == MethodAccessDescr.class ) {
            MethodAccessDescr methodAccessor = (MethodAccessDescr) invokerDescr;

            ValueHandler instanceValueHandler = null;
            String variableName = methodAccessor.getVariableName();
            if ( declarations.containsKey( variableName ) ) {
                instanceValueHandler = new DeclarationVariable( (Declaration) declarations.get( variableName ) );
            } else if ( this.pkg.getGlobals().containsKey( variableName ) ) {
                instanceValueHandler = new GlobalVariable( variableName,
                                                           (Class) this.pkg.getGlobals().get( variableName ) );
            } else {
                throw new IllegalArgumentException( "The variable name [" + variableName + "] was not a global or declaration." );
            }

            List arguments = ((MethodAccessDescr) invokerDescr).getArguments();
            List valueHandlers = new ArrayList();

            for ( Iterator iter = arguments.iterator(); iter.hasNext(); ) {
                valueHandlers.add( buildValueHandler( (ArgumentValueDescr) iter.next() ) );
                //                if ( desc.getType() == ArgumentValueDescr.VARIABLE ) {
                //                    if ( this.declarations.containsKey( desc.getValue() ) ) {
                //                        valueHandlers.add( new DeclarationVariable( (Declaration) declarations.get( desc.getValue() ) ) );
                //                    } else if ( this.pkg.getGlobals().containsKey( desc.getValue() ) ) {
                //                        valueHandlers.add( new GlobalVariable( (String) desc.getValue(), ( Class ) this.pkg.getGlobals().get( desc.getValue() ) ) );
                //                    } else {
                //                        throw new IllegalArgumentException( "Uknown variable: " + desc.getValue() );
                //                    }
                //                //} else if ( desc.getType() == ArgumentValueDescr.MAP ) {
                //                    //valueHandlers.add( o )
                //                } else {
                //                    // handling a literal
                //                    valueHandlers.add( new LiteralValue( (String) desc.getValue(), Object.class ) );
                //                }
            }

            MethodInvoker invoker = new MethodInvoker( methodAccessor.getMethodName(),
                                                       instanceValueHandler,
                                                       (ValueHandler[]) valueHandlers.toArray( new ValueHandler[valueHandlers.size()] ) );
            dataProvider = new MethodDataProvider( invoker );
        }
        //        if ( invokerDescr.getClass() == FieldAccessDescr.class ) {
        //            //FieldAccessDescr fieldAccessDescr = ( FieldAccessDescr ) invokerDescr;
        //        } else if ( invokerDescr.getClass() == FunctionCallDescr.class ) {
        //            //FunctionCallDescr functionCallDescr = ( FunctionCallDescr) invokerDescr;
        //        } else if ( invokerDescr.getClass() == AccessDescr.class ) {
        //            AccessDescr AccessDescr = (AccessDescr) invokerDescr;
        //            dataProvider = new DataProvider( AccessDescr.getVariableName(),
        //                                                   AccessDescr.getName(),
        //                                                   AccessDescr.getArguments(),
        //                                                   this.declarations,
        //                                                   this.pkg.getGlobals() );
        //        }

        return new From( column,
                         dataProvider );
    }

    private ValueHandler buildValueHandler(ArgumentValueDescr descr) {
        ValueHandler valueHandler = null;
        if ( descr.getType() == ArgumentValueDescr.VARIABLE ) {
            if ( this.declarations.containsKey( descr.getValue() ) ) {
                valueHandler = new DeclarationVariable( (Declaration) declarations.get( descr.getValue() ) );
            } else if ( this.pkg.getGlobals().containsKey( descr.getValue() ) ) {
                valueHandler = new GlobalVariable( (String) descr.getValue(),
                                                   (Class) this.pkg.getGlobals().get( descr.getValue() ) );
            } else {
                throw new IllegalArgumentException( "Uknown variable: " + descr.getValue() );
            }
        } else if ( descr.getType() == ArgumentValueDescr.MAP ) {
            ArgumentValueDescr.KeyValuePairDescr[] pairs = (ArgumentValueDescr.KeyValuePairDescr[]) descr.getValue();
            List list = new ArrayList( pairs.length );
            for ( int i = 0, length = pairs.length; i < length; i++ ) {
                list.add( new MapValue.KeyValuePair( buildValueHandler( pairs[i].getKey() ),
                                                     buildValueHandler( pairs[i].getValue() ) ) );
            }

            valueHandler = new MapValue( (MapValue.KeyValuePair[]) list.toArray( new MapValue.KeyValuePair[pairs.length] ) );
        } else {
            // handling a literal
            valueHandler = new LiteralValue( (String) descr.getValue(),
                                             Object.class );
        }
        return valueHandler;
    }

    private EvalCondition build(final EvalDescr evalDescr) {

        final String className = "eval" + this.counter++;
        evalDescr.setClassMethodName( className );

        final List[] usedIdentifiers = getUsedIdentifiers( evalDescr,
                                                           evalDescr.getText() );

        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedIdentifiers[0].get( i ) );
        }

        final EvalCondition eval = new EvalCondition( declarations );

        StringTemplate st = RuleBuilder.ruleGroup.getInstanceOf( "evalMethod" );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     evalDescr.getText() );

        st.setAttribute( "methodName",
                         className );

        final String evalText = RuleBuilder.functionFixer.fix( evalDescr.getText() );
        st.setAttribute( "text",
                         evalText );

        this.methods.add( st.toString() );

        st = RuleBuilder.invokerGroup.getInstanceOf( "evalInvoker" );

        st.setAttribute( "package",
                         this.pkg.getName() );
        st.setAttribute( "ruleClassName",
                         ucFirst( this.ruleDescr.getClassName() ) );
        st.setAttribute( "invokerClassName",
                         this.ruleDescr.getClassName() + ucFirst( className ) + "Invoker" );
        st.setAttribute( "methodName",
                         className );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     evalDescr.getText() );

        st.setAttribute( "hashCode",
                         evalText.hashCode() );

        final String invokerClassName = this.pkg.getName() + "." + this.ruleDescr.getClassName() + ucFirst( className ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 eval );
        this.descrLookups.put( invokerClassName,
                               evalDescr );
        return eval;
    }

    private void buildConsequence(final RuleDescr ruleDescr) {
        // generate 
        // generate Invoker
        final String className = "consequence";

        StringTemplate st = RuleBuilder.ruleGroup.getInstanceOf( "consequenceMethod" );

        st.setAttribute( "methodName",
                         className );

        final List[] usedIdentifiers = getUsedCIdentifiers( ruleDescr,
                                                            ruleDescr.getConsequence() );

        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedIdentifiers[0].get( i ) );
        }

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     ruleDescr.getConsequence() );
        st.setAttribute( "text",
                         RuleBuilder.functionFixer.fix( RuleBuilder.knowledgeHelperFixer.fix( ruleDescr.getConsequence() ) ) );

        this.methods.add( st.toString() );

        st = RuleBuilder.invokerGroup.getInstanceOf( "consequenceInvoker" );

        st.setAttribute( "package",
                         this.pkg.getName() );
        st.setAttribute( "ruleClassName",
                         ucFirst( this.ruleDescr.getClassName() ) );
        st.setAttribute( "invokerClassName",
                         ruleDescr.getClassName() + ucFirst( className ) + "Invoker" );
        st.setAttribute( "methodName",
                         className );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     ruleDescr.getConsequence() );

        // Must use the rule declarations, so we use the same order as used in the generated invoker
        final List list = Arrays.asList( this.rule.getDeclarations() );

        final int[] indexes = new int[declarations.length];
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            indexes[i] = list.indexOf( declarations[i] );
            if ( indexes[i] == -1 ) {
                // some defensive code, this should never happen
                throw new RuntimeDroolsException( "Unable to find declaration in list while generating the consequence invoker" );
            }
        }

        st.setAttribute( "indexes",
                         indexes );

        st.setAttribute( "text",
                         ruleDescr.getConsequence() );

        final String invokerClassName = this.pkg.getName() + "." + ruleDescr.getClassName() + ucFirst( className ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 this.rule );
        this.descrLookups.put( invokerClassName,
                               ruleDescr );
    }

    private void buildRule(final RuleDescr ruleDescr) {
        // If there is no compiled code, return
        if ( this.methods.isEmpty() ) {
            this.ruleClass = null;
            return;
        }
        final String lineSeparator = System.getProperty( "line.separator" );

        final StringBuffer buffer = new StringBuffer();
        buffer.append( "package " + this.pkg.getName() + ";" + lineSeparator );

        for ( final Iterator it = this.pkg.getImports().iterator(); it.hasNext(); ) {
            buffer.append( "import " + it.next() + ";" + lineSeparator );
        }

        buffer.append( "public class " + ucFirst( this.ruleDescr.getClassName() ) + " {" + lineSeparator );
        buffer.append( "    private static final long serialVersionUID  = 7952983928232702826L;" + lineSeparator );

        for ( int i = 0, size = this.methods.size() - 1; i < size; i++ ) {
            buffer.append( this.methods.get( i ) + lineSeparator );
        }

        final String[] lines = buffer.toString().split( lineSeparator );

        this.ruleDescr.setConsequenceOffset( lines.length + 2 );
        //To get the error position in the DRL
        //error.getLine() - this.ruleDescr.getConsequenceOffset() + this.ruleDescr.getConsequenceLine()

        buffer.append( this.methods.get( this.methods.size() - 1 ) + lineSeparator );
        buffer.append( "}" );

        this.ruleClass = buffer.toString();
    }

    private void setStringTemplateAttributes(final StringTemplate st,
                                             final Declaration[] declarations,
                                             final String[] globals,
                                             final String text) {
        final String[] declarationTypes = new String[declarations.length];
        for ( int i = 0, size = declarations.length; i < size; i++ ) {
            declarationTypes[i] = declarations[i].getExtractor().getExtractToClass().getName().replace( '$',
                                                                                                        '.' );
        }

        final List globalTypes = new ArrayList( globals.length );
        for ( int i = 0, length = globals.length; i < length; i++ ) {
            globalTypes.add( ((Class) this.pkg.getGlobals().get( globals[i] )).getName().replace( '$',
                                                                                                  '.' ) );
        }

        st.setAttribute( "declarations",
                         declarations );
        st.setAttribute( "declarationTypes",
                         declarationTypes );

        st.setAttribute( "globals",
                         globals );
        st.setAttribute( "globalTypes",
                         globalTypes );
    }

    private String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    private FieldExtractor getFieldExtractor(final PatternDescr descr,
                                             final ObjectType objectType,
                                             final String fieldName) {
        FieldExtractor extractor = null;

        if ( objectType.getValueType() == ValueType.FACTTEMPLATE_TYPE ) {
            //@todo use extractor cache            
            FactTemplate factTemplate = ((FactTemplateObjectType) objectType).getFactTemplate();
            extractor = new FactTemplateFieldExtractor( factTemplate,
                                                        factTemplate.getFieldTemplateIndex( fieldName ) );
        } else {
            try {
                extractor = classFieldExtractorCache.getExtractor( ((ClassObjectType) objectType).getClassType(),
                                                                   fieldName );
            } catch ( final RuntimeDroolsException e ) {
                this.errors.add( new RuleError( this.rule,
                                                descr,
                                                e,
                                                "Unable to create Field Extractor for '" + fieldName + "'" ) );
            }
        }

        return extractor;
    }

    private Evaluator getEvaluator(final PatternDescr descr,
                                   final ValueType valueType,
                                   final String evaluatorString) {

        final Evaluator evaluator = valueType.getEvaluator( Operator.determineOperator( evaluatorString ) );

        if ( evaluator == null ) {
            this.errors.add( new RuleError( this.rule,
                                            descr,
                                            null,
                                            "Unable to determine the Evaluator for  '" + valueType + "' and '" + evaluatorString + "'" ) );
        }

        return evaluator;
    }

    private List[] getUsedIdentifiers(final PatternDescr descr,
                                      final String text) {
        List[] usedIdentifiers = null;
        try {
            usedIdentifiers = this.analyzer.analyzeExpression( text,
                                                               new Set[]{this.declarations.keySet(), this.pkg.getGlobals().keySet()} );
        } catch ( final Exception e ) {
            this.errors.add( new RuleError( this.rule,
                                            descr,
                                            null,
                                            "Unable to determine the used declarations" ) );
        }
        return usedIdentifiers;
    }

    private List[] getUsedCIdentifiers(final PatternDescr descr,
                                       final String text) {
        List[] usedIdentifiers = null;
        try {
            usedIdentifiers = this.analyzer.analyzeBlock( text,
                                                          new Set[]{this.declarations.keySet(), this.pkg.getGlobals().keySet()} );
        } catch ( final Exception e ) {
            this.errors.add( new RuleError( this.rule,
                                            descr,
                                            null,
                                            "Unable to determine the used declarations" ) );
        }
        return usedIdentifiers;
    }

    static class ColumnCounter {
        // we start with -1 so that we can ++this.value - otherwise the first element has a lower value than the second in an 'or'
        private int          value = -1;

        private GroupElement ge;

        public void setParent(final GroupElement ge) {
            this.ge = ge;
        }

        public int getNext() {
            return ++this.value;
        }
    }
}