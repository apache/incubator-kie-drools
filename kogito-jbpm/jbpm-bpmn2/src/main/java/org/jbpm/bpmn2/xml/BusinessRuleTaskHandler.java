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

package org.jbpm.bpmn2.xml;

import org.drools.xml.ExtensibleXmlParser;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BusinessRuleTaskHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new RuleSetNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return RuleSetNode.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
        RuleSetNode ruleSetNode = (RuleSetNode) node;
		String ruleFlowGroup = element.getAttribute("ruleFlowGroup");
		if (ruleFlowGroup != null) {
			ruleSetNode.setRuleFlowGroup(ruleFlowGroup);
		}
        handleScript(ruleSetNode, element, "onEntry");
        handleScript(ruleSetNode, element, "onExit");
	}

	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		RuleSetNode ruleSetNode = (RuleSetNode) node;
		writeNode("businessRuleTask", ruleSetNode, xmlDump, metaDataType);
		if (ruleSetNode.getRuleFlowGroup() != null) {
			xmlDump.append("g:ruleFlowGroup=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(ruleSetNode.getRuleFlowGroup()) + "\" >" + EOL);
		}
		writeScripts(ruleSetNode, xmlDump);
		endNode("businessRuleTask", xmlDump);
	}

}
