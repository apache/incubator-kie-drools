/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;


import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.drools.core.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.kie.api.definition.process.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPMN2XMLTest extends XMLTestCase {
	
    private static final Logger logger = LoggerFactory.getLogger(BPMN2XMLTest.class);
   
	private static final String[] processes = {
		"BPMN2-SimpleXMLProcess.bpmn2"
	};

	private String errorMessage;

	public void setUp() throws Exception {
		super.setUp();
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreComments(true);
		setErrorMessage(null);
	}

	public void testXML() throws IOException, SAXException {
		SemanticModules modules = new SemanticModules();
		modules.addSemanticModule(new BPMNSemanticModule());
		modules.addSemanticModule(new BPMNDISemanticModule());
        XmlProcessReader processReader = new XmlProcessReader(modules, getClass().getClassLoader());
        for (String processName: processes) {
			String original = slurp(BPMN2XMLTest.class.getResourceAsStream("/" + processName));
			List<Process> processes = processReader.read(BPMN2XMLTest.class.getResourceAsStream("/" + processName));
            assertNotNull(processes);
            assertEquals(1, processes.size());
            RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
			String result = XmlBPMNProcessDumper.INSTANCE.dump(p, XmlBPMNProcessDumper.META_DATA_USING_DI);

			// Compare original with result using XMLUnit
			Diff diff = new Diff(original, result);
			
			// Ignore the sequence of nodes (or children nodes) when looking at these nodes
			final HashSet<String> sequenceDoesNotMatter = new HashSet<String>();			
			sequenceDoesNotMatter.add("startEvent");
			sequenceDoesNotMatter.add("scriptTask");
			sequenceDoesNotMatter.add("endEvent");
			sequenceDoesNotMatter.add("bpmndi:BPMNShape");
			diff.overrideDifferenceListener(new DifferenceListener() {
                
                public int differenceFound(Difference diff) {
                    String nodeName = diff.getTestNodeDetail().getNode().getNodeName();
                    if( sequenceDoesNotMatter.contains(nodeName)
                        && diff.getId() == DifferenceConstants.CHILD_NODELIST_SEQUENCE_ID ) { 
                        return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                    }
                    logger.info( "! {}", diff.getTestNodeDetail().getNode().getNodeName());
                    return RETURN_ACCEPT_DIFFERENCE;
                }
                
                public void skippedComparison(Node one, Node two) { 
                    logger.info("{} : {}", one.getLocalName(), two.getLocalName()) ;
                }
            });

			// nodes should only be compared if their attributes are the same
			diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
            
			assertTrue("Original and generated output is not the same.", diff.identical());
		}
	}

	public void testInvalidXML() throws Exception, SAXException {

		SemanticModules modules = new SemanticModules();
		modules.addSemanticModule(new BPMNSemanticModule());
		modules.addSemanticModule(new BPMNDISemanticModule());
		XmlProcessReader processReader = new XmlProcessReader(modules, getClass().getClassLoader()) {
			@Override
			protected String processParserMessage(LinkedList<Object> parents, org.xml.sax.Attributes attr, String errorMessage) {
				setErrorMessage(super.processParserMessage(parents, attr, errorMessage));
				return errorMessage;
			}
		};

		processReader.read(BPMN2XMLTest.class.getResourceAsStream("/BPMN2-XMLProcessWithError.bpmn2"));

		assertNotNull(getErrorMessage());
		assertThat(getErrorMessage()).contains("Process Info: id:error.process, pkg:org.jbpm, name:errorprocess, version:1.0 \n" +
                              "Node Info: id:_F8A89567-7416-4CCA-9CCD-BC1DDE870F1E name: \n" +
                              "Parser message: (null: 45, 181): cvc-complex-type.2.4.a: Invalid content was found");

	}

	public void testInvalidXMLInCompositeNode() throws Exception, SAXException {
		SemanticModules modules = new SemanticModules();
		modules.addSemanticModule(new BPMNSemanticModule());
		modules.addSemanticModule(new BPMNDISemanticModule());
		XmlProcessReader processReader = new XmlProcessReader(modules, getClass().getClassLoader()) {
			@Override
			protected String processParserMessage(LinkedList<Object> parents, org.xml.sax.Attributes attr, String errorMessage) {
				setErrorMessage(super.processParserMessage(parents, attr, errorMessage));
				return errorMessage;
			}
		};

		processReader.read(BPMN2XMLTest.class.getResourceAsStream("/BPMN2-XMLProcessWithErrorInCompositeNode.bpmn2"));

		assertNotNull(getErrorMessage());
		assertThat(getErrorMessage()).contains("Process Info: id:abc.abc, pkg:org.drools.bpmn2, name:abc, version:1.0 \n" +
                             "Node Info: id:_47489F3D-FEBD-4452-B62E-B04EF191C6C3 name: \n" +
                             "Parser message: (null: 24, 185): cvc-complex-type.2.4.a: Invalid content was found");
	}

	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	private String getErrorMessage() {
		return errorMessage;
	}
	
	public static String slurp(InputStream in) throws IOException {
	    StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    return out.toString();
	}

}
