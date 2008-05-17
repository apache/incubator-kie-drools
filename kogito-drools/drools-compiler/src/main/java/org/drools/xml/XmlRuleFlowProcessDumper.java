package org.drools.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.Work;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.timer.Timer;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.core.node.TimerNode;
import org.drools.workflow.core.node.WorkItemNode;

public class XmlRuleFlowProcessDumper {
    
    // TODO writing out should also be part of the NodeHandler ?

    private final static String EOL = System.getProperty( "line.separator" );
    
    private StringBuffer xmlDump;
    
    public String dump(RuleFlowProcess process) {
        return dump(process, true);
    }
    
    public synchronized String dump(RuleFlowProcess process, boolean includeMeta) {
        this.xmlDump = new StringBuffer();
        visitProcess(process, includeMeta);
        return this.xmlDump.toString();
    }
    
    private void visitProcess(RuleFlowProcess process, boolean includeMeta) {
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
        visitHeader(process, includeMeta);
        visitNodes(process, includeMeta);
        visitConnections(process, includeMeta);
        xmlDump.append("</process>");
    }
    
    private void visitHeader(RuleFlowProcess process, boolean includeMeta) {
        xmlDump.append("  <header>" + EOL);
        visitImports(process.getImports());
        visitGlobals(process.getGlobals());
        visitVariables(process.getVariableScope().getVariables());
        xmlDump.append("  </header>" + EOL + EOL);
    }
    
    private void visitImports(List<String> imports) {
        if (imports != null && imports.size() > 0) {
            xmlDump.append("    <imports>" + EOL);
            for (String importString: imports) {
                xmlDump.append("      <import name=\"" + importString + "\" />" + EOL);
            }
            xmlDump.append("    </imports>" + EOL);
        }
    }
    
    private void visitGlobals(Map<String, String> globals) {
        if (globals != null && globals.size() > 0) {
            xmlDump.append("    <globals>" + EOL);
            for (Map.Entry<String, String> global: globals.entrySet()) {
                xmlDump.append("      <global identifier=\"" + global.getKey() + "\" type=\"" + global.getValue() + "\" />" + EOL);
            }
            xmlDump.append("    </globals>" + EOL);
        }
    }
    
    private void visitVariables(List<Variable> variables) {
        if (variables != null && variables.size() > 0) {
            xmlDump.append("    <variables>" + EOL);
            for (Variable variable: variables) {
                xmlDump.append("      <variable name=\"" + variable.getName() + "\" >" + EOL);
                visitDataType(variable.getType());
                Object value = variable.getValue();
                if (value != null) {
                    visitValue(variable.getValue());
                }
                xmlDump.append("      </variable>" + EOL);
            }
            xmlDump.append("    </variables>" + EOL);
        }
    }
    
    private void visitDataType(DataType dataType) {
        xmlDump.append("        <type name=\"" + dataType.getClass().getName() + "\" />" + EOL);
    }
    
    private void visitValue(Object value) {
        xmlDump.append("        <value>" + value + "</value>" + EOL);
    }
    
    private void visitNodes(RuleFlowProcess process, boolean includeMeta) {
        xmlDump.append("  <nodes>" + EOL);
        StartNode startNode = process.getStart();
        if (startNode != null) {
            visitStartNode(startNode, includeMeta);
        }
        for (Node node: process.getNodes()) {
            if (node instanceof StartNode) {
                // Do nothing, start node already added
            } else if (node instanceof EndNode) {
                visitEndNode((EndNode) node, includeMeta);
            } else if (node instanceof ActionNode) {
                visitActionNode((ActionNode) node, includeMeta);
            } else if (node instanceof RuleSetNode) {
                visitRuleSetNode((RuleSetNode) node, includeMeta);
            } else if (node instanceof SubProcessNode) {
                visitSubProcessNode((SubProcessNode) node, includeMeta);
            } else if (node instanceof WorkItemNode) {
                visitWorkItemNode((WorkItemNode) node, includeMeta);
            } else if (node instanceof Join) {
                visitJoinNode((Join) node, includeMeta);
            } else if (node instanceof Split) {
                visitSplitNode((Split) node, includeMeta);
            } else if (node instanceof MilestoneNode) {
                visitMileStoneNode((MilestoneNode) node, includeMeta);
            } else if (node instanceof TimerNode) {
                visitTimerNode((TimerNode) node, includeMeta);
            } else {
                throw new IllegalArgumentException(
                    "Unknown node type: " + node);
            }
        }
        xmlDump.append("  </nodes>" + EOL + EOL);
    }
    
