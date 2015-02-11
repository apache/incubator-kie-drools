package org.drools.core.xml.jaxb.util;

import static junit.framework.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.drools.core.command.runtime.process.StartProcessCommand;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JaxbUnknownAdapterTest {

    private static final String[] vals = { 
        "a long string containing spaces and other characters +ěš@#$%^*()_{}\\/.,", 
        "Ampersand in the string &.",
        "\"quoted string\"" 
    };
  
    @BeforeClass
    public static void before() { 
       System.setProperty("org.kie.xml.encode", "true");
    }
    
    @AfterClass
    public static void after() { 
       System.setProperty("org.kie.xml.encode", "false");
    }
    
    // For https://bugzilla.redhat.com/show_bug.cgi?id=1139667
    // - issues related to weird characters in HTTP and JAXB
    @Test
    public void testFunnyCharactersInStrings() { 
            for( int i = 0; i < vals.length; ++i ) {
                String base64 = JaxbUnknownAdapter.stringToBase64String(vals[i]);
                String copy = JaxbUnknownAdapter.base64StringToString(base64);
                assertEquals(copy, vals[i]);
            } 
    }
    
    @Test
    public void testFunnyCharactersMarshalling() throws Exception {
        Map<String, Object> params = new TreeMap<String, Object>();
        StartProcessCommand cmd = new StartProcessCommand("test", params);
        
        for( int i = 0; i < vals.length; ++i ) { 
            params.put("var-" + i, vals[i]);
        }
       
        Class [] jaxbClasses = { StartProcessCommand.class };
        JAXBContext jaxbCtx = JAXBContext.newInstance(jaxbClasses);
        StartProcessCommand copyCmd = (StartProcessCommand) testRoundTrip(cmd, jaxbCtx);
        
        for( Entry<String, Object> entry : copyCmd.getParameters().entrySet() ) { 
           String iStr = entry.getKey().substring(4);
           Integer i = Integer.parseInt(iStr);
           assertEquals( vals[i], entry.getValue() );
        }
    }
    
    public Object testRoundTrip(Object input, JAXBContext jaxbCtx) throws Exception {
        String xmlStr = convertJaxbObjectToString(input, jaxbCtx);
        return convertStringToJaxbObject(xmlStr, jaxbCtx);
    }


    public String convertJaxbObjectToString(Object object, JAXBContext jaxbCtx) throws JAXBException {
        Marshaller marshaller = jaxbCtx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();

        marshaller.marshal(object, stringWriter);
        String output = stringWriter.toString();

        return output;
    }

    public Object convertStringToJaxbObject(String xmlStr, JAXBContext jaxbCtx) throws JAXBException {
        Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes());

        Object jaxbObj = unmarshaller.unmarshal(xmlStrInputStream);

        return jaxbObj;
    }
}
