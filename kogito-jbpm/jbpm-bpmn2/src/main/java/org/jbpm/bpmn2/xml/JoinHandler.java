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

import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.Join;
import org.xml.sax.Attributes;

public class JoinHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
    	throw new IllegalArgumentException("Reading in should be handled by gateway handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Join.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		Join join = (Join) node;
		String type = null;
		switch (join.getType()) {
			case Join.TYPE_AND:
				type = "parallelGateway";
				break;
			case Join.TYPE_XOR:
				type = "exclusiveGateway";
				break;
			case Join.TYPE_OR:
                type = "inclusiveGateway";
                break;
			default:
				type = "complexGateway";
		}
		writeNode(type, node, xmlDump, metaDataType);
		xmlDump.append("gatewayDirection=\"Converging\" >" + EOL);
        writeExtensionElements(join, xmlDump);
		endNode(type, xmlDump);
	}

}
