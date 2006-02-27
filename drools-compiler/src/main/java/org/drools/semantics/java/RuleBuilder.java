package org.drools.semantics.java;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;
import org.drools.CheckedDroolsException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.EvaluatorFactory;
import org.drools.base.FieldFactory;
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
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.And;
import org.drools.rule.BoundVariableConstraint;
import org.drools.rule.Column;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Exists;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Or;
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
    public Map                  invokeables;

    public Map                  referenceLookups;

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

    public Map getInvokeables() {
        return this.invokeables;
    }

    public List getMethods() {
        return this.methods;
    }

    public String getRuleClass() {
        return this.ruleClass;
    }

    public Map getReferenceLookups() {
        return this.referenceLookups;
    }

    public Rule getRule() {
        return this.rule;
    }

    public Package getPackage() {
        return this.pkg;
    }

    public synchronized Rule build(Package pkg,
                                   RuleDescr ruleDescr) throws CheckedDroolsException {
        this.pkg = pkg;
        this.methods = new ArrayList();
        this.invokeables = new HashMap();
        this.referenceLookups = new HashMap();
        this.declarations = ruleDescr.getDeclarations();

        this.rule = new Rule( ruleDescr.getName() );
        this.ruleDescr = ruleDescr;

        // Assign attributes
        setAttributes( rule,
                       ruleDescr.getAttributes() );

        try {
            // Build the left hand side
            // generate invokers, methods
            build( rule,
                   ruleDescr.getLhs(),
                   rule.getLhs() );
            // Build the consequence and generate it's invokers/methods
            // generate the main rule from the previously generated methods.
            build( rule,
                   ruleDescr );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new CheckedDroolsException( e );
        }

        return rule;
    }

    private void setAttributes(Rule rule,
                               List attributes) {
        for ( Iterator it = attributes.iterator(); it.hasNext(); ) {
            AttributeDescr attributeDescr = (AttributeDescr) it.next();
            String name = attributeDescr.getName();
            if ( name.equals( "salience" ) ) {
                rule.setSalience( Integer.parseInt( attributeDescr.getValue() ) );
            } else if ( name.equals( "no-loop" ) ) {
                rule.setNoLoop( Boolean.valueOf( attributeDescr.getValue() ).booleanValue() );
            } else if ( name.equals( "agenda-group" ) ) {
                rule.setAgendaGroup( attributeDescr.getValue() );
            } else if ( name.equals( "duration" ) ) {
                //@todo: need  to do this before the release
                //duration rules cannot be partitioned into an agenda group
                rule.setAgendaGroup( "" );
            } else if ( name.equals( "language" ) ) {
                //@todo: we don't currently  support multiple languages
            }
        }
    }

    private void build(Rule rule,
                       ConditionalElementDescr descr,
                       ConditionalElement ce) throws Exception {
        for ( Iterator it = descr.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof ConditionalElementDescr ) {
                if ( object instanceof AndDescr ) {
                    And and = new And();
                    ce.addChild( and );
                    build( rule,
                           (ConditionalElementDescr) object,
                           and );
                } else if ( object instanceof OrDescr ) {
                    Or or = new Or();
                    ce.addChild( or );
                    build( rule,
                           (ConditionalElementDescr) object,
                           or );
                } else if ( object instanceof NotDescr ) {
                    Not not = new Not();
                    ce.addChild( not );
                    build( rule,
                           (ConditionalElementDescr) object,
                           not );
                } else if ( object instanceof ExistsDescr ) {
                    Exists exists = new Exists();
                    ce.addChild( exists );
                    build( rule,
                           (ConditionalElementDescr) object,
                           exists );
                } else if ( object instanceof EvalDescr ) {
                    build( ce,
                           (EvalDescr) object );
                }
            } else if ( object instanceof ColumnDescr ) {
                build( ce,
                       (ColumnDescr) object );
            }
        }
    }

    private void build(ConditionalElement ce,
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
            }
        }

        ce.addChild( column );
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

        String invokerClassName = ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokeables.put( invokerClassName,
                              string.toString() );
        this.referenceLookups.put( this.pkg.getName() + "." + invokerClassName,
                                   returnValueConstraint );
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

        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            predicateDescr.getFieldName() );

        Declaration declaration = column.addDeclaration( predicateDescr.getDeclaration(),
                                                         extractor );

        this.declarations.put( declaration.getIdentifier(),
                               declaration );

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

        String invokerClassName = ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokeables.put( invokerClassName,
                              string.toString() );
        this.referenceLookups.put( this.pkg.getName() + "." + invokerClassName,
                                   predicateConstraint );
    }

    private void build(ConditionalElement ce,
                       EvalDescr evalDescr) throws TokenStreamException,
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
                  classMethodName );

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
        ce.addChild( eval );

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

        String invokerClassName = ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokeables.put( invokerClassName,
                              string.toString() );
        this.referenceLookups.put( this.pkg.getName() + "." + invokerClassName,
                                   eval );
    }

    //    private void build(ConditionalElement ce,
    //                       EvalDescr evalDescr) throws TokenStreamException,
    //                                           RecognitionException,
    //                                           IOException,
    //                                           TemplateException,
    //                                           InvalidRuleException {
    //        // generate method
    //        // generate invoker
    //        Map root = new HashMap();
    //
    //        String classMethodName = "eval" + counter++;
    //        evalDescr.setClassMethodName( classMethodName );
    //
    //        root.put( "package",
    //                  this.pkg.getName() );
    //        root.put( "ruleClassName",
    //                  ucFirst( this.ruleDescr.getClassName() ) );
    //        root.put( "invokerClassName",
    //                  ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
    //        root.put( "methodName",
    //                  classMethodName );
    //
    //        List usedDeclarations = this.analyzer.analyze( evalDescr.getText(),
    //                                                       this.declarations.keySet() );
    //
    //        Declaration[] declarations = new Declaration[usedDeclarations.size()];
    //        for ( int i = 0, size = usedDeclarations.size(); i < size; i++ ) {
    //            declarations[i] = (Declaration) this.declarations.get( (String) usedDeclarations.get( i ) );
    //        }
    //
    //        root.put( "declarations",
    //                  declarations );
    //
    //        root.put( "globals",
    //                  getUsedGlobals( evalDescr.getText() ) );
    //
    //        root.put( "globalTypes",
    //                  this.pkg.getGlobals() );
    //
    //        root.put( "text",
    //                  evalDescr.getText() );
    //
    //        EvalCondition eval = new EvalCondition( declarations );
    //        ce.addChild( eval );
    //
    //        StringTemplateGroup group = new StringTemplateGroup( new InputStreamReader( getClass().getResourceAsStream( "javaMethods.stg" ) ),
    //                                                             AngleBracketTemplateLexer.class );
    //        StringTemplate st = group.getInstanceOf( "evalMethod" );
    //
    //        String[] declarationTypes = new String[declarations.length];
    //        for ( int i = 0, size = declarations.length; i < size; i++ ) {
    //            declarationTypes[i] = ((ClassObjectType) declarations[i].getObjectType()).getClassType().getName().replace( '$',
    //                                                                                                                        '.' );
    //        }
    //
    //        List globals = getUsedGlobals( evalDescr.getText() );
    //        List globalTypes = new ArrayList( globals.size() );
    //        for ( Iterator it = globals.iterator(); it.hasNext(); ) {
    //            globalTypes.add( ((Class) this.pkg.getGlobals().get( it.next() )).getName().replace( '$',
    //                                                                                                 '.' ) );
    //        }
    //
    //        st.setAttribute( "declarations",
    //                         declarations );
    //        st.setAttribute( "declarationTypes",
    //                         declarationTypes );
    //
    //        st.setAttribute( "globals",
    //                         globals );
    //        st.setAttribute( "globalTypes",
    //                         globalTypes );
    //
    //        st.setAttribute( "methodName",
    //                         classMethodName );
    //        st.setAttribute( "text",
    //                         evalDescr.getText() );
    //        System.out.println( "stringtemplate : " + st );
    //
    //        //        Template template = this.cfg.getTemplate( "evalMethod.ftl" );
    //        //        StringWriter string = new StringWriter();
    //        //        template.process( root,
    //        //                          string );
    //        //        string.flush();
    //        //        this.methods.add( string.toString() );
    //        this.methods.add( st.toString() );
    //
    //        //        Template template = this.cfg.getTemplate( "evalInvoker.ftl" );
    //        //        StringWriter string = new StringWriter();
    //        //        template.process( root,
    //        //                          string );
    //        //        string.flush();
    //
    //        group = new StringTemplateGroup( new InputStreamReader( getClass().getResourceAsStream( "javaInvokeables.stg" ) ),
    //                                         AngleBracketTemplateLexer.class );
    //
    //        st = group.getInstanceOf( "evalInvokeable" );
    //
    //        st.setAttribute( "package",
    //                         this.pkg.getName() );
    //        st.setAttribute( "ruleClassName",
    //                         ucFirst( this.ruleDescr.getClassName() ) );
    //        st.setAttribute( "invokeableClassName",
    //                         ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invokeable" );
    //        st.setAttribute( "methodName",
    //                         classMethodName );
    //
    //
    //        st.setAttribute( "declarations",
    //                         declarations );
    //        st.setAttribute( "declarationTypes",
    //                         declarationTypes );
    //
    //        st.setAttribute( "globals",
    //                         globals );
    //        st.setAttribute( "globalTypes",
    //                         globalTypes );
    //
    //        st.setAttribute( "methodName",
    //                         classMethodName );
    //        st.setAttribute( "text",
    //                         evalDescr.getText() );
    //        System.out.println( "stringtemplate : " + st );
    //
    //        String invokerClassName = ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
    //        this.invokeables.put( invokerClassName,
    //                              st.toString() );
    //        this.referenceLookups.put( this.pkg.getName() + "." + invokerClassName,
    //                                   eval );
    //    }

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

        String invokerClassName = ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokeables.put( invokerClassName,
                              string.toString() );
        this.referenceLookups.put( this.pkg.getName() + "." + invokerClassName,
                                   this.rule );

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
