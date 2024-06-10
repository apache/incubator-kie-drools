/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.backend.marshalling.v1_1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.extensions.MyTestRegister;
import org.kie.dmn.backend.marshalling.v1_1.xstream.extensions.DecisionServicesExtensionRegister;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class UnmarshalMarshalTest {

    protected static final Logger LOG = LoggerFactory.getLogger(UnmarshalMarshalTest.class);

    @Test
    void test0001() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0001-input-data-string.dmn");
    }

    @Test
    void test0002() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0002-input-data-number.dmn");
    }

    @Test
    void test0003() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0003-input-data-string-allowed-values.dmn");
    }

    @Test
    void test0004() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(List.of(new DecisionServicesExtensionRegister()));
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0004-decision-services.dmn", marshaller);
    }

    @Test
    void test0004_ns_other_location() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(List.of(new DecisionServicesExtensionRegister()));
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0004-decision-services_ns_other_location.dmn", marshaller);
    }

    @Test
    void test0005_decision_list() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0005-decision-list.dmn");
    }

    @Test
    void hardcoded_java_max_call() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "hardcoded-java-max-call.dmn");
    }

    @Test
    void dish() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "dish-decision.xml");
    }

    @Disabled("failing to compare over a xsi:type=\"tImport\" attribute, but why content generated 'control' need to explicit it ?")
    @Test
    void dummyDefinitions() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "dummy-definitions.xml");
    }

    @Test
    void dummyRelation() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "dummy-relation.xml");
    }

    @Test
    void ch11() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "ch11example.xml");
    }

    @Test
    void helloWorldSemanticNamespace() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "Hello_World_semantic_namespace.dmn");
    }

    @Test
    void helloWorldSemanticNamespaceWithExtensions() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(List.of(new MyTestRegister()));
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "Hello_World_semantic_namespace_with_extensions.dmn", marshaller);
    }

    @Test
    void helloWorldSemanticNamespaceWithExtensionsOtherNsLocation() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(List.of(new MyTestRegister()));
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "Hello_World_semantic_namespace_with_extensions_other_ns_location.dmn", marshaller);
    }

    @Test
    void semanticNamespace() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "semantic-namespace.xml");
    }

    @Disabled("The current file cannot marshal back extension elements because they don't provide converters.")
    @Test
    void test20161014() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "test20161014.xml");
    }

    @Test
    void qNameSerialization() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "hardcoded_function_definition.dmn");
    }

    @Disabled("A problem with the StaxDriver has still to be resolved.")
    @Test
    void faiLforMissingNamespaces() {
        fail("PERFORM A MANUAL CHECK: does now the Stax driver do output the namespace for 'feel:' ?? ");
    }

    public void testRoundTrip(String subdir, String xmlfile) throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();
        testRoundTrip(subdir, xmlfile, marshaller);
    }

    public void testRoundTrip(String subdir, String xmlfile, DMNMarshaller marshaller) throws Exception {

        File baseOutputDir = new File("target/test-xmlunit/");
        File testClassesBaseDir = new File("target/test-classes/");

        File inputXMLFile = new File(testClassesBaseDir, subdir + xmlfile);

        FileInputStream fis = new FileInputStream(inputXMLFile);

        Definitions unmarshal = marshaller.unmarshal(new InputStreamReader(fis));

        Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(this.getClass().getResource("/DMN11.xsd").getFile()));
        ValidationResult validateInputResult = v.validateInstance(new StreamSource(inputXMLFile));
        if (!validateInputResult.isValid()) {
            for (ValidationProblem p : validateInputResult.getProblems()) {
                LOG.error("{}", p);
            }
        }
        assertThat(validateInputResult.isValid()).isTrue();

        final File subdirFile = new File(baseOutputDir, subdir);
        if (!subdirFile.mkdirs()) {
            LOG.warn("mkdirs() failed for File: {}", subdirFile.getAbsolutePath());
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
        assertThat(validateOutputResult.isValid()).isTrue();

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

         */
        Set<QName> attrWhichCanDefault = new HashSet<QName>();
        attrWhichCanDefault.addAll(Arrays.asList(new QName("expressionLanguage"),
                                                 new QName("typeLanguage"),
                                                 new QName("isCollection"),
                                                 new QName("hitPolicy"),
                                                 new QName("preferredOrientation")));
        Set<String> nodeHavingDefaultableAttr = new HashSet<>();
        nodeHavingDefaultableAttr.addAll(Arrays.asList("definitions", "decisionTable", "itemDefinition", "itemComponent"));
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
                                                       if (outcome == ComparisonResult.DIFFERENT && comparison.getType() == ComparisonType.ATTR_NAME_LOOKUP) {
                                                           boolean testIsDefaulableAttribute = false;
                                                           QName whichDefaultableAttr = null;
                                                           if (comparison.getControlDetails().getValue() == null && attrWhichCanDefault.contains(comparison.getTestDetails().getValue())) {
                                                               for (QName a : attrWhichCanDefault) {
                                                                   boolean check = comparison.getTestDetails().getXPath().endsWith("@" + a);
                                                                   if (check) {
                                                                       testIsDefaulableAttribute = true;
                                                                       whichDefaultableAttr = a;
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
                .ignoreComments()
                .checkForSimilar()
                .build();
        checkSimilar.getDifferences().forEach(m -> LOG.error("{}", m));
        if (!checkSimilar.getDifferences().iterator().hasNext()) {
            LOG.info("[ EMPTY - no diffs using customized similarity ]");
        }
        assertThat(checkSimilar.hasDifferences()).as("XML are NOT similar: " + checkSimilar.toString()).isFalse();
    }

    private String safeStripDMNPRefix(Node target) {
        if (KieDMNModelInstrumentedBase.URI_DMN.equals(target.getNamespaceURI())) {
            return target.getLocalName();
        }
        return null;
    }
}
