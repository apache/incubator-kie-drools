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

import java.util.HashSet;
import java.util.List;

import org.kie.definition.process.Process;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DefinitionsHandler extends BaseAbstractHandler implements Handler {
	
	public static final String CONNECTIONS = "BPMN.Connections";

	@SuppressWarnings("unchecked")
	public DefinitionsHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet();
			this.validParents.add(null);

			this.validPeers = new HashSet();
			this.validPeers.add(null);

			this.allowNesting = false;
		}
	}

	public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);
		return new Definitions();
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		Definitions definitions = (Definitions) parser.getCurrent();
        String namespace = element.getAttribute("targetNamespace");
        List<Process> processes = ((ProcessBuildData) parser.getData()).getProcesses();
        for (Process process : processes) {
            RuleFlowProcess ruleFlowProcess = (RuleFlowProcess)process;
            ruleFlowProcess.setMetaData("TargetNamespace", namespace);
        }
        definitions.setTargetNamespace(namespace);
        return definitions;
	}

	public Class<?> generateNodeFor() {
		return Definitions.class;
	}
	
}