    private void visitNode(String name, Node node, boolean includeMeta) {
        xmlDump.append("    <" + name + " id=\"" + node.getId() + "\" "); 
        if (node.getName() != null) {
            xmlDump.append("name=\"" + node.getName() + "\" ");
        }
        if (includeMeta) {
            Integer x = (Integer) node.getMetaData("x");
            Integer y = (Integer) node.getMetaData("y");
            Integer width = (Integer) node.getMetaData("width");
            Integer height = (Integer) node.getMetaData("height");
            if (x != null && x != 0) {
                xmlDump.append("x=\"" + x + "\" ");
            }
            if (y != null && y != 0) {
                xmlDump.append("y=\"" + y + "\" ");
            }
            if (width != null && width != -1) {
                xmlDump.append("width=\"" + width + "\" ");
            }
            if (height != null && height != -1) {
                xmlDump.append("height=\"" + height + "\" ");
            }
        }
    }
    
    private void endElement() {
        xmlDump.append("/>" + EOL);
    }
    
    private void endElement(String name) {
        xmlDump.append("    </" + name + ">" + EOL);
    }
    
    private void visitStartNode(StartNode startNode, boolean includeMeta) {
        visitNode("start", startNode, includeMeta);
        endElement();
    }
    
    private void visitEndNode(EndNode endNode, boolean includeMeta) {
        visitNode("end", endNode, includeMeta);
        endElement();
    }
    
    private void visitActionNode(ActionNode actionNode, boolean includeMeta) {
        visitNode("action", actionNode, includeMeta);
        DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
        if (action != null) {
            String dialect = action.getDialect();
            if (dialect != null) {
                xmlDump.append("dialect=\"" + action.getDialect() + "\" ");
            }
            String consequence = action.getConsequence();
            if (consequence == null) {
                endElement();
            } else {
                xmlDump.append(">" + XmlDumper.replaceIllegalChars(consequence.trim()) + "</action>" + EOL);
            }
        } else {
            endElement();
        }
    }
    
    private void visitRuleSetNode(RuleSetNode ruleSetNode, boolean includeMeta) {
        visitNode("ruleSet", ruleSetNode, includeMeta);
        String ruleFlowGroup = ruleSetNode.getRuleFlowGroup();
        if (ruleFlowGroup != null) {
            xmlDump.append("ruleFlowGroup=\"" + ruleFlowGroup + "\" ");
        }
        endElement();
    }
    
    private void visitSubProcessNode(SubProcessNode subProcessNode, boolean includeMeta) {
        visitNode("subProcess", subProcessNode, includeMeta);
        String processId = subProcessNode.getProcessId();
        if (processId != null) {
            xmlDump.append("processId=\"" + processId + "\" ");
        }
        if (!subProcessNode.isWaitForCompletion()) {
            xmlDump.append("waitForCompletion=\"false\" ");
        }
        endElement();
    }
    
    private void visitJoinNode(Join joinNode, boolean includeMeta) {
        visitNode("join", joinNode, includeMeta);
        int type = joinNode.getType();
        if (type != 0) {
            xmlDump.append("type=\"" + type + "\" ");
        }
        endElement();
    }
    
