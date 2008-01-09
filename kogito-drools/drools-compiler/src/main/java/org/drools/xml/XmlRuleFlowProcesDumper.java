package org.drools.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.StartNode;

public class XmlRuleFlowProcesDumper {
    
    // TODO writing out should also be part of the NodeHandler ?

    private final static String EOL = System.getProperty( "line.separator" );
    
    private StringBuffer xmlDump;
    
    public synchronized String dump(RuleFlowProcess process) {
        this.xmlDump = new StringBuffer();
        visitProcess(process);
        return this.xmlDump.toString();
    }
    
    private void visitProcess(RuleFlowProcess process) {
        xmlDump.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + EOL
            + "<process xmlns=\"http://drools.org/drools-4.0/process\"" + EOL
            + "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" + EOL
            + "         xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"" + EOL
            + "         name=\"" + process.getName() + "\" id=\"" + process.getId() + "\" type=\"RuleFlow\" package-name=\"" + process.getPackageName() + "\" >" + EOL + EOL);
        visitHeader(process);
        visitNodes(process);
        visitConnections(process);
        xmlDump.append("</process>");
    }
    
    private void visitHeader(RuleFlowProcess process) {
        xmlDump.append("  <header>" + EOL);
        visitImports(process.getImports());
        visitGlobals(process.getGlobals());
        xmlDump.append("  </header>" + EOL + EOL);
    }
    
    private void visitImports(List<String> imports) {
        xmlDump.append("    <imports>" + EOL);
        for (String importString: imports) {
            xmlDump.append("      <import name=\"" + importString + "\" />" + EOL);
        }
        xmlDump.append("    </imports>" + EOL);
    }
    
    private void visitGlobals(Map<String, String> globals) {
        xmlDump.append("    <globals>" + EOL);
        for (Map.Entry<String, String> global: globals.entrySet()) {
            xmlDump.append("      <global identifier=\"" + global.getKey() + "\" type=\"" + global.getValue() + "\" />" + EOL);
        }
        xmlDump.append("    </globals>" + EOL);
    }
    
    private void visitNodes(RuleFlowProcess process) {
        xmlDump.append("  <nodes>" + EOL);
        for (Node node: process.getNodes()) {
            if (node instanceof StartNode) {
                visitStartNode((StartNode) node);
            } else if (node instanceof EndNode) {
                visitEndNode((EndNode) node);
            } else if (node instanceof ActionNode) {
                visitActionNode((ActionNode) node);
            } else {
                throw new IllegalArgumentException(
                    "Unknown node type: " + node);
            }
        }
        xmlDump.append("  </nodes>" + EOL + EOL);
    }
    
    private void visitStartNode(StartNode startNode) {
        xmlDump.append("    <start name=\"" + startNode.getName() + "\" />" + EOL);
    }
    
    private void visitEndNode(EndNode endNode) {
        xmlDump.append("    <end name=\"" + endNode.getName() + "\" />" + EOL);
    }
    
    private void visitActionNode(ActionNode actionNode) {
        DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
        xmlDump.append("    <action name=\"" + actionNode.getName() 
            + "\" dialect=\"" + action.getDialect() + "\">"
            + action.getConsequence() + "</action>" + EOL);
    }
    
    private void visitConnections(RuleFlowProcess process) {
        List<Connection> connections = new ArrayList<Connection>();
        for (Node node: process.getNodes()) {
            connections.addAll(node.getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE));
        }
        xmlDump.append("  <connections>" + EOL);
        for (Connection connection: connections) {
            xmlDump.append("    <connection from=\"" + connection.getFrom().getName() + "\" to=\"" + connection.getTo().getName() + "\"/>" + EOL);
        }
        xmlDump.append("  </connections>" + EOL + EOL);
    }
    
}
