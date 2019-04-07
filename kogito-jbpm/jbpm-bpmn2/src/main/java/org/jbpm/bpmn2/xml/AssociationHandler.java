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

import static org.jbpm.bpmn2.xml.ProcessHandler.ASSOCIATIONS;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.Lane;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.kie.api.definition.process.Process;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AssociationHandler extends BaseAbstractHandler implements Handler {

	public AssociationHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet<Class<?>>();
			this.validParents.add(Process.class);
			this.validParents.add(CompositeContextNode.class); // for SubProcesses

			this.validPeers = new HashSet<Class<?>>();
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
	
	public Object start(final String uri, final String localName, 
			final Attributes attrs, final ExtensibleXmlParser parser) 
			throws SAXException {
		parser.startElementBuilder(localName, attrs);
		
		Association association = new Association();
		association.setId(attrs.getValue("id"));
		association.setSourceRef(attrs.getValue("sourceRef"));
		association.setTargetRef(attrs.getValue("targetRef"));
		String direction = attrs.getValue("associationDirection");
		if( direction != null ) { 
		     boolean acceptableDirection = false;
		     direction = direction.toLowerCase();
		     String [] possibleDirections = { "none", "one", "both" };
		     for( String acceptable : possibleDirections ) { 
		         if( acceptable.equals(direction) ) { 
		             acceptableDirection = true;
		             break;
		         }
		     }
		     if( ! acceptableDirection ) { 
		         throw new IllegalArgumentException("Unknown direction '" + direction + "' used in Association " + association.getId());
		     }
		}
		association.setDirection(direction);
		
		/** 
		 * BPMN2 spec, p. 66: 
		 * "At this point, BPMN provides three standard Artifacts: Associations, 
		 *  Groups, and Text Annotations.
		 * ...
		 *  When an Artifact is defined it is contained within a Collaboration
		 *  or a FlowElementsContainer (a Process or Choreography)."
		 *  
		  * (In other words: associations must be defined within a process, not outside) 
		  */
		List<Association> associations = null;
		NodeContainer nodeContainer = (NodeContainer) parser.getParent();
		if( nodeContainer instanceof Process ) { 
		    RuleFlowProcess process = (RuleFlowProcess) nodeContainer;
		    associations = (List<Association>) process.getMetaData(ASSOCIATIONS);
		    if (associations == null) {
		        associations = new ArrayList<Association>();
		        process.setMetaData(ASSOCIATIONS, associations);
		    }
		} else if( nodeContainer instanceof CompositeNode ) {
		   CompositeContextNode compositeNode = (CompositeContextNode) nodeContainer;
           associations = (List<Association>) compositeNode.getMetaData(ASSOCIATIONS);
           if (associations == null) {
               associations = new ArrayList<Association>();
               compositeNode.setMetaData(ProcessHandler.ASSOCIATIONS, associations);
           }
		}
		associations.add(association);
		
		return association;
	}

	public Object end(final String uri, final String localName,
			final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		return parser.getCurrent();
	}

	public Class<?> generateNodeFor() {
		return Association.class;
	}
}
