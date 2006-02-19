package org.drools.semantics.java;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.CheckedDroolsException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.EvaluatorFactory;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BoundVariableDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElement;
import org.drools.lang.descr.ConsequenceDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.BoundVariableConstraint;
import org.drools.rule.Declaration;
import org.drools.rule.Column;
import org.drools.rule.EvalCondition;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.Rule;
import org.drools.spi.ColumnExtractor;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.rule.Package;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class RuleCompiler {
    private final DdjCompiler   compiler;

    private final Configuration cfg;

    private Package             pkg;
    private Rule                rule;
    private RuleDescr           ruleDescr;

    public Map                  invokerMethods;
    public List                 invokerClasses;

    private Map                 declarations;
    
    private int                 counter;
    
    private int                 columnCounter; 

    // @todo move to an interface so it can work as a decorator
    private JavaExprAnalyzer    analyzer = new JavaExprAnalyzer();

    public RuleCompiler(DdjCompiler packageCompiler) {
        this.compiler = packageCompiler;
        cfg = new Configuration();
        cfg.setClassForTemplateLoading( getClass(),
                                        "" );
    }

    public synchronized Rule compile(Package pkg,
                                     RuleDescr ruleDescr) throws CheckedDroolsException {
        this.pkg = pkg;
        this.invokerMethods = new HashMap();
        this.invokerClasses = new ArrayList();
        this.declarations = ruleDescr.getDeclarations();
               
        String ruleClassName = getUniqueLegalName(pkg.getName(), ruleDescr.getName(), "java" );
        ruleDescr.SetClassName( ruleClassName );        

        this.rule = new Rule( ruleDescr.getName() );
        this.ruleDescr = ruleDescr;
        
        try {
            configure( rule, ruleDescr.getLhs() );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new CheckedDroolsException( e );
        }
        
        return rule;
    }

    private void configure(Rule rule, ConditionalElement descr) throws Exception {
        for ( Iterator it = descr.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof ConditionalElement ) {
                configure(rule,  (ConditionalElement) object );
            } else if ( object instanceof ColumnDescr ) {
                configure(rule, (ColumnDescr) object );
            }
        }
    }

    private void configure(Rule rule, ColumnDescr columnDescr) throws IOException,
                                              TemplateException,
                                              TokenStreamException,
                                              RecognitionException,
                                              ClassNotFoundException,
                                              IntrospectionException, InvalidRuleException {
        Class clazz = Class.forName( columnDescr.getObjectType() );
        Column column;
        if ( columnDescr.getIdentifier() != null && columnDescr.getIdentifier().equals( "" ) ) {
            column = new Column( columnCounter++, 
                                 new ClassObjectType(clazz) );
            this.declarations.put( column.getDeclaration().getIdentifier(),
                                   column.getDeclaration() );            
        } else {
            column = new Column( columnCounter++, 
                                 new ClassObjectType(clazz),
                                 columnDescr.getIdentifier() );
        }

        for ( Iterator it = columnDescr.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof FieldBindingDescr ) {
                configure( column,
                           (FieldBindingDescr) object );
            } else if ( object instanceof BoundVariableDescr ) {
                configure( column,
                           (BoundVariableDescr) object );
            }  else if ( object instanceof ReturnValueDescr ) {
                configure( column,
                           (ReturnValueDescr) object );
            } else if ( object instanceof PredicateDescr ) {
                configure( column,
                           (PredicateDescr) object );
            } else if ( object instanceof EvalDescr ) {
                configure( (EvalDescr) object );
            } else if ( object instanceof ConsequenceDescr ) {
                configure( (ConsequenceDescr) object );
            }
        }
        // generate method
        // generate invoker
    }
    
    private void configure(Column column,
                           BoundVariableDescr boundVariable) {
        
        boundVariable.getDeclarationIdentifier();
        
        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            boundVariable.getFieldName() );        
        
        Declaration declaration = (Declaration) this.declarations.get( boundVariable.getDeclarationIdentifier() );
        
        Evaluator evaluator = EvaluatorFactory.getEvaluator( declaration.getObjectType().getValueType(), boundVariable.getEvaluator() );
        
        column.addConstraint( new BoundVariableConstraint( extractor, 
                                                           declaration,
                                                           evaluator ) );
    }    

    private void configure(Column column,
                           FieldBindingDescr fieldBinding) throws ClassNotFoundException,
                                                          IntrospectionException {
        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            fieldBinding.getFieldName() );

        Declaration declaration = column.addDeclaration( fieldBinding.getIdentifier(),
                                                         extractor );
        
        this.declarations.put( declaration.getIdentifier(), declaration );
    }

    private void configure(Column column,
                           ReturnValueDescr returnValueDescr) throws IOException,
                                                        TemplateException,
                                                        TokenStreamException,
                                                        RecognitionException {
        // generate method
        // generate invoker
        Map root = new HashMap();
        
        String classMethodName = "returnValue"  + counter++;
        returnValueDescr.setClassMethodName( classMethodName );
        
        root.put( "package",  this.pkg.getName() );
        root.put( "ruleClassName",  ucFirst( this.ruleDescr.getClassName() ));
        root.put( "invokerClassName",  ucFirst( classMethodName )  + "Invoker"  );
        root.put( "methodName",  classMethodName );
        
        List usedDeclarations = this.analyzer.analyze( returnValueDescr.getText(),
                                                       this.declarations.keySet() );       
        
        Declaration[] declarations = new Declaration[usedDeclarations.size()];
        for ( int i = 0, size = usedDeclarations.size(); i < size; i++) { 
            declarations[i] = (Declaration) this.declarations.get( (String) usedDeclarations.get(i) );            
        }

        root.put( "declarations",
                  declarations );

        root.put( "globals",
                  getUsedGlobals( returnValueDescr.getText() ) );

        root.put( "globalTypes", this.pkg.getGlobals() );
        
        root.put( "text", returnValueDescr.getText() );
        
        
        
        ClassFieldExtractor extractor = new ClassFieldExtractor(((ClassObjectType) column.getObjectType()).getClassType(),
                                                                returnValueDescr.getFieldName() );


        Evaluator evaluator = EvaluatorFactory.getEvaluator( extractor.getObjectType().getValueType(), returnValueDescr.getEvaluator() );
        
        ReturnValueConstraint returnValueConstraint = new ReturnValueConstraint(extractor, declarations,  evaluator);
        column.addConstraint( returnValueConstraint );
        
        Template template = this.cfg.getTemplate( "returnValueMethod.ftl" );
        StringWriter string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        System.out.println(string);
        this.invokerMethods.put( returnValueDescr,
                                 string );
        
        template = this.cfg.getTemplate( "returnValueInvoker.ftl" );
        string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        System.out.println(string);                        
    }

    private void configure(Column column,
                           PredicateDescr predicateDescr) throws TemplateException,
                                                    IOException,
                                                    TokenStreamException,
                                                    RecognitionException {
        // generate method
        // generate invoker
        Map root = new HashMap();
        
        String classMethodName = "predicate"  + counter++;
        predicateDescr.setClassMethodName( classMethodName );
        
        root.put( "package",  this.pkg.getName() );
        root.put( "ruleClassName",  ucFirst( this.ruleDescr.getClassName() ));
        root.put( "invokerClassName",  ucFirst( classMethodName )  + "Invoker"  );
        root.put( "methodName",  classMethodName );
        
        List usedDeclarations = this.analyzer.analyze( predicateDescr.getText(),
                                                       this.declarations.keySet() );
        
        // Don't include the focus declaration, that hasn't been merged into the tuple yet.
        usedDeclarations.remove( predicateDescr.getDeclaration() );
        
        Declaration[] declarations = new Declaration[usedDeclarations.size()];
        for ( int i = 0, size = usedDeclarations.size(); i < size; i++) { 
            declarations[i] = (Declaration) this.declarations.get( (String) usedDeclarations.get(i) );            
        }

        root.put( "declarations",
                  declarations );
        
        Declaration declaration = (Declaration) this.declarations.get( predicateDescr.getDeclaration() );
        root.put( "declaration",  declaration );

        root.put( "globals",
                  getUsedGlobals( predicateDescr.getText() ) );

        root.put( "globalTypes", this.pkg.getGlobals() );
        
        root.put( "text", predicateDescr.getText() );
        
        PredicateConstraint predicateConstraint = new PredicateConstraint(declaration, declarations);
        column.addConstraint( predicateConstraint );
        
        Template template = this.cfg.getTemplate( "predicateMethod.ftl" );
        StringWriter string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        System.out.println(string);
        this.invokerMethods.put( predicateDescr,
                                 string );
        
        template = this.cfg.getTemplate( "predicateInvoker.ftl" );
        string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        System.out.println(string);  
    }

    private void configure(EvalDescr evalDescr) throws TokenStreamException,
                                          RecognitionException,
                                          IOException,
                                          TemplateException, InvalidRuleException {
        // generate method
        // generate invoker
        Map root = new HashMap();
        
        String classMethodName = "returnValue"  + counter++;
        evalDescr.setClassMethodName( classMethodName );
        
        root.put( "package",  this.pkg.getName() );
        root.put( "ruleClassName",  ucFirst( this.ruleDescr.getClassName() ));
        root.put( "invokerClassName",  ucFirst( classMethodName )  + "Invoker"  );
        root.put( "methodName",  classMethodName );
        
        List usedDeclarations = this.analyzer.analyze( evalDescr.getText(),
                                                       this.declarations.keySet() );       
        
        Declaration[] declarations = new Declaration[usedDeclarations.size()];
        for ( int i = 0, size = usedDeclarations.size(); i < size; i++) { 
            declarations[i] = (Declaration) this.declarations.get( (String) usedDeclarations.get(i) );            
        }

        root.put( "declarations",
                  declarations );

        root.put( "globals",
                  getUsedGlobals( evalDescr.getText() ) );

        root.put( "globalTypes", this.pkg.getGlobals() );
        
        root.put( "text", evalDescr.getText() );


        
        EvalCondition eval = new EvalCondition(declarations);
        rule.addPattern( eval );
        
        Template template = this.cfg.getTemplate( "returnValueMethod.ftl" );
        StringWriter string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        System.out.println(string);
        this.invokerMethods.put( evalDescr,
                                 string );
        
        template = this.cfg.getTemplate( "returnValueInvoker.ftl" );
        string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        System.out.println(string);                        
    }

    private void configure(ConsequenceDescr and) {
        // generate method
        // generate invoker
    }

