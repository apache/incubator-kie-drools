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

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.Join;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class JoinNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new Join();
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        Join joinNode = (Join) node;
        String type = element.getAttribute("type");
        if (type != null && type.length() != 0 ) {
            joinNode.setType(new Integer(type));
        }
        String n = element.getAttribute("n");
        if (n != null && n.length() != 0 ) {
            joinNode.setN(n);
        }
    }

    public Class generateNodeFor() {
        return Join.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		Join joinNode = (Join) node;
		writeNode("join", joinNode, xmlDump, includeMeta);
        int type = joinNode.getType();
        if (type != 0) {
            xmlDump.append("type=\"" + type + "\" ");
        }
        if (type == Join.TYPE_N_OF_M) {
        	String n = joinNode.getN();
	        if (n != null && n.length() != 0) {
	            xmlDump.append("n=\"" + n + "\" ");
	        }
        }
        if (includeMeta && containsMetaData(joinNode)) {
        	xmlDump.append(">" + EOL);
        	writeMetaData(joinNode, xmlDump);
        	endNode("join", xmlDump);
        } else {
            endNode(xmlDump);
        }
	}

}
