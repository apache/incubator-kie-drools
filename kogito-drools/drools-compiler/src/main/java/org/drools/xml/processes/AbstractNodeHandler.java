package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class AbstractNodeHandler extends BaseAbstractHandler implements
        Handler {

    public AbstractNodeHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet();
        this.validParents.add(NodeContainer.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet();
        this.validPeers.add(null);
        this.validPeers.add(Node.class);
    }

    public Object start(final String uri, final String localName, final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startConfiguration(localName, attrs);

        NodeContainer nodeContainer = (NodeContainer) parser.getParent();

        final Node node = createNode();

        final String id = attrs.getValue("id");
        node.setId(new Long(id));

        final String name = attrs.getValue("name");
        node.setName(name);

        nodeContainer.addNode(node);
        ((ProcessBuildData) parser.getData()).addNode(node);

        return node;
    }

    protected abstract Node createNode();

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Configuration config = parser.endConfiguration();
        Node node = (Node) parser.getCurrent();
        handleNode(node, config, uri, localName, parser);
        return node;
    }
    
    protected void handleNode(final Node node, final Configuration config, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        final String x = config.getAttribute("x");
        if (x != null && !"".equals(x)) {
            try {
                node.setMetaData("x", new Integer(x));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'x' attribute", parser.getLocator());
            }
        }
        final String y = config.getAttribute("y");
        if (y != null && !"".equals(y)) {
            try {
                node.setMetaData("y", new Integer(y));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'y' attribute", parser.getLocator());
            }
        }
        final String width = config.getAttribute("width");
        if (width != null && !"".equals(width)) {
            try {
                node.setMetaData("width", new Integer(width));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'width' attribute", parser.getLocator());
            }
        }
        final String height = config.getAttribute("height");
        if (height != null && !"".equals(height)) {
            try {
                node.setMetaData("height", new Integer(height));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'height' attribute", parser.getLocator());
            }
        }
    }

}
