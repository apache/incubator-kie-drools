package org.drools.semantics.java;

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
    
    private Rule rule;
    
    private Map invokerMethods;
    private List invokerImpls;
    
    private Map bindings;    
    
    // @todo move to an interface so it can work as a decorator
    private JavaExprAnalyzer analyzer = new JavaExprAnalyzer(); 
    
    public JavaRuleCompiler(RuleSetBundle ruleSetBundle) {
        this.ruleSetBundle = ruleSetBundle;
        cfg = new Configuration();        
        cfg.setClassForTemplateLoading( getClass(), "" );    
        
        this.invokerMethods = new HashMap();
        this.invokerImpls = new ArrayList();
        this.bindings = new HashMap();
    }
    
    public void configure(Rule rule, AndDescr lhs, ConsequenceDescr rhs) throws IOException, TemplateException, TokenStreamException, RecognitionException {
        this.rule = rule;
        configure( lhs );
    }
    
    private void configure(ConditionalElement ce) throws IOException, TemplateException, TokenStreamException, RecognitionException {
        for (Iterator it = ce.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof ConditionalElement ) {
                configure( (ConditionalElement) object );
            } else if ( object instanceof ColumnDescr ) {
                configure( (ColumnDescr) object );
            } 
        }
    }
    
    private void configure(ColumnDescr column) throws IOException, TemplateException, TokenStreamException, RecognitionException {
        if ( column.getBinding() != null || column.getBinding().equals( "" ) ) {
            
        }
        
        for (Iterator it = column.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof FieldBindingDescr ) {
                configure( column, (FieldBindingDescr) object );
            } else if ( object instanceof BoundVariableDescr ) {
                configure( column, (BoundVariableDescr) object );
            } else if ( object instanceof ReturnValueDescr ) {
                configure( column, (ReturnValueDescr) object );
            } else if ( object instanceof PredicateDescr ) {
                configure( column, (PredicateDescr) object );
            } else if ( object instanceof EvalDescr ) {
                configure( (EvalDescr) object );
            } else if ( object instanceof ConsequenceDescr ) {
                configure( (ConsequenceDescr) object );
            }            
        }        
        // generate method
        // generate invoker
    }    

    private void configure(ColumnDescr column, FieldBindingDescr fieldBinding) throws ClassNotFoundException {
        Class clazz = Class.forName( fieldBinding.getFieldName() );        
        FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                            fieldBinding.getFieldName() );

        FieldBinding binding = new FieldBinding( fieldBinding.getIdentifier(),
                                 null,
                                 extractor,
                                 column.getIndex() );
        
        bindings.put( fieldBinding.getIdentifier(), binding );
    }
    
    private void configure(ColumnDescr column, BoundVariableDescr boundVariable) {
        // generate method
        // generate invoker
    }    
    
    private void configure(ColumnDescr column, ReturnValueDescr returnValue) throws IOException, TemplateException, TokenStreamException, RecognitionException {
        // generate method
        // generate invoker
        StringWriter string = new StringWriter();
        Map root = new HashMap();

        
        List usedDeclarations = this.analyzer.analyze( returnValue.getText(), this.bindings.keySet() );
        //updateDeclarations(usedDeclarations);
        root.put(  "declarations", 
                    usedDeclarations );
        
        
        
        Template template = this.cfg.getTemplate( "returnValueMethod" );
        template.process( root, string );        
        string.flush();
        this.invokerMethods.put( returnValue, string );
    }    
    
    private void configure(ColumnDescr column, PredicateDescr predicate) {
        // generate method
        // generate invoker
    }
    
    private void configure(EvalDescr and) {
        // generate method
        // generate invoker
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
    
    private Parameter[] getParameters(Map globals,
                                      Set usedGlobals,
                                      String text)
    {
        List list = new ArrayList();

        Set keys = globals.keySet();
        String key;
        Class clazz;
        String type;
        int nestedClassPosition;

        for ( Iterator it = keys.iterator(); it.hasNext(); )
        {
            key = (String) it.next();

            /*
             * poor mans check. Only add the application variable if it appears as text in the script
             */
            if ( text.indexOf( key ) == -1 )
            {
                continue;
            }
            clazz = (Class) globals.get( key );

            type = clazz.getName();
            nestedClassPosition = type.indexOf( '$' );

            if ( nestedClassPosition != -1 )
            {
                type = type.substring( 0,
                                       nestedClassPosition );
            }

            usedGlobals.add( key );

            imports.add( type );

            list.add( new Parameter( type,
                                     key ) );
        }

        return (Parameter[]) list.toArray( new Parameter[list.size()] );
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
                                           String ext)
    {
        // replaces the first char if its a number and after that all non
        // alphanumeric or $ chars with _
        String newName = name.replaceAll( "(^[0-9]|[^\\w$])",
                                          "_" );

        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        boolean exists = true;
        while ( exists )
        {
            counter++;
            String fileName = packageName.replaceAll( "\\.",
                                                      "/" ) + newName + "_" + counter + ext;
            this.ruleSetBundle.getMemoryResourceReader().isAvailable(  fileName );
        }
        // we have duplicate file names so append counter
        if ( counter >= 0 )
        {
            newName = newName + "_" + counter;
        }

        return newName;
    }    
}
