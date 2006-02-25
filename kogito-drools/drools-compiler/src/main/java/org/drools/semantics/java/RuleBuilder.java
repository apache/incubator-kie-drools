package org.drools.semantics.java;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.readers.MemoryResourceReader;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.drools.CheckedDroolsException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.EvaluatorFactory;
import org.drools.base.FieldFactory;
import org.drools.lang.descr.BoundVariableDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElement;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.BoundVariableConstraint;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Package;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class RuleBuilder {
    private final Configuration cfg;

    private Package             pkg;
    private Rule                rule;
    private RuleDescr           ruleDescr;

    public String               ruleClass;
    public List                 methods;
    public Map                  invokers;

    private Map                 declarations;

    private int                 counter;

    private int                 columnCounter;

    // @todo move to an interface so it can work as a decorator
    private JavaExprAnalyzer    analyzer = new JavaExprAnalyzer();

    public RuleBuilder() {
        cfg = new Configuration();
        cfg.setClassForTemplateLoading( getClass(),
                                        "" );
    }

    public synchronized Rule build(Package pkg,
                                   RuleDescr ruleDescr) throws CheckedDroolsException {
        this.pkg = pkg;
        this.methods = new ArrayList();
        this.invokers = new HashMap();
        this.declarations = ruleDescr.getDeclarations();

        this.rule = new Rule( ruleDescr.getName() );
        this.ruleDescr = ruleDescr;

        try {
            build( rule,
                   ruleDescr.getLhs() );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new CheckedDroolsException( e );
        }

        return rule;
    }

    public Map getInvokers() {
        return this.invokers;
    }

    public List getMethods() {
        return this.methods;
    }

    public String getRuleClass() {
        return this.ruleClass;
    }

    public Rule getRule() {
        return this.rule;
    }

    public Package getPackage() {
        return this.pkg;
    }

    private void build(Rule rule,
                       ConditionalElement descr) throws Exception {
        for ( Iterator it = descr.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof ConditionalElement ) {
                build( rule,
                       (ConditionalElement) object );
            } else if ( object instanceof ColumnDescr ) {
                build( rule,
                       (ColumnDescr) object );
            }
        }
    }

    private void build(Rule rule,
                       ColumnDescr columnDescr) throws IOException,
                                               TemplateException,
                                               TokenStreamException,
                                               RecognitionException,
                                               ClassNotFoundException,
                                               IntrospectionException,
                                               InvalidRuleException {
        Class clazz = Class.forName( columnDescr.getObjectType() );
        Column column;
        if ( columnDescr.getIdentifier() != null && !columnDescr.getIdentifier().equals( "" ) ) {
            column = new Column( columnCounter++,
                                 new ClassObjectType( clazz ),
                                 columnDescr.getIdentifier() );;
            this.declarations.put( column.getDeclaration().getIdentifier(),
                                   column.getDeclaration() );
        } else {
            column = new Column( columnCounter++,
                                 new ClassObjectType( clazz ) );
        }

        for ( Iterator it = columnDescr.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
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
            } else if ( object instanceof EvalDescr ) {
                build( (EvalDescr) object );
            }
        }

        build( rule,
               ruleDescr );
    }

    private void build(Column column,
                       FieldBindingDescr fieldBinding) throws ClassNotFoundException,
                                                      IntrospectionException {
        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            fieldBinding.getFieldName() );

        Declaration declaration = column.addDeclaration( fieldBinding.getIdentifier(),
                                                         extractor );

        this.declarations.put( declaration.getIdentifier(),
                               declaration );
    }

    private void build(Column column,
                       BoundVariableDescr boundVariable) {

        boundVariable.getDeclarationIdentifier();

        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            boundVariable.getFieldName() );

        Declaration declaration = (Declaration) this.declarations.get( boundVariable.getDeclarationIdentifier() );

        Evaluator evaluator = EvaluatorFactory.getEvaluator( declaration.getObjectType().getValueType(),
                                                             boundVariable.getEvaluator() );

        column.addConstraint( new BoundVariableConstraint( extractor,
                                                           declaration,
                                                           evaluator ) );
    }

    private void build(Column column,
                       LiteralDescr literalDescr) {

        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            literalDescr.getFieldName() );

        FieldValue field = FieldFactory.getFieldValue( literalDescr.getText(),
                                                       extractor.getObjectType().getValueType() );

        Evaluator evaluator = EvaluatorFactory.getEvaluator( extractor.getObjectType().getValueType(),
                                                             literalDescr.getEvaluator() );

        column.addConstraint( new LiteralConstraint( field,
                                                     extractor,
                                                     evaluator ) );
    }

    private void build(Column column,
                       ReturnValueDescr returnValueDescr) throws IOException,
                                                         TemplateException,
                                                         TokenStreamException,
                                                         RecognitionException {
        // generate method
        // generate invoker
        Map root = new HashMap();

        String classMethodName = "returnValue" + counter++;
        returnValueDescr.setClassMethodName( classMethodName );

        root.put( "package",
                  this.pkg.getName() );
        root.put( "ruleClassName",
                  ucFirst( this.ruleDescr.getClassName() ) );
        root.put( "invokerClassName",
                  ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        root.put( "methodName",
                  classMethodName );

        List usedDeclarations = this.analyzer.analyze( returnValueDescr.getText(),
                                                       this.declarations.keySet() );

        Declaration[] declarations = new Declaration[usedDeclarations.size()];
        for ( int i = 0, size = usedDeclarations.size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedDeclarations.get( i ) );
        }

        root.put( "declarations",
                  declarations );

        root.put( "globals",
                  getUsedGlobals( returnValueDescr.getText() ) );

        root.put( "globalTypes",
                  this.pkg.getGlobals() );

        root.put( "text",
                  returnValueDescr.getText() );

        ClassFieldExtractor extractor = new ClassFieldExtractor( ((ClassObjectType) column.getObjectType()).getClassType(),
                                                                 returnValueDescr.getFieldName() );

        Evaluator evaluator = EvaluatorFactory.getEvaluator( extractor.getObjectType().getValueType(),
                                                             returnValueDescr.getEvaluator() );

        ReturnValueConstraint returnValueConstraint = new ReturnValueConstraint( extractor,
                                                                                 declarations,
                                                                                 evaluator );
        column.addConstraint( returnValueConstraint );

        Template template = this.cfg.getTemplate( "returnValueMethod.ftl" );
        StringWriter string = new StringWriter();
        template.process( root,
                          string );
        string.flush();

        this.methods.add( string.toString() );

        template = this.cfg.getTemplate( "returnValueInvoker.ftl" );
        string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        this.invokers.put( ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" ,
                           string.toString() );
    }

    private void build(Column column,
                       PredicateDescr predicateDescr) throws TemplateException,
                                                     IOException,
                                                     TokenStreamException,
                                                     RecognitionException {
        // generate method
        // generate invoker
        Map root = new HashMap();

        String classMethodName = "predicate" + counter++;
        predicateDescr.setClassMethodName( classMethodName );

        root.put( "package",
                  this.pkg.getName() );
        root.put( "ruleClassName",
                  ucFirst( this.ruleDescr.getClassName() ) );
        root.put( "invokerClassName",
                  ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        root.put( "methodName",
                  classMethodName );
        
////////////////
        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            predicateDescr.getFieldName() );

        Declaration declaration = column.addDeclaration( predicateDescr.getDeclaration(),
                                                         extractor );

        this.declarations.put( declaration.getIdentifier(),
                               declaration );        
////////////////        

        List usedDeclarations = this.analyzer.analyze( predicateDescr.getText(),
                                                       this.declarations.keySet() );

        // Don't include the focus declaration, that hasn't been merged into the tuple yet.
        usedDeclarations.remove( predicateDescr.getDeclaration() );

        Declaration[] declarations = new Declaration[usedDeclarations.size()];
        for ( int i = 0, size = usedDeclarations.size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedDeclarations.get( i ) );
        }

        root.put( "declarations",
                  declarations );

        root.put( "declaration",
                  declaration );

        root.put( "globals",
                  getUsedGlobals( predicateDescr.getText() ) );

        root.put( "globalTypes",
                  this.pkg.getGlobals() );

        root.put( "text",
                  predicateDescr.getText() );

        PredicateConstraint predicateConstraint = new PredicateConstraint( declaration,
                                                                           declarations );
        column.addConstraint( predicateConstraint );

        Template template = this.cfg.getTemplate( "predicateMethod.ftl" );
        StringWriter string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        this.methods.add( string.toString() );

        template = this.cfg.getTemplate( "predicateInvoker.ftl" );
        string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        this.invokers.put( ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker",
                           string.toString() );
    }

    private void build(EvalDescr evalDescr) throws TokenStreamException,
                                           RecognitionException,
                                           IOException,
                                           TemplateException,
                                           InvalidRuleException {
        // generate method
        // generate invoker
        Map root = new HashMap();

        String classMethodName = "eval" + counter++;
        evalDescr.setClassMethodName( classMethodName );

        root.put( "package",
                  this.pkg.getName() );
        root.put( "ruleClassName",
                  ucFirst( this.ruleDescr.getClassName() ) );
        root.put( "invokerClassName",
                  ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        root.put( "methodName",
                  classMethodName);

        List usedDeclarations = this.analyzer.analyze( evalDescr.getText(),
                                                       this.declarations.keySet() );

        Declaration[] declarations = new Declaration[usedDeclarations.size()];
        for ( int i = 0, size = usedDeclarations.size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedDeclarations.get( i ) );
        }

        root.put( "declarations",
                  declarations );

        root.put( "globals",
                  getUsedGlobals( evalDescr.getText() ) );

        root.put( "globalTypes",
                  this.pkg.getGlobals() );

        root.put( "text",
                  evalDescr.getText() );

        EvalCondition eval = new EvalCondition( declarations );
        rule.addPattern( eval );

        Template template = this.cfg.getTemplate( "evalMethod.ftl" );
        StringWriter string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        this.methods.add( string.toString() );

        template = this.cfg.getTemplate( "evalInvoker.ftl" );
        string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        this.invokers.put( ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker",
                           string.toString() );
    }

    private void build(Rule rule,
                       RuleDescr ruleDescr) throws IOException,
                                           TokenStreamException,
                                           RecognitionException,
                                           TemplateException {
        // generate method
        // generate invoker
        Map root = new HashMap();

        String classMethodName = "consequence";

        root.put( "package",
                  this.pkg.getName() );
        root.put( "ruleClassName",
                  ucFirst( this.ruleDescr.getClassName() ) );
        root.put( "invokerClassName",
                  ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        root.put( "methodName",
                  classMethodName );

        root.put( "declarations",
                  this.declarations.values() );

        root.put( "globals",
                  getUsedGlobals( ruleDescr.getConsequence() ) );

        root.put( "globalTypes",
                  this.pkg.getGlobals() );

        // @todo: add in michael's regexpr to make modifies more efficient
        root.put( "text",
                  ruleDescr.getConsequence() );

        Template template = this.cfg.getTemplate( "consequenceMethod.ftl" );
        StringWriter string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        this.methods.add( string.toString() );

        template = this.cfg.getTemplate( "consequenceInvoker.ftl" );
        string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        this.invokers.put( ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" ,
                           string.toString() );

        template = this.cfg.getTemplate( "ruleClass.ftl" );
        root.put( "imports",
                  this.pkg.getImports() );
        root.put( "methods",
                  this.methods );
        string = new StringWriter();
        template.process( root,
                          string );
        string.flush();

        this.ruleClass = string.toString();
    }

    private List getUsedGlobals(String text) {
        List list = new ArrayList( 1 );
        Map globals = this.pkg.getGlobals();
        for ( Iterator it = globals.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();

            // poor mans check. Only add the application variable if it appears as text in the script
            if ( text.indexOf( key ) == -1 ) {
                continue;
            }
            Class clazz = (Class) globals.get( key );

            String type = clazz.getName();
            int nestedClassPosition = type.indexOf( '$' );

            if ( nestedClassPosition != -1 ) {
                type = type.substring( 0,
                                       nestedClassPosition );
            }

            if ( !list.contains( key ) ) {
                list.add( key );
            }
        }

        return list;
    }

    private String ucFirst(String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    private String lcFirst(String name) {
        return name.toLowerCase().charAt( 0 ) + name.substring( 1 );
    }
}
