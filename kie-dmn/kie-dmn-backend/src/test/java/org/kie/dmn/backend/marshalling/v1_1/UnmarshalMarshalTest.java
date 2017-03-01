/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.backend.marshalling.v1_1;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.dmn.backend.marshalling.v1_1.xstream.XStreamMarshaller;
import org.kie.dmn.model.v1_1.Definitions;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;


public class UnmarshalMarshalTest {
    
    @Test 
    public void test0001() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0001-input-data-string.dmn");
    }

    @Test 
    public void test0002() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0002-input-data-number.dmn");
    }
    
    @Test 
    public void test0003() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0003-input-data-string-allowed-values.dmn");
    }
    
    @Test
    public void testDish() throws Exception {
        testRoundTrip("", "dish-decision.xml");
    }
    
    @Ignore("failing to compare over a xsi:type=\"tImport\" attribute, but why content generated 'control' need to explicit it ?")
    @Test
    public void testDummyDefinitions() throws Exception {
        testRoundTrip("", "dummy-definitions.xml");
    }
    
    @Test
    public void testDummyRelation() throws Exception {
        testRoundTrip("", "dummy-relation.xml");
    }
    
    @Test
    public void testCh11() throws Exception {
        testRoundTrip("", "ch11example.xml");
    }
    
    @Ignore("Need first for XMLUnit to reconciliate the namespaces for comparison. ")
    @Test
    public void testSemanticNamespace() throws Exception {
        testRoundTrip("", "semantic-namespace.xml");
    }

    @Ignore("Need first for XMLUnit to reconciliate the namespaces for comparison. ")
    @Test
    public void test20161014() throws Exception {
        testRoundTrip("", "test20161014.xml");
    }
    
    @Ignore("A problem with the StaxDriver has still to be resolved.")
    @Test
    public void testFAILforMissingNamespaces() {
        fail("PERFORM A MANUAL CHECK: does now the Stax driver do output the namespace for 'feel:' ?? ");
    }

    public void testRoundTrip(String subdir, String xmlfile) throws Exception {
        XStreamMarshaller marshaller = new XStreamMarshaller();
        
        File baseOutputDir = new File("target/test-xmlunit/");
        File testClassesBaseDir = new File("target/test-classes/");
        
    
        File inputXMLFile = new File(testClassesBaseDir, subdir + xmlfile);
        
        FileInputStream fis = new FileInputStream( inputXMLFile );
                
        Definitions unmarshal = marshaller.unmarshal( new InputStreamReader( fis ) );
        
        Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource( this.getClass().getResource("/dmn.xsd").getFile() ));
        ValidationResult validateInputResult = v.validateInstance(new StreamSource( inputXMLFile ));
        if (!validateInputResult.isValid()) {
            for ( ValidationProblem p : validateInputResult.getProblems()) {
                System.err.println(p);
            }
        }
        assertTrue(validateInputResult.isValid());
        
        new File(baseOutputDir, subdir).mkdirs();
        FileOutputStream sourceFos = new FileOutputStream( new File(baseOutputDir, subdir + "a." + xmlfile) );
        Files.copy(
                new File(testClassesBaseDir, subdir + xmlfile).toPath(),
                sourceFos
                );
        sourceFos.flush();
        sourceFos.close();
                
        System.out.println( marshaller.marshal(unmarshal) );
        File outputXMLFile = new File(baseOutputDir, subdir + "b." + xmlfile);
        FileWriter targetFos = new FileWriter( outputXMLFile );
        marshaller.marshal(unmarshal, targetFos);        
        targetFos.flush();
        targetFos.close();
        
        // Should also validate output XML:
        ValidationResult validateOutputResult = v.validateInstance(new StreamSource( outputXMLFile ));
        if (!validateOutputResult.isValid()) {
            for ( ValidationProblem p : validateOutputResult.getProblems()) {
                System.err.println(p);
            }
        }
        assertTrue(validateOutputResult.isValid());
        
        System.out.println("\n---\nDefault XMLUnit comparison:");
        Source control = Input.fromFile( inputXMLFile ).build();
        Source test = Input.fromFile( outputXMLFile ).build();
        Diff allDiffsSimilarAndDifferent = DiffBuilder
                .compare( control )
                .withTest( test )
                .build();
        allDiffsSimilarAndDifferent.getDifferences().forEach(System.out::println);

        System.out.println("XMLUnit comparison with customized similarity for defaults:");
        // in the following a manual DifferenceEvaluator is needed until XMLUnit is configured for properly parsing the XSD linked inside the XML,
        // in order to detect the optional+defaultvalue attributes of xml element which might be implicit in source-test, and explicit in test-serialized.
        /*
         * $ grep -Eo "<xsd:attribute name=\\\"([^\\\"]*)\\\" type=\\\"([^\\\"]*)\\\" use=\\\"optional\\\" default=\\\"([^\\\"])*\\\"" dmn.xsd 
<xsd:attribute name="expressionLanguage" type="xsd:anyURI" use="optional" default="http://www.omg.org/spec/FEEL/20140401"
<xsd:attribute name="typeLanguage" type="xsd:anyURI" use="optional" default="http://www.omg.org/spec/FEEL/20140401"
<xsd:attribute name="isCollection" type="xsd:boolean" use="optional" default="false"
<xsd:attribute name="hitPolicy" type="tHitPolicy" use="optional" default="UNIQUE"
<xsd:attribute name="preferredOrientation" type="tDecisionTableOrientation" use="optional" default="Rule-as-Row"

         */
        Set<QName> attrWhichCanDefault = new HashSet<QName>();
        attrWhichCanDefault.addAll(Arrays.asList(new QName[] {
                new QName("expressionLanguage"), 
                new QName("typeLanguage"), 
                new QName("isCollection"), 
                new QName("hitPolicy"), 
                new QName("preferredOrientation")
                }));
        Set<String> nodeHavingDefaultableAttr = new HashSet<>();
        nodeHavingDefaultableAttr.addAll(Arrays.asList(new String[]{"definitions", "decisionTable", "itemDefinition", "itemComponent"}));
        Diff checkSimilar = DiffBuilder
                .compare( control )
                .withTest( test )
                .withDifferenceEvaluator(
                        DifferenceEvaluators.chain(DifferenceEvaluators.Default,
                        ((comparison, outcome) -> {
                            if (outcome == ComparisonResult.DIFFERENT && comparison.getType() == ComparisonType.ELEMENT_NUM_ATTRIBUTES) {
                                if (comparison.getControlDetails().getTarget().getNodeName().equals( comparison.getTestDetails().getTarget().getNodeName() )
                                        && nodeHavingDefaultableAttr.contains( comparison.getControlDetails().getTarget().getNodeName() )) {
                                    return ComparisonResult.SIMILAR;
                                }
                            }
                            if (outcome == ComparisonResult.DIFFERENT && comparison.getType() == ComparisonType.ATTR_NAME_LOOKUP) {
                                boolean testIsDefaulableAttribute = false;
                                QName whichDefaultableAttr = null;
                                if (comparison.getControlDetails().getValue() == null && attrWhichCanDefault.contains(comparison.getTestDetails().getValue())) {
                                    for (QName a : attrWhichCanDefault) {
                                        boolean check = comparison.getTestDetails().getXPath().endsWith("@"+a);
                                        if (check) {
                                            testIsDefaulableAttribute = true;
                                            whichDefaultableAttr = a;
                                            continue;
                                        }
                                    }
                                }
                                if ( testIsDefaulableAttribute ) {
                                    if (comparison.getTestDetails().getXPath().equals(comparison.getControlDetails().getXPath() + "/@" + whichDefaultableAttr )) {
                                        // TODO missing to check the explicited option attribute has value set to the actual default value.
                                        return ComparisonResult.SIMILAR;
                                    }
                                }
                            }
                        return outcome;
                    })))
                .ignoreWhitespace()
                .checkForSimilar()
                .build();
        checkSimilar.getDifferences().forEach(System.err::println);
        if (!checkSimilar.getDifferences().iterator().hasNext()) {
            System.out.println("[ EMPTY - no diffs using customized similarity ]");
        }
        assertFalse("XML are NOT similar: " + checkSimilar.toString(), checkSimilar.hasDifferences());
    }
}
