package org.drools.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.datatype.DataType;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.xml.processes.AbstractNodeHandler;

public class XmlRuleFlowProcessDumper {
    
    private final static String EOL = System.getProperty( "line.separator" );
    
    private static final SemanticModule semanticModule = new ProcessSemanticModule();
    
    public static String dump(RuleFlowProcess process) {
        return dump(process, true);
    }
    
    public static String dump(RuleFlowProcess process, boolean includeMeta) {
        StringBuffer xmlDump = new StringBuffer();
        visitProcess(process, xmlDump, includeMeta);
        return xmlDump.toString();
    }
    
    private static void visitProcess(RuleFlowProcess process, StringBuffer xmlDump, boolean includeMeta) {
        xmlDump.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + EOL
            + "<process xmlns=\"http://drools.org/drools-4.0/process\"" + EOL
            + "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" + EOL
            + "         xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"" + EOL
            + "         type=\"RuleFlow\" ");
        if (process.getName() != null) {
            xmlDump.append("name=\"" + process.getName() + "\" ");
        }
        if (process.getId() != null) {
            xmlDump.append("id=\"" + process.getId() + "\" ");
        }
        if (process.getPackageName() != null) {
            xmlDump.append("package-name=\"" + process.getPackageName() + "\" ");
        }
        if (process.getVersion() != null) {
            xmlDump.append("version=\"" + process.getVersion() + "\" ");
        }
        if (includeMeta) {
            Integer routerLayout = (Integer) process.getMetaData("routerLayout");
            if (routerLayout != null && routerLayout != 0) {
                xmlDump.append("routerLayout=\"" + routerLayout + "\" ");
            }
        }
        xmlDump.append(">" + EOL + EOL);
        visitHeader(process, xmlDump, includeMeta);
        visitNodes(process, xmlDump, includeMeta);
        visitConnections(process.getNodes(), xmlDump, includeMeta);
        xmlDump.append("</process>");
    }
    
    private static void visitHeader(RuleFlowProcess process, StringBuffer xmlDump, boolean includeMeta) {
        xmlDump.append("  <header>" + EOL);
        visitImports(process.getImports(), xmlDump);
        visitGlobals(process.getGlobals(), xmlDump);
        visitVariables(process.getVariableScope().getVariables(), xmlDump);
        xmlDump.append("  </header>" + EOL + EOL);
    }
    
    private static void visitImports(List<String> imports, StringBuffer xmlDump) {
        if (imports != null && imports.size() > 0) {
            xmlDump.append("    <imports>" + EOL);
            for (String importString: imports) {
                xmlDump.append("      <import name=\"" + importString + "\" />" + EOL);
            }
            xmlDump.append("    </imports>" + EOL);
        }
    }
    
    private static void visitGlobals(Map<String, String> globals, StringBuffer xmlDump) {
        if (globals != null && globals.size() > 0) {
            xmlDump.append("    <globals>" + EOL);
            for (Map.Entry<String, String> global: globals.entrySet()) {
                xmlDump.append("      <global identifier=\"" + global.getKey() + "\" type=\"" + global.getValue() + "\" />" + EOL);
            }
            xmlDump.append("    </globals>" + EOL);
        }
    }
    
    private static void visitVariables(List<Variable> variables, StringBuffer xmlDump) {
        if (variables != null && variables.size() > 0) {
            xmlDump.append("    <variables>" + EOL);
            for (Variable variable: variables) {
                xmlDump.append("      <variable name=\"" + variable.getName() + "\" >" + EOL);
                visitDataType(variable.getType(), xmlDump);
                Object value = variable.getValue();
                if (value != null) {
                    visitValue(variable.getValue(), xmlDump);
                }
                xmlDump.append("      </variable>" + EOL);
            }
            xmlDump.append("    </variables>" + EOL);
        }
    }
    
    private static void visitDataType(DataType dataType, StringBuffer xmlDump) {
        xmlDump.append("        <type name=\"" + dataType.getClass().getName() + "\" />" + EOL);
    }
    
    private static void visitValue(Object value, StringBuffer xmlDump) {
        xmlDump.append("        <value>" + value + "</value>" + EOL);
    }
    
    private static void visitNodes(RuleFlowProcess process, StringBuffer xmlDump, boolean includeMeta) {
        xmlDump.append("  <nodes>" + EOL);
        for (Node node: process.getNodes()) {
            visitNode(node, xmlDump, includeMeta);
        }
        xmlDump.append("  </nodes>" + EOL + EOL);
    }
    
    public static void visitNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
     	Handler handler = semanticModule.getHandlerByClass(node.getClass());
        if (handler != null) {
        	((AbstractNodeHandler) handler).writeNode(node, xmlDump, includeMeta);
        } else {
        	throw new IllegalArgumentException(
                "Unknown node type: " + node);
        }
    }
    
    private static void visitConnections(Node[] nodes, StringBuffer xmlDump, boolean includeMeta) {
        List<Connection> connections = new ArrayList<Connection>();
        for (Node node: nodes) {
            connections.addAll(node.getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE));
        }
        xmlDump.append("  <connections>" + EOL);
        for (Connection connection: connections) {
            visitConnection(connection, xmlDump, includeMeta);
        }
        xmlDump.append("  </connections>" + EOL + EOL);
    }
    
    public static void visitConnection(Connection connection, StringBuffer xmlDump, boolean includeMeta) {
        xmlDump.append("    <connection from=\"" + connection.getFrom().getId() + "\" ");
        if (!NodeImpl.CONNECTION_DEFAULT_TYPE.equals(connection.getFromType())) {
            xmlDump.append("fromType=\"" + connection.getFromType() + "\" ");
        }
        xmlDump.append("to=\"" + connection.getTo().getId() + "\" ");
        if (!NodeImpl.CONNECTION_DEFAULT_TYPE.equals(connection.getToType())) {
            xmlDump.append("toType=\"" + connection.getToType() + "\" ");
        }
        if (includeMeta) {
            String bendpoints = (String) connection.getMetaData("bendpoints");
            if (bendpoints != null) {
                xmlDump.append("bendpoints=\"" + bendpoints + "\" ");
            }
        }
        xmlDump.append("/>" + EOL);
    }
    
}
