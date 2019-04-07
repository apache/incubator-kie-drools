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

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.junit.Test;
import org.kie.api.KieBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import static org.junit.Assert.*;

public class XMLBPMNProcessDumperTest extends JbpmBpmn2TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(XMLBPMNProcessDumperTest.class);
    
    public XMLBPMNProcessDumperTest() {
        super(false);
    }

    /**
     * TESTS
     */

    @Test
    public void testConditionExpression() throws Exception {
        // JBPM-4069 : XmlBPMNProcessDumper.dump() misses conditionExpression in sequenceFlow
        String filename = "BPMN2-GatewaySplit-SequenceConditions.bpmn2";
        String original = BPMN2XMLTest.slurp(XMLBPMNProcessDumperTest.class.getResourceAsStream("/" + filename));
        
        KieBase kbase = createKnowledgeBase(filename);
        RuleFlowProcess process = (RuleFlowProcess) kbase.getProcess("GatewayTest");
        String result = XmlBPMNProcessDumper.INSTANCE.dump(process, XmlBPMNProcessDumper.META_DATA_USING_DI);
        
        // Compare original with result using XMLUnit
        Diff diff = new Diff(original, result);
        
        diff.overrideDifferenceListener(new DifferenceListener() {
            
            public int differenceFound(Difference diff) {
                String nodeName = diff.getTestNodeDetail().getNode().getNodeName();
                
                if (nodeName.equals("conditionExpression") || nodeName.equals("language")) {
                    logger.info(diff.toString());
                    return RETURN_ACCEPT_DIFFERENCE;
                }
                
                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }

            public void skippedComparison(Node one, Node two) { 
                logger.info("{} : {}", one.getLocalName(), two.getLocalName()) ;
            }
            
        });
        
        assertTrue("Original and generated output is not the same.", diff.identical());
   }

}
