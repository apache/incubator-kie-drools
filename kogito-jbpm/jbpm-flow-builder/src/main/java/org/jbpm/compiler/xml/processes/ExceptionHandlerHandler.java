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

package org.jbpm.compiler.xml.processes;

import java.util.HashSet;

import org.kie.api.definition.process.Process;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.workflow.core.DroolsAction;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ExceptionHandlerHandler extends BaseAbstractHandler implements Handler {
	
	public ExceptionHandlerHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet<Class<?>>();
			this.validParents.add(Process.class);

			this.validPeers = new HashSet<Class<?>>();
			this.validPeers.add(null);

			this.allowNesting = false;
		}
	}

	public Object start(final String uri, final String localName,
			final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);
		return null;
	}

	public Object end(final String uri, final String localName,
			final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		ContextContainer contextContainer = (ContextContainer) parser.getParent();

		final String type = element.getAttribute("type");
		emptyAttributeCheck(localName, "type", type, parser);

		final String faultName = element.getAttribute("faultName");
		emptyAttributeCheck(localName, "faultName", type, parser);
		
		final String faultVariable = element.getAttribute("faultVariable");
		
		ActionExceptionHandler exceptionHandler = null;
		if ("action".equals(type)) {
			exceptionHandler = new ActionExceptionHandler();
			org.w3c.dom.Node xmlNode = element.getFirstChild();
	        if (xmlNode instanceof Element) {
	    		Element actionXml = (Element) xmlNode;
	    		DroolsAction action = ActionNodeHandler.extractAction(actionXml); 
	    		((ActionExceptionHandler) exceptionHandler).setAction(action);
	        }
		} else {
			throw new SAXParseException("Unknown exception handler type " + type, parser.getLocator());
		}
		
		if (faultVariable != null && faultVariable.length() > 0) {
			exceptionHandler.setFaultVariable(faultVariable);
		}
		
		ExceptionScope exceptionScope = (ExceptionScope)
			contextContainer.getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
		if (exceptionScope == null) {
			exceptionScope = new ExceptionScope();
	        contextContainer.addContext(exceptionScope);
	        contextContainer.setDefaultContext(exceptionScope);
		}
		
		exceptionScope.setExceptionHandler(faultName, exceptionHandler);

		return null;
	}

	public Class<?> generateNodeFor() {
		return null;
	}

}
