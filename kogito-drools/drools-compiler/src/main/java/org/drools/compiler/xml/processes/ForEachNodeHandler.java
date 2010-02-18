package org.drools.compiler.xml.processes;

import java.util.List;
import java.util.Map;

import org.drools.compiler.xml.XmlDumper;
import org.drools.definition.process.Connection;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.CompositeNode;
import org.drools.workflow.core.node.ForEachNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ForEachNodeHandler extends CompositeNodeHandler {

    protected Node createNode() {
        return new ForEachNode();
    }

    public Class generateNodeFor() {
        return ForEachNode.class;
    }
    
    protected String getNodeName() {
    	return "forEach";
    }
    
    protected void writeAttributes(CompositeNode compositeNode, StringBuilder xmlDump, boolean includeMeta) {
    	ForEachNode forEachNode = (ForEachNode) compositeNode;
    	String variableName = forEachNode.getVariableName();
    	if (variableName != null) {
    		xmlDump.append("variableName=\"" + variableName + "\" ");
    	}
    	String collectionExpression = forEachNode.getCollectionExpression();
    	if (collectionExpression != null) {
    		xmlDump.append("collectionExpression=\"" + XmlDumper.replaceIllegalChars(collectionExpression) + "\" ");
    	}
    	boolean waitForCompletion = forEachNode.isWaitForCompletion();
    	if (!waitForCompletion) {
    		xmlDump.append("waitForCompletion=\"false\" ");
    	}
    }

    protected List<Node> getSubNodes(CompositeNode compositeNode) {
    	return super.getSubNodes(((ForEachNode) compositeNode).getCompositeNode());
    }

    protected List<Connection> getSubConnections(CompositeNode compositeNode) {
    	return super.getSubConnections(((ForEachNode) compositeNode).getCompositeNode());
    }

    protected Map<String, CompositeNode.NodeAndType> getInPorts(CompositeNode compositeNode) {
    	return ((ForEachNode) compositeNode).getCompositeNode().getLinkedIncomingNodes();
    }
    
    protected Map<String, CompositeNode.NodeAndType> getOutPorts(CompositeNode compositeNode) {
    	return ((ForEachNode) compositeNode).getCompositeNode().getLinkedOutgoingNodes();
    }
    
    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	ForEachNode forEachNode = (ForEachNode) node;
        final String variableName = element.getAttribute("variableName");
        if (variableName != null && variableName.length() != 0) {
            forEachNode.setVariable(variableName, new ObjectDataType());
        }
        final String collectionExpression = element.getAttribute("collectionExpression");
        if (collectionExpression != null && collectionExpression.length() != 0) {
            forEachNode.setCollectionExpression(collectionExpression);
        }
        final String waitForCompletion = element.getAttribute("waitForCompletion");
        if ("false".equals(waitForCompletion)) {
            forEachNode.setWaitForCompletion(false);
        }
    }

}
