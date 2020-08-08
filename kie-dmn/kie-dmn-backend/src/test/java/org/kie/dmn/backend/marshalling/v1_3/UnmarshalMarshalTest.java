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

package org.kie.dmn.backend.marshalling.v1_3;

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

import org.junit.Test;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_3.extensions.TrisoExtensionRegister;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UnmarshalMarshalTest {

    private static final StreamSource DMN13_SCHEMA_SOURCE = new StreamSource(UnmarshalMarshalTest.class.getResource("/DMN13.xsd").getFile());
    private static final DMNMarshaller MARSHALLER = new org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller();
    protected static final Logger LOG = LoggerFactory.getLogger(UnmarshalMarshalTest.class);

    @Test
    public void testV13_simple() throws Exception {
        testRoundTripV13("org/kie/dmn/backend/marshalling/v1_3/", "simple.dmn");
    }

    @Test
    public void testV13_ch11example_asFromOMG() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new TrisoExtensionRegister())); // as the example from OMG contains example of extension element, preserving.
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_3/", "Chapter 11 Example.dmn", marshaller, DMN13_SCHEMA_SOURCE);
    }

    @Test
    public void testV13_financial() throws Exception {
        testRoundTripV13("org/kie/dmn/backend/marshalling/v1_3/", "Financial.dmn");
    }

    @Test
    public void testV13_loan_info() throws Exception {
        testRoundTripV13("org/kie/dmn/backend/marshalling/v1_3/", "Loan info.dmn");
    }

    @Test
    public void testV13_recommended_loan_product() throws Exception {
        testRoundTripV13("org/kie/dmn/backend/marshalling/v1_3/", "Recommended Loan Products.dmn");
    }

    @Test
    public void testV13_group() throws Exception {
        testRoundTripV13("org/kie/dmn/backend/marshalling/v1_3/", "group.dmn");
    }

    @Test
    public void testV13_dmnedge() throws Exception {
        testRoundTripV13("org/kie/dmn/backend/marshalling/v1_3/", "dmnedge.dmn");
    }

    @Test
    public void testV13_functionItem() throws Exception {
        testRoundTripV13("org/kie/dmn/backend/marshalling/v1_3/", "functionItem.dmn");
    }

    @Test
    public void testV13_decision_list() throws Exception {
        testRoundTripV13("org/kie/dmn/backend/marshalling/v1_3/", "decision-list.dmn");
    }

    public void testRoundTripV13(String subdir, String xmlfile) throws Exception {
        testRoundTrip(subdir, xmlfile, MARSHALLER, DMN13_SCHEMA_SOURCE);
    }

    public void testRoundTrip(String subdir, String xmlfile, DMNMarshaller marshaller, Source schemaSource) throws Exception {

        File baseOutputDir = new File("target/test-xmlunit/");
        File testClassesBaseDir = new File("target/test-classes/");

        File inputXMLFile = new File(testClassesBaseDir, subdir + xmlfile);

        FileInputStream fis = new FileInputStream(inputXMLFile);

        Definitions unmarshal = marshaller.unmarshal(new InputStreamReader(fis));

        Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(schemaSource);
        ValidationResult validateInputResult = v.validateInstance(new StreamSource(inputXMLFile));
        if (!validateInputResult.isValid()) {
            for (ValidationProblem p : validateInputResult.getProblems()) {
                LOG.error("{}", p);
            }
        }
        assertTrue(validateInputResult.isValid());

        final File subdirFile = new File(baseOutputDir, subdir);
        if (!subdirFile.mkdirs()) {
            LOG.warn("mkdirs() failed for File: ", subdirFile.getAbsolutePath());
        }
        FileOutputStream sourceFos = new FileOutputStream(new File(baseOutputDir, subdir + "a." + xmlfile));
        Files.copy(
                new File(testClassesBaseDir, subdir + xmlfile).toPath(),
                sourceFos
        );
        sourceFos.flush();
        sourceFos.close();

        LOG.debug("{}", marshaller.marshal(unmarshal));
        File outputXMLFile = new File(baseOutputDir, subdir + "b." + xmlfile);
        try (FileWriter targetFos = new FileWriter(outputXMLFile)) {
            marshaller.marshal(unmarshal, targetFos);
        }

        // Should also validate output XML:
        ValidationResult validateOutputResult = v.validateInstance(new StreamSource(outputXMLFile));
        if (!validateOutputResult.isValid()) {
            for (ValidationProblem p : validateOutputResult.getProblems()) {
                LOG.error("{}", p);
            }
        }
        assertTrue(validateOutputResult.isValid());

        LOG.debug("\n---\nDefault XMLUnit comparison:");
        Source control = Input.fromFile(inputXMLFile).build();
        Source test = Input.fromFile(outputXMLFile).build();
        Diff allDiffsSimilarAndDifferent = DiffBuilder
                .compare(control)
                .withTest(test)
                .build();
        allDiffsSimilarAndDifferent.getDifferences().forEach(m -> LOG.debug("{}", m));

        LOG.info("XMLUnit comparison with customized similarity for defaults:");
        // in the following a manual DifferenceEvaluator is needed until XMLUnit is configured for properly parsing the XSD linked inside the XML,
        // in order to detect the optional+defaultvalue attributes of xml element which might be implicit in source-test, and explicit in test-serialized.
        /*
         * $ grep -Eo "<xsd:attribute name=\\\"([^\\\"]*)\\\" type=\\\"([^\\\"]*)\\\" use=\\\"optional\\\" default=\\\"([^\\\"])*\\\"" dmn.xsd 
<xsd:attribute name="expressionLanguage" type="xsd:anyURI" use="optional" default="http://www.omg.org/spec/FEEL/20140401"
<xsd:attribute name="typeLanguage" type="xsd:anyURI" use="optional" default="http://www.omg.org/spec/FEEL/20140401"
<xsd:attribute name="isCollection" type="xsd:boolean" use="optional" default="false"
<xsd:attribute name="hitPolicy" type="tHitPolicy" use="optional" default="UNIQUE"
<xsd:attribute name="preferredOrientation" type="tDecisionTableOrientation" use="optional" default="Rule-as-Row"
DMNv1.2:
<xsd:attribute name="kind" type="tFunctionKind" default="FEEL"/>
<xsd:attribute name="textFormat" type="xsd:string" default="text/plain"/>
<xsd:attribute name="associationDirection" type="tAssociationDirection" default="None"/>
DMNDIv1.2:
<xsd:attribute name="isCollapsed" type="xsd:boolean" use="optional" default="false"/>
         */
        Set<QName> attrWhichCanDefault = new HashSet<QName>();
        attrWhichCanDefault.addAll(Arrays.asList(new QName[]{
                new QName("expressionLanguage"),
                new QName("typeLanguage"),
                new QName("isCollection"),
                new QName("hitPolicy"),
                new QName("preferredOrientation"),
                new QName("kind"),
                new QName("textFormat"),
                new QName("associationDirection"),
                new QName("isCollapsed")
        }));
        Set<String> nodeHavingDefaultableAttr = new HashSet<>();
        nodeHavingDefaultableAttr.addAll(Arrays.asList(new String[]{"definitions", "decisionTable", "itemDefinition", "itemComponent", "encapsulatedLogic", "textAnnotation", "association", "DMNShape"}));
        Diff checkSimilar = DiffBuilder
                .compare(control)
                .withTest(test)
                .withDifferenceEvaluator(
                        DifferenceEvaluators.chain(DifferenceEvaluators.Default,
                                                   ((comparison, outcome) -> {
                                                       if (outcome == ComparisonResult.DIFFERENT && comparison.getType() == ComparisonType.ELEMENT_NUM_ATTRIBUTES) {
                                                           if (comparison.getControlDetails().getTarget().getNodeName().equals(comparison.getTestDetails().getTarget().getNodeName())
                                                                   && nodeHavingDefaultableAttr.contains(safeStripDMNPRefix(comparison.getControlDetails().getTarget()))) {
                                                               return ComparisonResult.SIMILAR;
                                                           }
                                                       }
                                                       // DMNDI/DMNDiagram#documentation is actually deserialized escaped with newlines as &#10; by the XML JDK infra.
                                                       if (outcome == ComparisonResult.DIFFERENT && comparison.getType() == ComparisonType.ATTR_VALUE) {
                                                           if (comparison.getControlDetails().getTarget().getNodeName().equals(comparison.getTestDetails().getTarget().getNodeName())
                                                                   && comparison.getControlDetails().getTarget().getNodeType() == Node.ATTRIBUTE_NODE
                                                                   && comparison.getControlDetails().getTarget().getLocalName().equals("documentation")) {
                                                               return ComparisonResult.SIMILAR;
                                                           }
                                                       }
                                                       if (outcome == ComparisonResult.DIFFERENT && comparison.getType() == ComparisonType.ATTR_NAME_LOOKUP) {
                                                           boolean testIsDefaulableAttribute = false;
                                                           QName whichDefaultableAttr = null;
                                                           if (comparison.getControlDetails().getValue() == null && attrWhichCanDefault.contains(comparison.getTestDetails().getValue())) {
                                                               for (QName a : attrWhichCanDefault) {
                                                                   boolean check = comparison.getTestDetails().getXPath().endsWith("@" + a);
                                                                   if (check) {
                                                                       testIsDefaulableAttribute = true;
                                                                       whichDefaultableAttr = a;
                                                                       continue;
                                                                   }
                                                               }
                                                           }
                                                           if (testIsDefaulableAttribute) {
                                                               if (comparison.getTestDetails().getXPath().equals(comparison.getControlDetails().getXPath() + "/@" + whichDefaultableAttr)) {
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
        checkSimilar.getDifferences().forEach(m -> LOG.error("{}", m));
        if (!checkSimilar.getDifferences().iterator().hasNext()) {
            LOG.info("[ EMPTY - no diffs using customized similarity ]");
        }
        assertFalse("XML are NOT similar: " + checkSimilar.toString(), checkSimilar.hasDifferences());
    }

    private String safeStripDMNPRefix(Node target) {
        if (KieDMNModelInstrumentedBase.URI_DMN.equals(target.getNamespaceURI()) ||
                KieDMNModelInstrumentedBase.URI_DMNDI.equals(target.getNamespaceURI())) {
            return target.getLocalName();
        }
        return null;
    }
}
