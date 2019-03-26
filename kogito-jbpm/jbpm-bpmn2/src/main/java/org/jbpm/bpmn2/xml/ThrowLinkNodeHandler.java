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

import java.util.List;

import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.xml.sax.Attributes;

public class ThrowLinkNodeHandler extends AbstractNodeHandler {

	public Class<?> generateNodeFor() {
		return ThrowLinkNode.class;
	}

	@Override
	protected Node createNode(Attributes attrs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {

		ThrowLinkNode linkNode = (ThrowLinkNode) node;

		writeNode("intermediateThrowEvent", linkNode, xmlDump, metaDataType);
		xmlDump.append(">" + EOL);
		writeExtensionElements(node, xmlDump);
		
		String name = (String) node.getMetaData().get(
				IntermediateThrowEventHandler.LINK_NAME);

		xmlDump.append("<linkEventDefinition name=\"" + name + "\" >" + EOL);

		List<String> sources = (List<String>) linkNode
				.getMetaData(IntermediateThrowEventHandler.LINK_SOURCE);

		if (null != sources) {
			for (String s : sources) {
				xmlDump.append(String.format("<source>%s</source>", s) + EOL);
			}
		}
		xmlDump.append("</linkEventDefinition>" + EOL);

		endNode("intermediateThrowEvent", xmlDump);

	}
}
