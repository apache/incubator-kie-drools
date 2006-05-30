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
import org.drools.base.ClassObjectType;
import org.drools.base.EvaluatorFactory;
import org.drools.base.FieldFactory;
import org.drools.base.FieldImpl;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BoundVariableDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.And;
import org.drools.rule.BoundVariableConstraint;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Exists;
import org.drools.rule.GroupElement;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Or;
import org.drools.rule.Package;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Query;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.TypeResolver;

public class RuleBuilder {
    private Package                     pkg;
    private Rule                        rule;
    private RuleDescr                   ruleDescr;

    public String                       ruleClass;
    public List                         methods;
    public Map                          invokers;

    private Map                         invokerLookups;

    private Map                         descrLookups;

    private Map                         declarations;

    private int                         counter;

    private ColumnCounter               columnCounter;

    private int                         columnOffset;

    private List                        errors;

    private TypeResolver                typeResolver;

    private Map                         notDeclarations;

    private static final StringTemplateGroup  ruleGroup            = new StringTemplateGroup( new InputStreamReader( RuleBuilder.class.getResourceAsStream( "javaRule.stg" ) ),
                                                                                        AngleBracketTemplateLexer.class );

    private static final StringTemplateGroup  invokerGroup         = new StringTemplateGroup( new InputStreamReader( RuleBuilder.class.getResourceAsStream( "javaInvokers.stg" ) ),
                                                                                        AngleBracketTemplateLexer.class );

    private static final KnowledgeHelperFixer knowledgeHelperFixer = new KnowledgeHelperFixer();
    private static final FunctionFixer        functionFixer        = new FunctionFixer();

    // @todo move to an interface so it can work as a decorator
    private final JavaExprAnalyzer            analyzer             = new JavaExprAnalyzer();