//    private void addDeclarations(List usedDeclarations) {
//        for ( Iterator it = usedDeclarations.iterator(); it.hasNext(); ) {
//            String declaration = (String) it.next();
//            if ( !this.declarations.contains( declaration ) ) {
//                this.declarations.add( declaration );
//            }
//        }
//    }

    private List  getUsedGlobals(String text) {
        List list = new ArrayList(1);
        Map globals  =  this.pkg.getGlobals();
        for ( Iterator it = globals.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();

            /*
             * poor mans check. Only add the application variable if it appears as text in the script
             */
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

    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     * 
     * @param packageName
     * @param name
     * @param ext
     * @return
     */
    private String getUniqueLegalName(String packageName,
                                      String name,
                                      String ext) {
        // replaces the first char if its a number and after that all non
        // alphanumeric or $ chars with _
        String newName = name.replaceAll( "(^[0-9]|[^\\w$])",
                                          "_" );
        
        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        boolean exists = true;
        while ( exists ) {
            counter++;
            String fileName = packageName.replaceAll( "\\.",
                                                      "/" ) + newName + "_" + counter + ext;
            exists = this.compiler.getMemoryResourceReader().isAvailable( fileName );
        }
        // we have duplicate file names so append counter
        if ( counter >= 0 ) {
            newName = newName + "_" + counter;
        }

        return newName;
    }
    
    private String ucFirst(String name) {
        return  name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }
}
