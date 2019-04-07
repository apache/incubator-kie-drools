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

import org.drools.core.xml.Handler;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.xml.sax.Attributes;

public class CatchLinkNodeHandler extends AbstractNodeHandler implements
		Handler {

	public Class<?> generateNodeFor() {
		return CatchLinkNode.class;
	}

	@Override
	protected Node createNode(Attributes attrs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {

		CatchLinkNode linkNode = (CatchLinkNode) node;
		writeNode("intermediateCatchEvent", linkNode, xmlDump, metaDataType);
		xmlDump.append(">" + EOL);
        writeExtensionElements(linkNode, xmlDump);

		String name = (String) node.getMetaData().get(
				IntermediateCatchEventHandler.LINK_NAME);

		xmlDump.append("<linkEventDefinition name=\"" + name + "\" >" + EOL);

		Object target = linkNode.getMetaData("target");
		if (null != target) {
			xmlDump.append(String.format("<target>%s</target>", target) + EOL);
		}
		xmlDump.append("</linkEventDefinition>" + EOL);
		endNode("intermediateCatchEvent", xmlDump);

	}

}
