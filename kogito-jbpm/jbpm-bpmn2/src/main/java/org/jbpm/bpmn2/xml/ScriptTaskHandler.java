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

package org.jbpm.bpmn2.xml;

import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ScriptTaskHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        ActionNode result = new ActionNode();
        result.setAction(new DroolsConsequenceAction());
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Node.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
        ActionNode actionNode = (ActionNode) node;
        node.setMetaData("NodeType", "ScriptTask");
        DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
        if (action == null) {
        	action = new DroolsConsequenceAction();
        	actionNode.setAction(action);
        }
		String language = element.getAttribute("scriptFormat");
		if (XmlBPMNProcessDumper.JAVA_LANGUAGE.equals(language)) {
			action.setDialect(JavaDialect.ID);
		} else if (XmlBPMNProcessDumper.JAVASCRIPT_LANGUAGE.equals(language)) {
		    action.setDialect("JavaScript");
		}
		action.setConsequence("");
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	if (xmlNode instanceof Element && "script".equals(xmlNode.getNodeName())) {
        		action.setConsequence(xmlNode.getTextContent());
        	}
        	xmlNode = xmlNode.getNextSibling();
        }
        
        String compensation = element.getAttribute("isForCompensation");
        if( compensation != null ) {
            boolean isForCompensation = Boolean.parseBoolean(compensation);
            if( isForCompensation ) { 
                actionNode.setMetaData("isForCompensation", isForCompensation );
            }
        }
	}

	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
	    throw new IllegalArgumentException("Writing out should be handled by action node handler");
	}

}
