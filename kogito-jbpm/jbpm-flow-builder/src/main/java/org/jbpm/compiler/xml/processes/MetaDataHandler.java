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

import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.process.core.ValueObject;
import org.jbpm.workflow.core.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MetaDataHandler extends BaseAbstractHandler
    implements
    Handler {
    public MetaDataHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Node.class );

            this.validPeers = new HashSet();         
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        Node node = (Node) parser.getParent();
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        return new MetaDataWrapper(node, name);
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return MetaDataWrapper.class;
    }
    
    public class MetaDataWrapper implements ValueObject {
    	private Node node;
    	private String name;
    	public MetaDataWrapper(Node node, String name) {
    		this.node = node;
    		this.name = name;
    	}
		public Object getValue() {
			return node.getMetaData().get(name);
		}
		public void setValue(Object value) {
			node.setMetaData(name, value);
		}
		public DataType getType() {
			return new StringDataType();
		}
		public void setType(DataType type) {
		}
    }

}
