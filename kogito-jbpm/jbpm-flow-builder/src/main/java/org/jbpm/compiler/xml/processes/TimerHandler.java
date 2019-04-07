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

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TimerHandler extends BaseAbstractHandler implements Handler {
	
    public TimerHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add( StateBasedNode.class );

            this.validPeers = new HashSet<Class<?>>();         
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName, attrs );
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        Element element = parser.endElementBuilder();
        StateBasedNode parent = (StateBasedNode) parser.getParent();
        String id = element.getAttribute("id");
        emptyAttributeCheck( localName, "id", id, parser );
        String delay = element.getAttribute("delay");
        String period = element.getAttribute("period");
        Timer timer = new Timer();
        timer.setId(new Long(id));
        if (delay != null && delay.length() != 0 ) {
            timer.setDelay(delay);
        }
        if (period != null && period.length() != 0 ) {
            timer.setPeriod(period);
        }
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        DroolsAction action = null;
        if (xmlNode instanceof Element) {
    		Element actionXml = (Element) xmlNode;
    		action = AbstractNodeHandler.extractAction(actionXml);
        }
        parent.addTimer(timer, action);
        return null;
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return null;
    }    

}
