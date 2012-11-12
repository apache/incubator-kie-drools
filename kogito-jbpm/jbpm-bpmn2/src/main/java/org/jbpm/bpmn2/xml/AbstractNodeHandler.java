/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.drools.compiler.xml.XmlDumper;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.Lane;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class AbstractNodeHandler extends BaseAbstractHandler implements Handler {

    protected final static String EOL = System.getProperty( "line.separator" );
    protected Map<String, String> dataInputs = new HashMap<String, String>();
    protected Map<String, String> dataOutputs = new HashMap<String, String>();

    public AbstractNodeHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = true;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(NodeContainer.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
        this.validPeers.add(null);
        this.validPeers.add(Lane.class);
        this.validPeers.add(Variable.class);
        this.validPeers.add(Node.class);
        this.validPeers.add(SequenceFlow.class);
        this.validPeers.add(Lane.class);
        this.validPeers.add(Association.class);
    }

    public Object start(final String uri, final String localName, final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName, attrs );
        final Node node = createNode(attrs);
        String id = attrs.getValue("id");
        node.setMetaData("UniqueId", id);
        try {
            // remove starting _
            id = id.substring(1);
            // remove ids of parent nodes
            id = id.substring(id.lastIndexOf("-") + 1);
            final String name = attrs.getValue("name");
            node.setName(name);
            node.setId(new Integer(id));
        } catch (NumberFormatException e) {
            // id is not in the expected format, generating a new one
            long newId = 0;
            NodeContainer nodeContainer = (NodeContainer) parser.getParent();
            for (org.kie.definition.process.Node n: nodeContainer.getNodes()) {
                if (n.getId() > newId) {
                    newId = n.getId();
                }
            }
            ((org.jbpm.workflow.core.Node) node).setId(++newId);
        }
        return node;
    }

    protected abstract Node createNode(Attributes attrs);

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Node node = (Node) parser.getCurrent();
        handleNode(node, element, uri, localName, parser);
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        return node;
    }
    
    protected void handleNode(final Node node, final Element element, final String uri, 
                              final String localName, final ExtensibleXmlParser parser)
    	throws SAXException {
        final String x = element.getAttribute("x");
        if (x != null && x.length() != 0) {
            try {
                node.setMetaData("x", new Integer(x));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'x' attribute", parser.getLocator());
            }
        }
        final String y = element.getAttribute("y");
        if (y != null && y.length() != 0) {
            try {
                node.setMetaData("y", new Integer(y));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'y' attribute", parser.getLocator());
            }
        }
        final String width = element.getAttribute("width");
        if (width != null && width.length() != 0) {
            try {
                node.setMetaData("width", new Integer(width));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'width' attribute", parser.getLocator());
            }
        }
        final String height = element.getAttribute("height");
        if (height != null && height.length() != 0) {
            try {
                node.setMetaData("height", new Integer(height));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'height' attribute", parser.getLocator());
            }
        }
    }
    
    public abstract void writeNode(final Node node, final StringBuilder xmlDump,
    		                       final int metaDataType);
    
    protected void writeNode(final String name, final Node node, 
    		                 final StringBuilder xmlDump, int metaDataType) {
    	xmlDump.append("    <" + name + " "); 
        xmlDump.append("id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(node) + "\" ");
        if (node.getName() != null) {
            xmlDump.append("name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(node.getName()) + "\" ");
        }
        if (metaDataType == XmlBPMNProcessDumper.META_DATA_AS_NODE_PROPERTY) {
            Integer x = (Integer) node.getMetaData().get("x");
            Integer y = (Integer) node.getMetaData().get("y");
            Integer width = (Integer) node.getMetaData().get("width");
            Integer height = (Integer) node.getMetaData().get("height");
            if (x != null && x != 0) {
                xmlDump.append("g:x=\"" + x + "\" ");
            }
            if (y != null && y != 0) {
                xmlDump.append("g:y=\"" + y + "\" ");
            }
            if (width != null && width != -1) {
                xmlDump.append("g:width=\"" + width + "\" ");
            }
            if (height != null && height != -1) {
                xmlDump.append("g:height=\"" + height + "\" ");
            }
        }
    }
    
    protected void endNode(final StringBuilder xmlDump) {
        xmlDump.append("/>" + EOL);
    }

    protected void endNode(final String name, final StringBuilder xmlDump) {
        xmlDump.append("    </" + name + ">" + EOL);
    }
    
    protected void handleScript(final ExtendedNodeImpl node, final Element element, String type) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
        	if (nodeList.item(i) instanceof Element) {
	        	Element xmlNode = (Element) nodeList.item(i);
	        	String nodeName = xmlNode.getNodeName();
	        	if (nodeName.equals("extensionElements")) {
	                NodeList subNodeList = xmlNode.getChildNodes();
	                for (int j = 0; j < subNodeList.getLength(); j++) {
	                	org.w3c.dom.Node subXmlNode = subNodeList.item(j);
	                	if(subXmlNode.getNodeName().contains(type + "-script")) {
	                		List<DroolsAction> actions = node.getActions(type);
				    		if (actions == null) {
				    			actions = new ArrayList<DroolsAction>();
				            	node.setActions(type, actions);
				    		}
				        	DroolsAction action = extractScript((Element) subXmlNode);
				        	actions.add(action);
	                	}
			    	}
	            }
        	}
        }
    }

    public static DroolsAction extractScript(Element xmlNode) {
    	String dialect = "mvel";
    	if ("http://www.java.com/java".equals(xmlNode.getAttribute("scriptFormat"))) {
    		dialect = "java";
    	}
		NodeList subNodeList = xmlNode.getChildNodes();
        for (int j = 0; j < subNodeList.getLength(); j++) {
        	if (subNodeList.item(j) instanceof Element) {
	        	Element subXmlNode = (Element) subNodeList.item(j);
	        	if ("script".equals(subXmlNode.getNodeName())) {
	        		String consequence = subXmlNode.getTextContent();
	        		DroolsConsequenceAction action = new DroolsConsequenceAction(dialect, consequence);
	        		return action;
	        	}
        	}
    	}
		return new DroolsConsequenceAction("mvel", "");
    }
    
    protected void writeScripts(ExtendedNodeImpl node, final StringBuilder xmlDump) {
    	if (node.containsActions()) {
    		xmlDump.append("      <extensionElements>" + EOL);
    		writeScripts("onEntry", node.getActions("onEntry"), xmlDump);
    		writeScripts("onExit", node.getActions("onExit"), xmlDump);
    		xmlDump.append("      </extensionElements>" + EOL);
    	}
    }
    
    protected void writeScripts(final String type, List<DroolsAction> actions, final StringBuilder xmlDump) {
    	if (actions != null && actions.size() > 0) {
	    	for (DroolsAction action: actions) {
	    		writeScript(action, type, xmlDump);
	    	}
    	}
    }
    
    public static void writeScript(final DroolsAction action, String type, final StringBuilder xmlDump) {
    	if (action instanceof DroolsConsequenceAction) {
    		DroolsConsequenceAction consequenceAction = (DroolsConsequenceAction) action;
    		xmlDump.append("        <tns:" + type + "-script");
            String name = consequenceAction.getName();
            if (name != null) {
                xmlDump.append(" name=\"" + name + "\"");
            }
            String dialect = consequenceAction.getDialect();
            if (JavaDialect.ID.equals(dialect)) {
                xmlDump.append(" scriptFormat=\"" + XmlBPMNProcessDumper.JAVA_LANGUAGE + "\"");
            }
            String consequence = consequenceAction.getConsequence();
            if (consequence != null) {
                xmlDump.append(">" + EOL + 
                    "          <tns:script>" + XmlDumper.replaceIllegalChars(consequence.trim()) + "</tns:script>" + EOL);
                xmlDump.append("        </tns:" + type + "-script>" + EOL);
            } else {
            	xmlDump.append("/>" + EOL);
            }
    	} else {
    		throw new IllegalArgumentException(
				"Unknown action " + action);
    	}
    }
    
    protected void readIoSpecification(org.w3c.dom.Node xmlNode, Map<String, String> dataInputs, Map<String, String> dataOutputs) {
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        while (subNode instanceof Element) {
            String subNodeName = subNode.getNodeName();
            if ("dataInput".equals(subNodeName)) {
                String id = ((Element) subNode).getAttribute("id");
                String inputName = ((Element) subNode).getAttribute("name");
                dataInputs.put(id, inputName);
            }
            if ("dataOutput".equals(subNodeName)) {
                String id = ((Element) subNode).getAttribute("id");
                String outputName = ((Element) subNode).getAttribute("name");
                dataOutputs.put(id, outputName);
            }
            subNode = subNode.getNextSibling();
        }
    }
    
}
