/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.custommonkey.xmlunit.ElementQualifier;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.kie.definition.process.Process;
import org.drools.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPMN2XMLTest extends XMLTestCase {
	
    private Logger logger = LoggerFactory.getLogger(BPMN2XMLTest.class);
   
	private static final String[] processes = {
		"BPMN2-SimpleXMLProcess.bpmn2",
//		"BPMN2-MinimalProcess.xml",
	};
	
	public void setUp() throws Exception {
		super.setUp();
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreComments(true);
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
                    System.out.println( "! " + diff.getTestNodeDetail().getNode().getNodeName());
                    return RETURN_ACCEPT_DIFFERENCE;
                }
                
                public void skippedComparison(Node one, Node two) { 
                   System.out.println(one.getLocalName() + " : " + two.getLocalName()) ;
                }
            });

			// nodes should only be compared if their attributes are the same
			diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
            
			assertTrue("Original and generated output is not the same.", diff.identical());
		}
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
