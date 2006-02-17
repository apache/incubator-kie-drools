package org.drools.semantics.java;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.CheckedDroolsException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BoundVariableDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElement;
import org.drools.lang.descr.ConsequenceDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.rule.ColumnBinding;
import org.drools.rule.Declaration;
import org.drools.rule.FieldBinding;
import org.drools.rule.Rule;
import org.drools.spi.FieldExtractor;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class JavaRuleCompiler {
    private final RuleSetBundle ruleSetBundle;
    private final Configuration cfg;

    private Rule                rule;

    public Map                 invokerMethods;
    public List                invokerImpls;

    private Map                 globals;
    private Set                 usedGlobals;

    private Map                 bindings;

    // @todo move to an interface so it can work as a decorator
    private JavaExprAnalyzer    analyzer = new JavaExprAnalyzer();

    public JavaRuleCompiler(RuleSetBundle ruleSetBundle) {
        this.ruleSetBundle = ruleSetBundle;
        cfg = new Configuration();
        cfg.setClassForTemplateLoading( getClass(),
                                        "" );

        this.invokerMethods = new HashMap();
        this.invokerImpls = new ArrayList();
        this.bindings = new HashMap();
        this.globals = ruleSetBundle.getRuleSet().getGlobalDeclarations();
        this.usedGlobals = new HashSet();
    }

    public void configure(Rule rule,
                          AndDescr lhs,
                          ConsequenceDescr rhs) throws CheckedDroolsException {
        try {
            this.rule = rule;
            configure( lhs );
        } catch ( Exception e) {
            e.printStackTrace();
            throw new CheckedDroolsException(e);
        }
    }

    private void configure(ConditionalElement descr) throws IOException,
                                                    TemplateException,
                                                    TokenStreamException,
                                                    RecognitionException,
                                                    ClassNotFoundException,
                                                    IntrospectionException {
        System.out.println( "X" );
        for ( Iterator it = descr.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof ConditionalElement ) {
                configure( (ConditionalElement) object );
            } else if ( object instanceof ColumnDescr ) {
                configure( (ColumnDescr) object );
            }
        }
    }

    private void configure(ColumnDescr column) throws IOException,
                                              TemplateException,
                                              TokenStreamException,
                                              RecognitionException,
                                              ClassNotFoundException,
                                              IntrospectionException {
        System.out.println( "Y" );
        Class clazz = Class.forName( column.getObjectType() );

        // We need to keep the type of the binding, so it can be specified in the imports of the generated code
        // We can reuse this as part of the ruel generation
        if ( column.getIdentifier() != null && column.getIdentifier().equals( "" ) ) {
            this.bindings.put( column.getIdentifier(),
                               clazz );
        }

        for ( Iterator it = column.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof FieldBindingDescr ) {
                configure( clazz,
                           (FieldBindingDescr) object );
            } else if ( object instanceof BoundVariableDescr ) {
                configure( column,
                           (BoundVariableDescr) object );
            } else if ( object instanceof ReturnValueDescr ) {
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

    private void configure(Class parentClazz,
                           FieldBindingDescr fieldBinding) throws ClassNotFoundException,
                                                          IntrospectionException {
        // We need to keep the type of the binding, so it can be specified in the imports of the generated code
        // We can reuse this as part of the ruel generation        
        bindings.put( fieldBinding.getIdentifier(),
                      getClassType( parentClazz,
                                    fieldBinding.getFieldName() ) );
    }

    private void configure(ColumnDescr column,
                           BoundVariableDescr boundVariable) {
        // Do nothing no code needs to be generated for this one.
    }

    private void configure(ColumnDescr column,
                           ReturnValueDescr returnValue) throws IOException,
                                                        TemplateException,
                                                        TokenStreamException,
                                                        RecognitionException {
        // generate method
        // generate invoker
        Map root = new HashMap();

        List usedDeclarations = this.analyzer.analyze( returnValue.getText(),
                                                       this.bindings.keySet() );
        returnValue.setDeclarations( (String[]) usedDeclarations.toArray( new String[usedDeclarations.size()] ) );

        root.put( "declarations",
                  usedDeclarations );

        root.put( "globals",
                  getUsedGlobals( returnValue.getText() ) );

        Template template = this.cfg.getTemplate( "returnValueMethod.ftl" );
        StringWriter string = new StringWriter();               
        template.process( root,
                          string );
        string.flush();
        this.invokerMethods.put( returnValue,
                                 string );
        System.out.println( string );
    }

    private void configure(ColumnDescr column,
                           PredicateDescr predicate) throws TemplateException,
                                                    IOException,
                                                    TokenStreamException,
                                                    RecognitionException {
        // generate method
        // generate invoker
        Map root = new HashMap();

        List usedDeclarations = this.analyzer.analyze( predicate.getText(),
                                                       this.bindings.keySet() );
        
        predicate.setDeclarations( (String[]) usedDeclarations.toArray( new String[usedDeclarations.size()] ) );

        root.put( "declaration",
                  predicate.getDeclaration() );
        
        root.put( "declarations",
                  usedDeclarations );

        root.put( "globals",
                  getUsedGlobals( predicate.getText() ) );

        Template template = this.cfg.getTemplate( "predicateMethod" );
        StringWriter string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        this.invokerMethods.put( predicate,
                                 string );
    }

    private void configure(EvalDescr eval) throws TokenStreamException, RecognitionException, IOException, TemplateException {
        // generate method
        // generate invoker
        Map root = new HashMap();

        List usedDeclarations = this.analyzer.analyze( eval.getText(),
                                                       this.bindings.keySet() );
        
        eval.setDeclarations( (String[]) usedDeclarations.toArray( new String[usedDeclarations.size()] ) );
        
        root.put( "declarations",
                  usedDeclarations );

        root.put( "globals",
                  getUsedGlobals( eval.getText() ) );

        Template template = this.cfg.getTemplate( "evalMethod" );
        StringWriter string = new StringWriter();
        template.process( root,
                          string );
        string.flush();
        this.invokerMethods.put( eval,
                                 string );        
    }

    private void configure(ConsequenceDescr and) {
        // generate method
        // generate invoker
    }

    //    private void updateDeclarations(List usedDeclarations) {
    //        for ( Iterator it = usedDeclarations.iterator(); it.hasNext(); ) {
    //            String declaration = (String) it.next();
    //            if ( ! this.declarations.contains( declaration ) ) {
    //                this.declarations.add( declaration );
    //            }
    //        }
    //    }

    private List getUsedGlobals(String text) {
        List list = new ArrayList();

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

            usedGlobals.add( key );

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
    private String generateUniqueLegalName(String packageName,
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
            this.ruleSetBundle.getMemoryResourceReader().isAvailable( fileName );
        }
        // we have duplicate file names so append counter
        if ( counter >= 0 ) {
            newName = newName + "_" + counter;
        }

        return newName;
    }

    private Class getClassType(Class clazz,
                               String name) throws IntrospectionException {
        Class fieldType = null;
        PropertyDescriptor[] descriptors = Introspector.getBeanInfo( clazz ).getPropertyDescriptors();
        for ( int i = 0; i < descriptors.length; i++ ) {
            if ( descriptors[i].getName().equals( name ) ) {
                fieldType = descriptors[i].getPropertyType();
                break;
            }
        }
        return fieldType;

    }
}
