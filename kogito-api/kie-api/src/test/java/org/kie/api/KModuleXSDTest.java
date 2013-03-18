package org.kie.api;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class KModuleXSDTest {

    @Test
    public void test1() throws Exception {
        SchemaFactory factory = 
                SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
       
        URL url = getClass().getClassLoader().getResource("org/kie/api/kmodule.xsd");
        assertNotNull( url );
        Schema schema = null;
        try {
            schema = factory.newSchema(url );
        } catch (SAXParseException ex ) {
            fail( "Unable to load XSD: " + ex.getMessage() + ":" + ex.getLineNumber() + ":" + ex.getColumnNumber()  );
        }
        assertNotNull( schema );

        Validator validator = schema.newValidator();
        
        Source source = new StreamSource(KModuleXSDTest.class.getResource( "kmod1.xml" ).openStream());
        assertNotNull( source );
        
        try {
            validator.validate(source);
        } catch (SAXException ex) {
            fail( "XML should be valid: " + ex.getMessage() );
        }  
    }
}
