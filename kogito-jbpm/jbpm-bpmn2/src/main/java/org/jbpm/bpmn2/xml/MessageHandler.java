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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.core.*;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MessageHandler extends BaseAbstractHandler implements Handler {
	
	@SuppressWarnings("unchecked")
	public MessageHandler() {
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
            this.validPeers.add(Signal.class);
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
		String itemRef = attrs.getValue("itemRef");
		String name = attrs.getValue("name");
		if (name == null) {
		    name = id;
		}
		
		Map<String, ItemDefinition> itemDefinitions = (Map<String, ItemDefinition>)
            ((ProcessBuildData) parser.getData()).getMetaData("ItemDefinitions");
        if (itemDefinitions == null) {
            throw new IllegalArgumentException("No item definitions found");
        }
        ItemDefinition itemDefinition = itemDefinitions.get(itemRef);
        if (itemDefinition == null) {
            throw new IllegalArgumentException("Could not find itemDefinition " + itemRef);
        }
        
        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
		Map<String, Message> messages = (Map<String, Message>)
            ((ProcessBuildData) parser.getData()).getMetaData("Messages");
        if (messages == null) {
            messages = new HashMap<String, Message>();
            buildData.setMetaData("Messages", messages);
        }
        Message message = new Message(id); 
        message.setType(itemDefinition.getStructureRef());
        message.setName(name);
        messages.put(id, message);
		return message;
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		return parser.getCurrent();
	}

	public Class<?> generateNodeFor() {
		return Message.class;
	}

}
