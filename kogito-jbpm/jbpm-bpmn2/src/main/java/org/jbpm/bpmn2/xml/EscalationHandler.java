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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.jbpm.bpmn2.core.DataStore;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.Interface;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EscalationHandler extends BaseAbstractHandler implements Handler {
	
	@SuppressWarnings("unchecked")
	public EscalationHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet();
			this.validParents.add(Definitions.class);

			this.validPeers = new HashSet();
			this.validPeers.add(null);
            this.validPeers.add(ItemDefinition.class);
            this.validPeers.add(Message.class);
            this.validPeers.add(Interface.class);
            this.validPeers.add(Escalation.class);
            this.validPeers.add(Error.class);
            this.validPeers.add(DataStore.class);
            this.validPeers.add(RuleFlowProcess.class);

			this.allowNesting = false;
		}
	}

	@SuppressWarnings("unchecked")
    public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		String id = attrs.getValue("id");
		String escalationCode = attrs.getValue("escalationCode");

		ProcessBuildData buildData = (ProcessBuildData) parser.getData();
		Map<String, Escalation> escalations = (Map<String, Escalation>) buildData.getMetaData("Escalations");
        if (escalations == null) {
        	escalations = new HashMap<String, Escalation>();
            buildData.setMetaData("Escalations", escalations);
        }
        Escalation e = new Escalation(id, escalationCode); 
        escalations.put(id, e);
		return e;
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		return parser.getCurrent();
	}

	public Class<?> generateNodeFor() {
		return Escalation.class;
	}

}