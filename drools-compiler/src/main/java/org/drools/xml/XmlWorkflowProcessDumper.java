package org.drools.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.WorkflowProcess;
import org.drools.process.core.context.exception.ActionExceptionHandler;
import org.drools.process.core.context.exception.ExceptionHandler;
import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.core.context.swimlane.Swimlane;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.xml.processes.AbstractNodeHandler;

public class XmlWorkflowProcessDumper {
    
    private final static String EOL = System.getProperty( "line.separator" );
    
    private String type;
    private String namespace;
    private String schemaLocation;
    private SemanticModule semanticModule;
    
    public XmlWorkflowProcessDumper(String type, String namespace, String schemaLocation, SemanticModule semanticModule) {
        this.type = type;
        this.namespace = namespace;
        this.schemaLocation = schemaLocation;
        this.semanticModule = semanticModule;
    }
    
    public String dump(WorkflowProcess process) {
        return dump(process, true);
    }
    
    public String dump(WorkflowProcess process, boolean includeMeta) {
        StringBuilder xmlDump = new StringBuilder();
        visitProcess(process, xmlDump, includeMeta);
        return xmlDump.toString();
    }
    
    protected void visitProcess(WorkflowProcess process, StringBuilder xmlDump, boolean includeMeta) {
        xmlDump.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + EOL
            + "<process xmlns=\"" + namespace + "\"" + EOL
            + "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" + EOL
            + "         xs:schemaLocation=\"" + namespace + " " + schemaLocation + "\"" + EOL
            + "         type=\"" + type + "\" ");
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
    
