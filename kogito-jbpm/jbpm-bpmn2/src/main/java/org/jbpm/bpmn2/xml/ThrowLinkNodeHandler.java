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
