package org.drools.semantics.java;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.Cheese;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.rule.Declaration;
import org.drools.spi.ColumnExtractor;
import org.drools.spi.FieldExtractor;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import junit.framework.TestCase;

public class FtlTest extends TestCase {
    public void test1() throws IOException, IntrospectionException, TemplateException {        
        Configuration cfg = new Configuration();
        
        cfg.setClassForTemplateLoading( getClass(), "" );
        
        Template temp = cfg.getTemplate( "generatePredicateMethod.ftl" );
        Map root = new HashMap();
        
        Declaration declaration = new Declaration( "cheese",
                                                   new ColumnExtractor( new ClassObjectType( Cheese.class ) ),
                                                   5 );
        root.put( "declaration", declaration );
        
        FieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                            "type" );

        Declaration declaration1 = new Declaration( "typeOfCheese",
                                                    extractor,
                                                    5 );
        
        List declarations = new ArrayList();
        declarations.add( declaration1 );
        declarations.add( declaration );
        root.put("declarations", declarations);
        root.put("className", "Test" );
        
        Map applicationData = new HashMap();
        root.put( "applicationData", applicationData );        
        applicationData.put( "xxx",  String.class );
        
        Set usedApplicationData = new HashSet();
        usedApplicationData.add( "xxx" );        
        root.put( "usedApplicationData", usedApplicationData );
        root.put( "methodName", "methodName" );
        root.put( "text", "text" );
                       
        Writer out = new OutputStreamWriter(System.out);
        temp.process( root, out );
        out.flush();
          
    }
    
    public static int getIndex(Class clazz,
                               String name) throws IntrospectionException {
        PropertyDescriptor[] descriptors = Introspector.getBeanInfo( clazz ).getPropertyDescriptors();
        for ( int i = 0; i < descriptors.length; i++ ) {
            if ( descriptors[i].getName().equals( name ) ) {
                return i;
            }
        }
        return -1;
    }    
}