    private void visitSplitNode(Split splitNode, boolean includeMeta) {
        visitNode("split", splitNode, includeMeta);
        int type = splitNode.getType();
        if (type != 0) {
            xmlDump.append("type=\"" + type + "\" ");
        }
        if (splitNode.getConstraints().isEmpty()) {
            endElement();
        } else {
            xmlDump.append(">" + EOL);
            xmlDump.append("      <constraints>" + EOL);
            for (Map.Entry<Split.ConnectionRef, Constraint> entry: splitNode.getConstraints().entrySet()) {
                Split.ConnectionRef connection = entry.getKey();
                Constraint constraint = entry.getValue();
                xmlDump.append("        <constraint "
                        + "toNodeId=\"" + connection.getNodeId() + "\" "
                        + "toType=\"" + connection.getToType() + "\" ");
                String name = constraint.getName();
                if (name != null && !"".equals(name)) {
                    xmlDump.append("name=\"" + constraint.getName() + "\" ");
                }
                int priority = constraint.getPriority();
                if (priority != 0) {
                    xmlDump.append("priority=\"" + constraint.getPriority() + "\" ");
                }
                xmlDump.append("type=\"" + constraint.getType() + "\" ");
                String dialect = constraint.getDialect();
                if (dialect != null && !"".equals(dialect)) {
                    xmlDump.append("dialect=\"" + dialect + "\" ");
                }
                String constraintString = constraint.getConstraint();
                if (constraintString != null) {
                    xmlDump.append(">" + XmlDumper.replaceIllegalChars(constraintString) + "</constraint>" + EOL);
                } else {
                    xmlDump.append("/>" + EOL);
                }
            }
            xmlDump.append("      </constraints>" + EOL);
            endElement("split");
        }
    }
    
    private void visitMileStoneNode(MilestoneNode milestoneNode, boolean includeMeta) {
        visitNode("milestone", milestoneNode, includeMeta);
        String constraint = milestoneNode.getConstraint();
        if (constraint != null) {
            xmlDump.append(">" + XmlDumper.replaceIllegalChars(constraint.trim()) + "</milestone>" + EOL);
        } else {
            endElement();
        }
    }
    
    private void visitWorkItemNode(WorkItemNode workItemNode, boolean includeMeta) {
        visitNode("workItem", workItemNode, includeMeta);
        if (!workItemNode.isWaitForCompletion()) {
            xmlDump.append("waitForCompletion=\"false\" ");
        }
        xmlDump.append(">" + EOL);
        Work work = workItemNode.getWork();
        if (work != null) {
            visitWork(work, includeMeta);
        }
        Map<String, String> inMappings = workItemNode.getInMappings();
        for (Map.Entry<String, String> inMapping: inMappings.entrySet()) {
            xmlDump.append(
                "      <mapping type=\"in\" "
                             + "parameterName=\"" + inMapping.getKey() + "\" "
                             + "variableName=\"" + inMapping.getValue() + "\" />" + EOL);
        }
        Map<String, String> outMappings = workItemNode.getOutMappings();
        for (Map.Entry<String, String> outMapping: outMappings.entrySet()) {
            xmlDump.append(
                "      <mapping type=\"out\" "
                             + "parameterName=\"" + outMapping.getKey() + "\" "
                             + "variableName=\"" + outMapping.getValue() + "\" />" + EOL);
        }
        endElement("workItem");
    }
    
    private void visitWork(Work work, boolean includeMeta) {
        xmlDump.append("      <work name=\"" + work.getName() + "\" >" + EOL);
        for (ParameterDefinition paramDefinition: work.getParameterDefinitions()) {
            if (paramDefinition == null) {
                throw new IllegalArgumentException(
                    "Could not find parameter definition " + paramDefinition.getName()
                        + " for work " + work.getName());
            }
            xmlDump.append("        <parameter name=\"" + paramDefinition.getName() + "\" " + 
                                              "type=\"" + paramDefinition.getType().getClass().getName() + "\" ");
            Object value = work.getParameter(paramDefinition.getName());
            if (value == null) {
                xmlDump.append("/>" + EOL);
            } else {
                xmlDump.append(">" + value + "</parameter>" + EOL);
            }
        }
        xmlDump.append("      </work>" + EOL);
    }
    
    private void visitTimerNode(TimerNode timerNode, boolean includeMeta) {
        visitNode("timer", timerNode, includeMeta);
        Timer timer = timerNode.getTimer();
        if (timer != null) {
            xmlDump.append("delay=\"" + timer.getDelay() + "\" ");
            if (timer.getPeriod() > 0) {
                xmlDump.append(" period=\"" + timer.getPeriod() + "\" ");
            }
        }
        endElement();
    }
    
    private void visitConnections(RuleFlowProcess process, boolean includeMeta) {
        List<Connection> connections = new ArrayList<Connection>();
        for (Node node: process.getNodes()) {
            connections.addAll(node.getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE));
        }
        xmlDump.append("  <connections>" + EOL);
        for (Connection connection: connections) {
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
        xmlDump.append("  </connections>" + EOL + EOL);
    }
    
}
