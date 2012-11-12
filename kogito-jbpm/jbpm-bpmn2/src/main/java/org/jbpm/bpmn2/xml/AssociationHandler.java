/**
 * Copyright 2010 Intalio Inc
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.Lane;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.workflow.core.Node;
import org.kie.definition.process.Process;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
/**
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 *
 */
public class AssociationHandler extends BaseAbstractHandler implements Handler {

	public AssociationHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet<Class<?>>();
			this.validParents.add(Process.class);

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
		
		Process parent = (Process) parser.getParent();
		Definitions definitions = (Definitions) 
			parent.getMetaData().get("Definitions");
		
		// FIXME for now associations are stored under the definitions node
		// we will move them under process and subprocesses when it becomes possible ?
		List<Association> associations = definitions.getAssociations();
		if (associations == null) {
			associations = new ArrayList<Association>();
			definitions.setAssociations(associations);
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
