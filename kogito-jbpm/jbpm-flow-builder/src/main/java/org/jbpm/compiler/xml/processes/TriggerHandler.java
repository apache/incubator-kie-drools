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
import org.jbpm.workflow.core.node.ConstraintTrigger;
import org.jbpm.workflow.core.node.EventTrigger;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.Trigger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TriggerHandler extends BaseAbstractHandler implements Handler {
	
    public TriggerHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add( StartNode.class );

            this.validPeers = new HashSet<Class<?>>();         
            this.validPeers.add( null );
            this.validPeers.add( Trigger.class );

            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName, attrs );
        StartNode startNode = (StartNode) parser.getParent();
        String type = attrs.getValue("type");
        emptyAttributeCheck( localName, "type", type, parser );
        
        Trigger trigger = null;
        if ("constraint".equals(type)) {
        	trigger = new ConstraintTrigger();
        } else if ("event".equals(type)) {
        	trigger = new EventTrigger();
        } else {
        	throw new SAXException("Unknown trigger type " + type);
        }
        startNode.addTrigger(trigger);
        return trigger;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return parser.getCurrent();
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Trigger.class;
    }    

}