    protected void visitHeader(WorkflowProcess process, StringBuilder xmlDump, boolean includeMeta) {
        xmlDump.append("  <header>" + EOL);
        visitImports(((org.drools.process.core.Process) process).getImports(), xmlDump);
        visitGlobals(((org.drools.process.core.Process) process).getGlobals(), xmlDump);
        visitFunctionImports(((org.drools.process.core.Process) process).getFunctionImports(), xmlDump);
        VariableScope variableScope = (VariableScope)
        	((org.drools.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        if (variableScope != null) {
            visitVariables(variableScope.getVariables(), xmlDump);
        }
        SwimlaneContext swimlaneContext = (SwimlaneContext)
        	((org.drools.process.core.Process) process).getDefaultContext(SwimlaneContext.SWIMLANE_SCOPE);
        if (swimlaneContext != null) {
            visitSwimlanes(swimlaneContext.getSwimlanes(), xmlDump);
        }
        ExceptionScope exceptionScope = (ExceptionScope)
        	((org.drools.process.core.Process) process).getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
        if (exceptionScope != null) {
            visitExceptionHandlers(exceptionScope.getExceptionHandlers(), xmlDump);
        }
        xmlDump.append("  </header>" + EOL + EOL);
    }
    
    private void visitImports(List<String> imports, StringBuilder xmlDump) {
        if (imports != null && imports.size() > 0) {
            xmlDump.append("    <imports>" + EOL);
            for (String importString: imports) {
                xmlDump.append("      <import name=\"" + importString + "\" />" + EOL);
            }
            xmlDump.append("    </imports>" + EOL);
        }
    }
    
    private void visitFunctionImports(List<String> imports, StringBuilder xmlDump) {
        if (imports != null && imports.size() > 0) {
            xmlDump.append("    <functionImports>" + EOL);
            for (String importString: imports) {
                xmlDump.append("      <functionImport name=\"" + importString + "\" />" + EOL);
            }
            xmlDump.append("    </functionImports>" + EOL);
        }
    }
    
    private void visitGlobals(Map<String, String> globals, StringBuilder xmlDump) {
        if (globals != null && globals.size() > 0) {
            xmlDump.append("    <globals>" + EOL);
            for (Map.Entry<String, String> global: globals.entrySet()) {
                xmlDump.append("      <global identifier=\"" + global.getKey() + "\" type=\"" + global.getValue() + "\" />" + EOL);
            }
            xmlDump.append("    </globals>" + EOL);
        }
    }
    
    public static void visitVariables(List<Variable> variables, StringBuilder xmlDump) {
        if (variables != null && variables.size() > 0) {
            xmlDump.append("    <variables>" + EOL);
            for (Variable variable: variables) {
                xmlDump.append("      <variable name=\"" + variable.getName() + "\" >" + EOL);
                visitDataType(variable.getType(), xmlDump);
                Object value = variable.getValue();
                if (value != null) {
                    visitValue(variable.getValue(), variable.getType(), xmlDump);
                }
                xmlDump.append("      </variable>" + EOL);
            }
            xmlDump.append("    </variables>" + EOL);
        }
    }
    
    private void visitSwimlanes(Collection<Swimlane> swimlanes, StringBuilder xmlDump) {
        if (swimlanes != null && swimlanes.size() > 0) {
            xmlDump.append("    <swimlanes>" + EOL);
            for (Swimlane swimlane: swimlanes) {
                xmlDump.append("      <swimlane name=\"" + swimlane.getName() + "\" />" + EOL);
            }
            xmlDump.append("    </swimlanes>" + EOL);
        }
    }
    
    public static void visitExceptionHandlers(Map<String, ExceptionHandler> exceptionHandlers, StringBuilder xmlDump) {
        if (exceptionHandlers != null && exceptionHandlers.size() > 0) {
            xmlDump.append("    <exceptionHandlers>" + EOL);
            for (Map.Entry<String, ExceptionHandler> entry: exceptionHandlers.entrySet()) {
            	ExceptionHandler exceptionHandler = entry.getValue();
            	if (exceptionHandler instanceof ActionExceptionHandler) {
            		ActionExceptionHandler actionExceptionHandler = (ActionExceptionHandler) exceptionHandler;
            		xmlDump.append("      <exceptionHandler faultName=\"" + entry.getKey() + "\" type=\"action\" ");
            		String faultVariable = exceptionHandler.getFaultVariable();
            		if (faultVariable != null && faultVariable.length() > 0) {
            			xmlDump.append("faultVariable=\"" + faultVariable + "\" ");
            		}
        			xmlDump.append(">" + EOL);
        			DroolsAction action = actionExceptionHandler.getAction();
        			if (action != null) {
        				AbstractNodeHandler.writeAction(action, xmlDump);
        			}
        			xmlDump.append("      </exceptionHandler>" + EOL);
            	} else {
            		throw new IllegalArgumentException(
        				"Unknown exception handler type: " + exceptionHandler);
            	}
            }
            xmlDump.append("    </exceptionHandlers>" + EOL);
        }
    }
    
    public static void visitDataType(DataType dataType, StringBuilder xmlDump) {
        xmlDump.append("        <type name=\"" + dataType.getClass().getName() + "\" ");
        // TODO make this pluggable so datatypes can write out other properties as well
        if (dataType instanceof ObjectDataType) {
        	String className = ((ObjectDataType) dataType).getClassName();
        	if (className != null
        			&& className.trim().length() > 0 
        			&& !"java.lang.Object".equals(className)) {
        		xmlDump.append("className=\"" + className + "\" ");
        	}
        }
        xmlDump.append("/>" + EOL);
    }
    
    public static void visitValue(Object value, DataType dataType, StringBuilder xmlDump) {
		xmlDump.append("        <value>" + XmlDumper.replaceIllegalChars(dataType.writeValue(value)) + "</value>" + EOL);
    }
    
    private void visitNodes(WorkflowProcess process, StringBuilder xmlDump, boolean includeMeta) {
        xmlDump.append("  <nodes>" + EOL);
        for (Node node: process.getNodes()) {
            visitNode(node, xmlDump, includeMeta);
        }
        xmlDump.append("  </nodes>" + EOL + EOL);
    }
    
    public void visitNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
     	Handler handler = semanticModule.getHandlerByClass(node.getClass());
        if (handler != null) {
        	((AbstractNodeHandler) handler).writeNode((org.drools.workflow.core.Node) node, xmlDump, includeMeta);
        } else {
        	throw new IllegalArgumentException(
                "Unknown node type: " + node);
        }
    }
    
    private void visitConnections(Node[] nodes, StringBuilder xmlDump, boolean includeMeta) {
        List<Connection> connections = new ArrayList<Connection>();
        for (Node node: nodes) {
            for (List<Connection> connectionList: node.getIncomingConnections().values()) {
                connections.addAll(connectionList);
            }
        }
        xmlDump.append("  <connections>" + EOL);
        for (Connection connection: connections) {
            visitConnection(connection, xmlDump, includeMeta);
        }
        xmlDump.append("  </connections>" + EOL + EOL);
    }
    
    public void visitConnection(Connection connection, StringBuilder xmlDump, boolean includeMeta) {
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
