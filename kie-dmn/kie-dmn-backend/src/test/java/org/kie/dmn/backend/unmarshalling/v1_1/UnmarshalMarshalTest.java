package org.kie.dmn.backend.unmarshalling.v1_1;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import javax.xml.transform.Source;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.dmn.backend.unmarshalling.v1_1.xstream.XStreamUnmarshaller;
import org.kie.dmn.feel.model.v1_1.Definitions;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DOMDifferenceEngine;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEngine;


public class UnmarshalMarshalTest {
    
    @Ignore("vaguely working, missing xmlnamespaces....")
    @Test 
    public void test0001() throws Exception {
        testRoundTrip("org/kie/dmn/backend/unmarshalling/v1_1/", "0001-input-data-string.dmn");
    }
    
    @Ignore("vaguely working, missing xmlnamespaces....")
    @Test 
    public void test0002() throws Exception {
        testRoundTrip("org/kie/dmn/backend/unmarshalling/v1_1/", "0002-input-data-number.dmn");
    }
    
    @Ignore("vaguely working, missing xmlnamespaces....")
    @Test 
    public void test0003() throws Exception {
        testRoundTrip("org/kie/dmn/backend/unmarshalling/v1_1/", "0003-input-data-string-allowed-values.dmn");
    }
    
    @Ignore("vaguely working, missing xmlnamespaces...., and CDATA is not marshalled - is it really required?")
    @Test
    public void testDish() throws Exception {
        testRoundTrip("", "dish-decision.xml");
    }
    
    @Ignore("still converter issues.")
    @Test
    public void testCh11() throws Exception {
        testRoundTrip("", "ch11example.xml");
    }

    public void testRoundTrip(String subdir, String xmlfile) throws Exception {
        XStreamUnmarshaller marshaller = new XStreamUnmarshaller();
        
        File baseOutputDir = new File("target/test-xmlunit/");
        File testClassesBaseDir = new File("target/test-classes/");
        
    
        File inputXMLFile = new File(testClassesBaseDir, subdir + xmlfile);
        
        FileInputStream fis = new FileInputStream( inputXMLFile );
                
        Definitions unmarshal = marshaller.unmarshal( new InputStreamReader( fis ) );
        
        new File(baseOutputDir, subdir).mkdirs();
        FileOutputStream sourceFos = new FileOutputStream( new File(baseOutputDir, subdir + "a." + xmlfile) );
        Files.copy(
                new File(testClassesBaseDir, subdir + xmlfile).toPath(),
                sourceFos
                );
        sourceFos.flush();
        sourceFos.close();
                
        marshaller.marshal(unmarshal);
        FileOutputStream targetFos = new FileOutputStream( new File(baseOutputDir, subdir + "b." + xmlfile) );
        marshaller.marshal(unmarshal, targetFos);        
        targetFos.flush();
        targetFos.close();
        
        
        Source control = Input.fromFile( inputXMLFile ).build();
        Source test = Input.fromFile( new File(baseOutputDir, subdir + "b." + xmlfile) ).build();
        Diff myDiff = DiffBuilder
                .compare( control )
                .withTest( test )
                .withDifferenceListeners(
                        new ComparisonListener() {
                            public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
                                System.out.println("found a difference: " + comparison);
                            }
                        }    
                        )
                .ignoreWhitespace()
                .build();
        assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());
    }
}
