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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.Lane;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.kie.api.definition.process.WorkflowProcess;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class LaneHandler extends BaseAbstractHandler implements Handler {
	
    public static final String LANES = "BPMN.Lanes";

	@SuppressWarnings("unchecked")
	public LaneHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet();
			this.validParents.add(RuleFlowProcess.class);

			this.validPeers = new HashSet();
	        this.validPeers.add(null);
	        this.validPeers.add(Lane.class);
	        this.validPeers.add(Variable.class);
	        this.validPeers.add(Node.class);
	        this.validPeers.add(SequenceFlow.class);
	        this.validPeers.add(Lane.class);
	        this.validPeers.add(Association.class);

			this.allowNesting = false;
		}
	}

	@SuppressWarnings("unchecked")
    public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		String id = attrs.getValue("id");
		String name = attrs.getValue("name");
		
		WorkflowProcess process = (WorkflowProcess) parser.getParent();
        
		List<Lane> lanes = (List<Lane>)
            ((RuleFlowProcess) process).getMetaData(LaneHandler.LANES);          
        if (lanes == null) {
            lanes = new ArrayList<Lane>();
            ((RuleFlowProcess) process).setMetaData(LaneHandler.LANES, lanes);
        }
        Lane lane = new Lane(id); 
        lane.setName(name);
        lanes.add(lane);
		return lane;
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Lane lane = (Lane) parser.getCurrent();
        
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("flowNodeRef".equals(nodeName)) {
                String flowElementRef = xmlNode.getTextContent();
                lane.addFlowElement(flowElementRef);
            }
            xmlNode = xmlNode.getNextSibling();
        }
        return lane;
    }

	public Class<?> generateNodeFor() {
		return Lane.class;
	}

}