    public RuleBuilder() {
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

        this.typeResolver = new ClassTypeResolver( pkg.getImports(),
                                                   pkg.getPackageCompilationData().getClassLoader() );
        // make an automatic import for the current package
        this.typeResolver.addImport( pkg.getName() + ".*" );
        this.typeResolver.addImport( "java.lang.*" );

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
        // generate invoker, methods
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
                if ( object instanceof AndDescr ) {
                    final And and = new And();
                    this.columnCounter.setParent( and );
                    build( this.rule,
                           (ConditionalElementDescr) object,
                           and,
                           false,
                           false );
                    this.rule.addPattern( and );
                } else if ( object instanceof OrDescr ) {
                    final Or or = new Or();
                    this.columnCounter.setParent( or );
                    build( this.rule,
                           (ConditionalElementDescr) object,
                           or,
                           false,
                           false );
                    this.rule.addPattern( or );
                } else if ( object instanceof NotDescr ) {
                    // We cannot have declarations created inside a not visible outside it, so track no declarations so they can be removed
                    this.notDeclarations = new HashMap();
                    final Not not = new Not();
                    this.columnCounter.setParent( not );
                    build( this.rule,
                           (ConditionalElementDescr) object,
                           not,
                           true,
                           true );
                    this.rule.addPattern( not );

                    // remove declarations bound inside not node
                    for ( final Iterator notIt = this.notDeclarations.keySet().iterator(); notIt.hasNext(); ) {
                        this.declarations.remove( notIt.next() );
                    }

                    this.notDeclarations = null;
                } else if ( object instanceof ExistsDescr ) {
                    // We cannot have declarations created inside a not visible outside it, so track no declarations so they can be removed
                    this.notDeclarations = new HashMap();
                    final Exists exists = new Exists();
                    this.columnCounter.setParent( exists );
                    build( this.rule,
                           (ConditionalElementDescr) object,
                           exists,
                           true,
                           true );
                    // remove declarations bound inside not node
                    for ( final Iterator notIt = this.notDeclarations.keySet().iterator(); notIt.hasNext(); ) {
                        this.declarations.remove( notIt.next() );
                    }

                    this.notDeclarations = null;
                    this.rule.addPattern( exists );
                } else if ( object instanceof EvalDescr ) {
                    final EvalCondition eval = build( (EvalDescr) object );
                    if ( eval != null ) {
                        this.rule.addPattern( eval );
                    }
                }
            } else if ( object instanceof ColumnDescr ) {
                final Column column = build( (ColumnDescr) object );
                if ( column != null ) {
                    this.rule.addPattern( column );
                }
            }
        }

        // Build the consequence and generate it's invoker/methods
        // generate the main rule from the previously generated methods.
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
                if ( object instanceof AndDescr ) {
                    final And and = new And();
                    this.columnCounter.setParent( and );
                    build( rule,
                           (ConditionalElementDescr) object,
                           and,
                           false,
                           false );
                    ce.addChild( and );
                } else if ( object instanceof OrDescr ) {
                    final Or or = new Or();
                    this.columnCounter.setParent( or );
                    build( rule,
                           (ConditionalElementDescr) object,
                           or,
                           false,
                           false );
                    ce.addChild( or );
                } else if ( object instanceof NotDescr ) {
                    final Not not = new Not();
                    this.columnCounter.setParent( not );
                    build( rule,
                           (ConditionalElementDescr) object,
                           not,
                           true,
                           true );
                    ce.addChild( not );
                } else if ( object instanceof ExistsDescr ) {
                    final Exists exists = new Exists();
                    this.columnCounter.setParent( exists );
                    build( rule,
                           (ConditionalElementDescr) object,
                           exists,
                           true,
                           true );
                    ce.addChild( exists );
                } else if ( object instanceof EvalDescr ) {
                    final EvalCondition eval = build( (EvalDescr) object );
                    if ( eval != null ) {
                        ce.addChild( eval );
                    }
                }
            } else if ( object instanceof ColumnDescr ) {
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

        Class clazz = null;

        try {
            //clazz = Class.forName( columnDescr.getObjectType() );
            clazz = this.typeResolver.resolveType( columnDescr.getObjectType() );
        } catch ( final ClassNotFoundException e ) {
            this.errors.add( new RuleError( this.rule,
                                            columnDescr,
                                            null,
                                            "Unable to resolve ObjectType '" + columnDescr.getObjectType() + "'" ) );
            return null;
        }

        Column column;
        if ( columnDescr.getIdentifier() != null && !columnDescr.getIdentifier().equals( "" ) ) {
            column = new Column( this.columnCounter.getNext(),
                                 this.columnOffset,
                                 new ClassObjectType( clazz ),
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
                                 new ClassObjectType( clazz ),
                                 null );
        }

        for ( final Iterator it = columnDescr.getDescrs().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof FieldBindingDescr ) {
                build( column,
                       (FieldBindingDescr) object );
            } else if ( object instanceof LiteralDescr ) {
                build( column,
                       (LiteralDescr) object );
            } else if ( object instanceof BoundVariableDescr ) {
                build( column,
                       (BoundVariableDescr) object );
            } else if ( object instanceof ReturnValueDescr ) {
                build( column,
                       (ReturnValueDescr) object );
            } else if ( object instanceof PredicateDescr ) {
                build( column,
                       (PredicateDescr) object );
            }
        }
        return column;
    }

    private void build(final Column column,
                       final FieldBindingDescr fieldBindingDescr) {
        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final FieldExtractor extractor = getFieldExtractor( fieldBindingDescr,
                                                      clazz,
                                                      fieldBindingDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        final Declaration declaration = column.addDeclaration( fieldBindingDescr.getIdentifier(),
                                                         extractor );

        this.declarations.put( declaration.getIdentifier(),
                               declaration );

        if ( this.notDeclarations != null ) {
            this.notDeclarations.put( declaration.getIdentifier(),
                                      declaration );
        }
    }

    private void build(final Column column,
                       final BoundVariableDescr boundVariableDescr) {
        if ( boundVariableDescr.getIdentifier() == null || boundVariableDescr.getIdentifier().equals( "" ) ) {
            this.errors.add( new RuleError( this.rule,
                                            boundVariableDescr,
                                            null,
                                            "Identifier not defined for binding field '" + boundVariableDescr.getFieldName() + "'" ) );
            return;
        }

        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final FieldExtractor extractor = getFieldExtractor( boundVariableDescr,
                                                      clazz,
                                                      boundVariableDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        final Declaration declaration = (Declaration) this.declarations.get( boundVariableDescr.getIdentifier() );

        if ( declaration == null ) {
            this.errors.add( new RuleError( this.rule,
                                            boundVariableDescr,
                                            null,
                                            "Unable to return Declaration for identifier '" + boundVariableDescr.getIdentifier() + "'" ) );
            return;
        }

        final Evaluator evaluator = getEvaluator( boundVariableDescr,
                                            extractor.getObjectType().getValueType(),
                                            boundVariableDescr.getEvaluator() );
        if ( evaluator == null ) {
            return;
        }

        column.addConstraint( new BoundVariableConstraint( extractor,
                                                           declaration,
                                                           evaluator ) );
    }

    private void build(final Column column,
                       final LiteralDescr literalDescr) {

        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final FieldExtractor extractor = getFieldExtractor( literalDescr,
                                                      clazz,
                                                      literalDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        FieldValue field = null;
        if ( literalDescr.isStaticFieldValue() ) {
            final int lastDot = literalDescr.getText().lastIndexOf( '.' );
            final String className = literalDescr.getText().substring( 0,
                                                                 lastDot );
            final String fieldName = literalDescr.getText().substring( lastDot + 1 );
            try {
                final Class staticClass = this.typeResolver.resolveType( className );
                field = new FieldImpl( staticClass.getField( fieldName ).get( null ) );
            } catch ( final ClassNotFoundException e ) {
                this.errors.add( new RuleError( this.rule,
                                                literalDescr,
                                                e,
                                                e.getMessage() ) );
            } catch ( final Exception e ) {
                this.errors.add( new RuleError( this.rule,
                                                literalDescr,
                                                e,
                                                "Unable to create a Field value of type  '" + extractor.getObjectType().getValueType() + "' and value '" + literalDescr.getText() + "'" ) );
            }

        } else {
            try {
                field = FieldFactory.getFieldValue( literalDescr.getText(),
                                                    extractor.getObjectType().getValueType() );
            } catch ( final Exception e ) {
                this.errors.add( new RuleError( this.rule,
                                                literalDescr,
                                                e,
                                                "Unable to create a Field value of type  '" + extractor.getObjectType().getValueType() + "' and value '" + literalDescr.getText() + "'" ) );
            }
        }

        final Evaluator evaluator = getEvaluator( literalDescr,
                                            extractor.getObjectType().getValueType(),
                                            literalDescr.getEvaluator() );
        if ( evaluator == null ) {
            return;
        }

        column.addConstraint( new LiteralConstraint( field,
                                                     extractor,
                                                     evaluator ) );
    }

    private void build(final Column column,
                       final ReturnValueDescr returnValueDescr) {
        final String classMethodName = "returnValue" + this.counter++;
        returnValueDescr.setClassMethodName( classMethodName );

        final List[] usedIdentifiers = getUsedIdentifiers( returnValueDescr,
                                                     returnValueDescr.getText() );

        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedIdentifiers[0].get( i ) );
        }

        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();
        final FieldExtractor extractor = getFieldExtractor( returnValueDescr,
                                                      clazz,
                                                      returnValueDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        final Evaluator evaluator = getEvaluator( returnValueDescr,
                                            extractor.getObjectType().getValueType(),
                                            returnValueDescr.getEvaluator() );
        if ( evaluator == null ) {
            return;
        }

        final ReturnValueConstraint returnValueConstraint = new ReturnValueConstraint( extractor,
                                                                                 declarations,
                                                                                 evaluator );
        column.addConstraint( returnValueConstraint );

        StringTemplate st = RuleBuilder.ruleGroup.getInstanceOf( "returnValueMethod" );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     returnValueDescr.getText() );

        st.setAttribute( "methodName",
                         classMethodName );

        final String returnValueText = RuleBuilder.functionFixer.fix( returnValueDescr.getText() );
        st.setAttribute( "text",
                         returnValueText );

        this.methods.add( st.toString() );

        st = RuleBuilder.invokerGroup.getInstanceOf( "returnValueInvoker" );

        st.setAttribute( "package",
                         this.pkg.getName() );
        st.setAttribute( "ruleClassName",
                         ucFirst( this.ruleDescr.getClassName() ) );
        st.setAttribute( "invokerClassName",
                         this.ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        st.setAttribute( "methodName",
                         classMethodName );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     returnValueDescr.getText() );

        st.setAttribute( "hashCode",
                         returnValueText.hashCode() );

        final String invokerClassName = this.pkg.getName() + "." + this.ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 returnValueConstraint );
        this.descrLookups.put( invokerClassName,
                               returnValueDescr );
    }

    private void build(final Column column,
                       final PredicateDescr predicateDescr) {
        // generate method
        // generate Invoker
        final String classMethodName = "predicate" + this.counter++;
        predicateDescr.setClassMethodName( classMethodName );

        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final FieldExtractor extractor = getFieldExtractor( predicateDescr,
                                                      clazz,
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
                         ((ClassObjectType) declaration.getObjectType()).getClassType().getName().replace( '$',
                                                                                                           '.' ) );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     predicateDescr.getText() );

        st.setAttribute( "methodName",
                         classMethodName );

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
                         this.ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        st.setAttribute( "methodName",
                         classMethodName );

        st.setAttribute( "declaration",
                         declaration );
        st.setAttribute( "declarationType",
                         ((ClassObjectType) declaration.getObjectType()).getClassType().getName().replace( '$',
                                                                                                           '.' ) );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     predicateDescr.getText() );

        st.setAttribute( "hashCode",
                         predicateText.hashCode() );

        final String invokerClassName = this.pkg.getName() + "." + this.ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 predicateConstraint );
        this.descrLookups.put( invokerClassName,
                               predicateDescr );
    }

    private EvalCondition build(final EvalDescr evalDescr) {

        final String classMethodName = "eval" + this.counter++;
        evalDescr.setClassMethodName( classMethodName );

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
                         classMethodName );

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
                         this.ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        st.setAttribute( "methodName",
                         classMethodName );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     evalDescr.getText() );

        st.setAttribute( "hashCode",
                         evalText.hashCode() );

        final String invokerClassName = this.pkg.getName() + "." + this.ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 eval );
        this.descrLookups.put( invokerClassName,
                               evalDescr );
        return eval;
    }

    private void buildConsequence(final RuleDescr ruleDescr) {
        // generate method
        // generate Invoker
        final String classMethodName = "consequence";

        StringTemplate st = RuleBuilder.ruleGroup.getInstanceOf( "consequenceMethod" );

        st.setAttribute( "methodName",
                         classMethodName );

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
                         ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        st.setAttribute( "methodName",
                         classMethodName );

        setStringTemplateAttributes( st,
                                     declarations,
                                     (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                     ruleDescr.getConsequence() );

        final List list = Arrays.asList( this.rule.getDeclarations() );

        final int[] indexes = new int[declarations.length];
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            indexes[i] = list.indexOf( declarations[i] );
        }

        st.setAttribute( "indexes",
                         indexes );

        st.setAttribute( "text",
                         ruleDescr.getConsequence() );

        final String invokerClassName = this.pkg.getName() + "." + ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
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
            declarationTypes[i] = ((ClassObjectType) declarations[i].getObjectType()).getClassType().getName().replace( '$',
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
                                             final Class clazz,
                                             final String fieldName) {
        FieldExtractor extractor = null;
        try {
            extractor = new ClassFieldExtractor( clazz,
                                                 fieldName );
        } catch ( final RuntimeDroolsException e ) {
            this.errors.add( new RuleError( this.rule,
                                            descr,
                                            e,
                                            "Unable to create Field Extractor for '" + fieldName + "'" ) );
        }

        return extractor;
    }

    private Evaluator getEvaluator(final PatternDescr descr,
                                   final int valueType,
                                   final String evaluatorString) {
        final Evaluator evaluator = EvaluatorFactory.getEvaluator( valueType,
                                                             evaluatorString );

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

        private boolean      orCheck;

        private GroupElement ge;

        public void setParent(final GroupElement ge) {
            this.ge = ge;
            this.orCheck = false;
        }

        public int getNext() {
            if ( this.ge != null && this.ge.getClass() == Or.class ) {
                if ( !this.orCheck ) {
                    this.orCheck = true;
                    return ++this.value;
                } else {
                    return this.value;
                }
            } else {
                return ++this.value;
            }
        }
    }
}