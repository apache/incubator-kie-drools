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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.xml.XmlDumper;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.drools.core.xml.Handler;
import org.drools.core.xml.SemanticModule;
import org.drools.core.xml.SemanticModules;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.DataStore;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.swimlane.Swimlane;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.impl.ProcessImpl;
import org.jbpm.process.core.impl.XmlProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventTrigger;
import org.jbpm.workflow.core.node.FaultNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.Trigger;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlBPMNProcessDumper implements XmlProcessDumper {

	public static final String JAVA_LANGUAGE = "http://www.java.com/java";
	public static final String MVEL_LANGUAGE = "http://www.mvel.org/2.0";
	public static final String RULE_LANGUAGE = "http://www.jboss.org/drools/rule";
    public static final String XPATH_LANGUAGE = "http://www.w3.org/1999/XPath";
    public static final String JAVASCRIPT_LANGUAGE = "http://www.javascript.com/javascript";

    public static final int NO_META_DATA = 0;
    public static final int META_DATA_AS_NODE_PROPERTY = 1;
    public static final int META_DATA_USING_DI = 2;

	public static final XmlBPMNProcessDumper INSTANCE = new XmlBPMNProcessDumper();

    private static final Logger logger = LoggerFactory.getLogger(XmlBPMNProcessDumper.class);

    private final static String EOL = System.getProperty( "line.separator" );

    private SemanticModule semanticModule;
    private int metaDataType = META_DATA_USING_DI;

    private XmlBPMNProcessDumper() {
    	semanticModule = new BPMNSemanticModule();
    }

    public String dump(WorkflowProcess process) {
        return dump(process, META_DATA_USING_DI);
    }

    public String dump(WorkflowProcess process, boolean includeMeta) {
    	return dump(process, META_DATA_AS_NODE_PROPERTY);
    }

    public String dump(WorkflowProcess process, int metaDataType) {
        StringBuilder xmlDump = new StringBuilder();
        visitProcess(process, xmlDump, metaDataType);
        return xmlDump.toString();
    }

    public int getMetaDataType() {
		return metaDataType;
	}

	public void setMetaDataType(int metaDataType) {
		this.metaDataType = metaDataType;
	}

	private Set<String> visitedVariables;

	protected void visitProcess(WorkflowProcess process, StringBuilder xmlDump, int metaDataType) {
        String targetNamespace = (String) process.getMetaData().get("TargetNamespace");
        if (targetNamespace == null) {
        	targetNamespace = "http://www.jboss.org/drools";
        }
    	xmlDump.append(
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + EOL +
            "<definitions id=\"Definition\"" + EOL +
            "             targetNamespace=\"" + targetNamespace + "\"" + EOL +
            "             typeLanguage=\"http://www.java.com/javaTypes\"" + EOL +
            "             expressionLanguage=\"http://www.mvel.org/2.0\"" + EOL +
            "             xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"" + EOL +
            "             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + EOL +
            "             xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\"" + EOL +
            "             xmlns:g=\"http://www.jboss.org/drools/flow/gpd\"" + EOL +
            (metaDataType == META_DATA_USING_DI ? 
                "             xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"" + EOL +
            	"             xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"" + EOL +
        		"             xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"" + EOL : "") +
            "             xmlns:tns=\"http://www.jboss.org/drools\">" + EOL + EOL);

    	// item definitions
    	this.visitedVariables = new HashSet<String>();
    	VariableScope variableScope = (VariableScope)
    		((org.jbpm.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);
    	Set<String> dumpedItemDefs = new HashSet<String>();
    	Map<String, ItemDefinition> itemDefs = (Map<String, ItemDefinition>) process.getMetaData().get("ItemDefinitions");

    	if (itemDefs != null) {
    		for (ItemDefinition def : itemDefs.values()) {
    			xmlDump.append(
                        "  <itemDefinition id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(def.getId()) + "\" ");
                    if (def.getStructureRef() != null && !"java.lang.Object".equals(def.getStructureRef())) {
                        xmlDump.append("structureRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(def.getStructureRef()) + "\" ");
                    }
                    xmlDump.append("/>" + EOL);
                    dumpedItemDefs.add(def.getId().intern());
    		}
    	}

    	visitVariableScope(variableScope, "_", xmlDump, dumpedItemDefs);
    	visitSubVariableScopes(process.getNodes(), xmlDump, dumpedItemDefs);

	    visitInterfaces(process.getNodes(), xmlDump);

	    visitEscalations(process.getNodes(), xmlDump, new ArrayList<String>());
    	Definitions def = (Definitions) process.getMetaData().get("Definitions");
	    visitErrors(def, xmlDump);

	    //data stores
    	if (def != null && def.getDataStores() != null) {
    		for (DataStore dataStore : def.getDataStores()) {
    			visitDataStore(dataStore, xmlDump);
    		}
    	}

	    // the process itself
		xmlDump.append("  <process processType=\"Private\" isExecutable=\"true\" ");
        if (process.getId() == null || process.getId().trim().length() == 0) {
        	((ProcessImpl) process).setId("com.sample.bpmn2");
        }
        xmlDump.append("id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(process.getId()) + "\" ");
        if (process.getName() != null) {
            xmlDump.append("name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(process.getName()) + "\" ");
        }
        String packageName = process.getPackageName();
        if (packageName != null && !"org.drools.bpmn2".equals(packageName)) {
            xmlDump.append("tns:packageName=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(packageName) + "\" ");
        }
        if (((org.jbpm.workflow.core.WorkflowProcess) process).isDynamic()) {
        	xmlDump.append("tns:adHoc=\"true\" ");
        }
        String version = process.getVersion();
        if (version != null && !"".equals(version)) {
            xmlDump.append("tns:version=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(version) + "\" ");
        }
        // TODO: package, version
        xmlDump.append(">" + EOL + EOL);
        visitHeader(process, xmlDump, metaDataType);
        
        List<org.jbpm.workflow.core.Node> processNodes = new ArrayList<org.jbpm.workflow.core.Node>();
        for( Node procNode : process.getNodes()) { 
            processNodes.add((org.jbpm.workflow.core.Node) procNode);
        }
        visitNodes(processNodes, xmlDump, metaDataType);
        visitConnections(process.getNodes(), xmlDump, metaDataType);
        // add associations
        List<Association> associations = (List<Association>) process.getMetaData().get(ProcessHandler.ASSOCIATIONS);
        if( associations != null ) {
            for (Association association : associations ) {
                visitAssociation(association, xmlDump);
            }
        }

        xmlDump.append("  </process>" + EOL + EOL);
        if (metaDataType == META_DATA_USING_DI) {
        	xmlDump.append(
    			"  <bpmndi:BPMNDiagram>" + EOL +
    			"    <bpmndi:BPMNPlane bpmnElement=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(process.getId()) + "\" >" + EOL);
        	visitNodesDi(process.getNodes(), xmlDump);
        	visitConnectionsDi(process.getNodes(), xmlDump);
        	xmlDump.append(
    			"    </bpmndi:BPMNPlane>" + EOL +
        		"  </bpmndi:BPMNDiagram>" + EOL + EOL);
        }
        xmlDump.append("</definitions>");
    }

    private void visitDataStore(DataStore dataStore, StringBuilder xmlDump) {
    	String itemSubjectRef = dataStore.getItemSubjectRef();
    	String itemDefId = itemSubjectRef.substring(itemSubjectRef.indexOf(':') + 1);
    	xmlDump.append("  <itemDefinition id=\"" + itemDefId + "\" ");
    	if (dataStore.getType() != null && !"java.lang.Object".equals(dataStore.getType().getStringType())) {
    		xmlDump.append("structureRef=\"" + XmlDumper.replaceIllegalChars(dataStore.getType().getStringType()) + "\" ");
    	}
    	xmlDump.append("/>" + EOL);

    	xmlDump.append("  <dataStore name=\"" + XmlDumper.replaceIllegalChars(dataStore.getName()) + "\"");
    	xmlDump.append(" id=\"" + XmlDumper.replaceIllegalChars(dataStore.getId()) + "\"");
    	xmlDump.append(" itemSubjectRef=\"" + XmlDumper.replaceIllegalChars(dataStore.getItemSubjectRef()) + "\"");
    	xmlDump.append("/>" + EOL);
	}

    public void visitAssociation(Association association, StringBuilder xmlDump) {
    	xmlDump.append("    <association id=\"" + association.getId() + "\" ");
    	xmlDump.append(" sourceRef=\"" + association.getSourceRef() + "\" ");
    	xmlDump.append(" targetRef=\"" + association.getTargetRef() + "\" ");
    	xmlDump.append("/>" + EOL);
	}

    private void visitVariableScope(VariableScope variableScope, String prefix, StringBuilder xmlDump, Set<String> dumpedItemDefs) {
        if (variableScope != null && !variableScope.getVariables().isEmpty()) {
            int variablesAdded = 0;
            for (Variable variable: variableScope.getVariables()) {
                String itemDefId = (String) variable.getMetaData("ItemSubjectRef");
                if( itemDefId == null ) { 
                    itemDefId = prefix + variable.getName();
                }
                if( itemDefId != null && ! dumpedItemDefs.add(itemDefId.intern()) ) {
                   continue; 
                }
                if( ! visitedVariables.add(variable.getName()) ) { 
                    continue;
                }
                ++variablesAdded;
                xmlDump.append(
                    "  <itemDefinition id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(itemDefId) + "\" ");
                if (variable.getType() != null && !"java.lang.Object".equals(variable.getType().getStringType())) {
                    xmlDump.append("structureRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(variable.getType().getStringType()) + "\" ");
                }
                xmlDump.append("/>" + EOL);
            }
            if( variablesAdded > 0 ) { 
                xmlDump.append(EOL);
            }
        }
    }

    private void visitSubVariableScopes(Node[] nodes, StringBuilder xmlDump, Set<String> dumpedItemDefs) {
        for (Node node: nodes) {
            if (node instanceof ContextContainer) {
                VariableScope variableScope = (VariableScope) 
                    ((ContextContainer) node).getDefaultContext(VariableScope.VARIABLE_SCOPE);
                if (variableScope != null) {
                    visitVariableScope(variableScope, XmlBPMNProcessDumper.getUniqueNodeId(node) + "-", xmlDump, dumpedItemDefs);
                }
            }
            if (node instanceof NodeContainer) {
                visitSubVariableScopes(((NodeContainer) node).getNodes(), xmlDump, dumpedItemDefs);
            }
        }
    }

    private void visitLanes(WorkflowProcess process, StringBuilder xmlDump) {
        // lanes
        Collection<Swimlane> swimlanes = ((SwimlaneContext)
            ((org.jbpm.workflow.core.WorkflowProcess) process)
                .getDefaultContext(SwimlaneContext.SWIMLANE_SCOPE)).getSwimlanes();
        if (!swimlanes.isEmpty()) {
            xmlDump.append("    <laneSet>" + EOL);
            for (Swimlane swimlane: swimlanes) {
                xmlDump.append("      <lane name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(swimlane.getName()) + "\" >" + EOL);
                visitLane(process, swimlane.getName(), xmlDump);
                xmlDump.append("      </lane>" + EOL);
            }
            xmlDump.append("    </laneSet>" + EOL);
        }
    }

    private void visitLane(NodeContainer container, String lane, StringBuilder xmlDump) {
        for (Node node: container.getNodes()) {
            if (node instanceof HumanTaskNode) {
                String swimlane = ((HumanTaskNode) node).getSwimlane();
                if (lane.equals(swimlane)) {
                    xmlDump.append("        <flowNodeRef>" + XmlBPMNProcessDumper.getUniqueNodeId(node) + "</flowNodeRef>" + EOL);
                }
            } else {
                String swimlane = (String) node.getMetaData().get("Lane");
                if (lane.equals(swimlane)) {
                    xmlDump.append("        <flowNodeRef>" + XmlBPMNProcessDumper.getUniqueNodeId(node) + "</flowNodeRef>" + EOL);
                }
            }
            if (node instanceof NodeContainer) {
                visitLane((NodeContainer) node, lane, xmlDump);
            }
        }
    }

    protected void visitHeader(WorkflowProcess process, StringBuilder xmlDump, int metaDataType) {
        Map<String, Object> metaData = getMetaData(process.getMetaData());
    	Set<String> imports = ((org.jbpm.process.core.Process) process).getImports();
    	Map<String, String> globals = ((org.jbpm.process.core.Process) process).getGlobals();
    	if ((imports != null && !imports.isEmpty()) || (globals != null && globals.size() > 0) || !metaData.isEmpty()) {
    		xmlDump.append("    <extensionElements>" + EOL);
    		if (imports != null) {
	    		for (String s: imports) {
	    			xmlDump.append("     <tns:import name=\"" + s + "\" />" + EOL);
	    		}
    		}
    		if (globals != null) {
	    		for (Map.Entry<String, String> global: globals.entrySet()) {
	    			xmlDump.append("     <tns:global identifier=\"" + global.getKey() + "\" type=\"" + global.getValue() + "\" />" + EOL);
	    		}
    		}
    		writeMetaData(getMetaData(process.getMetaData()), xmlDump);
    		xmlDump.append("    </extensionElements>" + EOL);
    	}
    	// TODO: function imports
    	// TODO: exception handlers
        VariableScope variableScope = (VariableScope)
        	((org.jbpm.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        if (variableScope != null) {
            visitVariables(variableScope.getVariables(), xmlDump);
        }
        visitLanes(process, xmlDump);
    }

    public static void visitVariables(List<Variable> variables, StringBuilder xmlDump) {
    	if (!variables.isEmpty()) {
            xmlDump.append("    <!-- process variables -->" + EOL);
            for (Variable variable: variables) {
                if (variable.getMetaData("DataObject") == null) {
                    xmlDump.append("    <property id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(variable.getName()) + "\" ");
                    if (variable.getType() != null) {
                    	xmlDump.append("itemSubjectRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute((String) variable.getMetaData("ItemSubjectRef")) + "\"" );
                    }
                    // TODO: value?
                    Map<String, Object> metaData = getMetaData(variable.getMetaData());
                    if (metaData.isEmpty()) {
                    	xmlDump.append("/>" + EOL);
                    } else {
                    	xmlDump.append(">" + EOL
                    			+ "      <extensionElements>" + EOL);
                    	writeMetaData(metaData, xmlDump);
                		xmlDump.append("      </extensionElements>" + EOL
                				+ "    </property>" + EOL);
                    }
                }
            }
            for (Variable variable: variables) {
                if (variable.getMetaData("DataObject") != null) {
                    xmlDump.append("    <dataObject id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(variable.getName()) + "\" ");
                    if (variable.getType() != null) {
                        xmlDump.append("itemSubjectRef=\"_" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(variable.getName()) + "\"" );
                    }
                    // TODO: value?
                    Map<String, Object> metaData = getMetaData(variable.getMetaData());
                    if (metaData.isEmpty()) {
                    	xmlDump.append("/>" + EOL);
                    } else {
                    	xmlDump.append(">" + EOL
                    			+ "      <extensionElements>" + EOL);
                    	writeMetaData(metaData, xmlDump);
                		xmlDump.append("      </extensionElements>" + EOL
                				+ "    </property>" + EOL);
                    }
                }
            }
            xmlDump.append(EOL);
    	}
    }

    public static Map<String, Object> getMetaData(Map<String, Object> input) {
    	Map<String, Object> metaData = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry: input.entrySet()) {
        	String name = entry.getKey();
        	if (entry.getKey().startsWith("custom") 
        			&& entry.getValue() instanceof String) {
        		metaData.put(name, entry.getValue());
        	}
        }
        return metaData;
    }

    public static void writeMetaData(Map<String, Object> metaData, final StringBuilder xmlDump) {
        if (!metaData.isEmpty()) {
            for (Map.Entry<String, Object> entry: metaData.entrySet()) {
	    		xmlDump.append("        <tns:metaData name=\"" + entry.getKey() + "\">" + EOL);
	    		xmlDump.append("          <tns:metaValue>" + entry.getValue() + "</tns:metaValue>" + EOL);
	    		xmlDump.append("        </tns:metaData>" + EOL);
            }
        }
    }

    protected void visitInterfaces(Node[] nodes, StringBuilder xmlDump) {
        for (Node node: nodes) {
            if (node instanceof WorkItemNode) {
                Work work = ((WorkItemNode) node).getWork();
                if (work != null) {
                    if ("Service Task".equals(work.getName())) {
                        String interfaceName = (String) work.getParameter("Interface");
                        if (interfaceName == null) {
                            interfaceName = "";
                        }
                        String interfaceRef = (String) work.getParameter("interfaceImplementationRef");
                        if (interfaceRef == null) {
                            interfaceRef = "";
                        }
                        String operationName = (String) work.getParameter("Operation");
                        if (operationName == null) {
                            operationName = "";
                        }
                        String operationRef = (String) work.getParameter("operationImplementationRef");
                        if (operationRef == null) {
                            operationRef = "";
                        }
                        String parameterType = (String) work.getParameter("ParameterType");
                        if (parameterType == null) {
                            parameterType = "";
                        }
                        xmlDump.append(
                            "  <itemDefinition id=\"" + getUniqueNodeId(node) + "_InMessageType\" " + 
                            	("".equals(parameterType) || "java.lang.Object".equals(parameterType) ?
                        			"" : "structureRef=\"" + parameterType + "\" ")
                        			+ "/>" + EOL +
                            "  <message id=\"" + getUniqueNodeId(node) + "_InMessage\" itemRef=\"" + getUniqueNodeId(node) + "_InMessageType\" />" + EOL +
                            "  <interface id=\"" + getUniqueNodeId(node) + "_ServiceInterface\" name=\"" + interfaceName + "\" implementationRef=\""+ interfaceRef+"\" >" + EOL +
                            "    <operation id=\"" + getUniqueNodeId(node) + "_ServiceOperation\" name=\"" + operationName + "\" implementationRef=\""+ operationRef+"\" >" + EOL + 
                            "      <inMessageRef>" + getUniqueNodeId(node) + "_InMessage</inMessageRef>" + EOL +
                            "    </operation>" + EOL +
                            "  </interface>" + EOL + EOL);
                    } else if ("Send Task".equals(work.getName())) {
                        String messageType = (String) work.getParameter("MessageType");
                        if (messageType == null) {
                            messageType = "";
                        }
                        xmlDump.append(
                            "  <itemDefinition id=\"" + getUniqueNodeId(node) + "_MessageType\" " +
                            	("".equals(messageType) || "java.lang.Object".equals(messageType) ?
                        			"" : "structureRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(messageType) + "\" ")
                        			+ "/>" + EOL +
                            "  <message id=\"" + getUniqueNodeId(node) + "_Message\" itemRef=\"" + getUniqueNodeId(node) + "_MessageType\" />" + EOL + EOL);
                    } else if ("Receive Task".equals(work.getName())) {
                    	String messageId = (String) work.getParameter("MessageId");
                        String messageType = (String) work.getParameter("MessageType");
                        if (messageType == null) {
                            messageType = "";
                        }
                        xmlDump.append(
                            "  <itemDefinition id=\"" + getUniqueNodeId(node) + "_MessageType\" " + 
                            	("".equals(messageType) || "java.lang.Object".equals(messageType) ?
                        			"" : "structureRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(messageType) + "\" ")
                        			+ "/>" + EOL +
                            "  <message id=\"" + messageId + "\" itemRef=\"" + getUniqueNodeId(node) + "_MessageType\" />" + EOL + EOL);
                    }
                }
            } else if (node instanceof EndNode) {
                String messageType = (String) node.getMetaData().get("MessageType");
                if (messageType != null) {
                    xmlDump.append(
                        "  <itemDefinition id=\"" + getUniqueNodeId(node) + "_MessageType\" " + 
                        	("".equals(messageType) || "java.lang.Object".equals(messageType) ?
                    			"" : "structureRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(messageType) + "\" ")
                    			+ "/>" + EOL +
                        "  <message id=\"" + getUniqueNodeId(node) + "_Message\" itemRef=\"" + getUniqueNodeId(node) + "_MessageType\" />" + EOL + EOL);
                }
            } else if (node instanceof ActionNode) {
                String messageType = (String) node.getMetaData().get("MessageType");
                if (messageType != null) {
                    xmlDump.append(
                        "  <itemDefinition id=\"" + getUniqueNodeId(node) + "_MessageType\" " + 
                        	("".equals(messageType) || "java.lang.Object".equals(messageType) ?
                    			"" : "structureRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(messageType) + "\" ") +
                    			"/>" + EOL +
                        "  <message id=\"" + getUniqueNodeId(node) + "_Message\" itemRef=\"" + getUniqueNodeId(node) + "_MessageType\" />" + EOL + EOL);
                }
            } else if (node instanceof EventNode) {
            	List<EventFilter> filters = ((EventNode) node).getEventFilters();
            	if (filters.size() > 0) {
	                String messageRef = ((EventTypeFilter) filters.get(0)).getType();
	                if (messageRef.startsWith("Message-")) {
		                messageRef = messageRef.substring(8);
		                String messageType = (String) node.getMetaData().get("MessageType");
		                xmlDump.append(
		                    "  <itemDefinition id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(messageRef) + "Type\" " + 
		                    	("".equals(messageType) || "java.lang.Object".equals(messageType) ?
                        			"" : "structureRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(messageType) + "\" ") + 
                        			"/>" + EOL +
		                    "  <message id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(messageRef) + "\" itemRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(messageRef) + "Type\" />" + EOL + EOL);
	                }
            	}
            } else if (node instanceof StartNode) {
                StartNode startNode = (StartNode) node;
                if (startNode.getTriggers() != null && !startNode.getTriggers().isEmpty()) {
                    Trigger trigger = startNode.getTriggers().get(0);
                    if (trigger instanceof EventTrigger) {
                        String eventType = ((EventTypeFilter) ((EventTrigger) trigger).getEventFilters().get(0)).getType();
                        if (eventType.startsWith("Message-")) {
                            eventType = eventType.substring(8);
                            String messageType = (String) node.getMetaData().get("MessageType");
                            xmlDump.append(
                                "  <itemDefinition id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(eventType) + "Type\" " + 
                                	("".equals(messageType) || "java.lang.Object".equals(messageType) ?
                            			"" : "structureRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(messageType) + "\" ") +
                            			"/>" + EOL +
                                "  <message id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(eventType) + "\" itemRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(eventType) + "Type\" />" + EOL + EOL);
                        }
                    }
                }
            } else if (node instanceof ForEachNode) {
            	ForEachNode forEachNode = (ForEachNode) node;
            	String type = null;
            	if (forEachNode.getVariableType() instanceof ObjectDataType) {
            		type = ((ObjectDataType) forEachNode.getVariableType()).getClassName(); 
            	}
                xmlDump.append(
                    "  <itemDefinition id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(forEachNode) + "_multiInstanceItemType\" " + 
                    	(type == null || "java.lang.Object".equals(type) ? "" : "structureRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\" ") + "/>" + EOL + EOL);
            }
            if (node instanceof CompositeNode) {
            	visitInterfaces(((CompositeNode) node).getNodes(), xmlDump);
            }
        }
    }

    protected void visitEscalations(Node[] nodes, StringBuilder xmlDump, List<String> escalations) {
        for (Node node: nodes) {
            if (node instanceof FaultNode) {
            	FaultNode faultNode = (FaultNode) node;
            	if (!faultNode.isTerminateParent()) {
            		String escalationCode = faultNode.getFaultName();
            		if (!escalations.contains(escalationCode)) {
            			escalations.add(escalationCode);
	                    xmlDump.append(
	                        "  <escalation id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(escalationCode) + "\" escalationCode=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(escalationCode) + "\" />" + EOL);
            		}
                }
            } else if (node instanceof ActionNode) {
            	ActionNode actionNode = (ActionNode) node;
            	if(actionNode.getAction() instanceof DroolsConsequenceAction) {
                    DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
                    if (action != null) {
                        String s = action.getConsequence();
                        if (s.startsWith("org.drools.core.process.instance.context.exception.ExceptionScopeInstance scopeInstance = (org.drools.core.process.instance.context.exception.ExceptionScopeInstance) ((org.drools.workflow.instance.NodeInstance) kcontext.getNodeInstance()).resolveContextInstance(org.drools.core.process.core.context.exception.ExceptionScope.EXCEPTION_SCOPE, \"")) {
                            s = s.substring(327);
                            String type = s.substring(0, s.indexOf("\""));
                            if (!escalations.contains(type)) {
                                escalations.add(type);
                                xmlDump.append(
                                        "  <escalation id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\" escalationCode=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\" />" + EOL);
                            }
                        }
                    }
                } else {
            	    logger.warn("Cannot serialize custom implementation of the Action interface to XML");
                }
            } else if (node instanceof EventNode) {
            	EventNode eventNode = (EventNode) node;
            	String type = (String) eventNode.getMetaData("EscalationEvent");
            	if (type != null) {
            		if (!escalations.contains(type)) {
            			escalations.add(type);
		                xmlDump.append(
		                    "  <escalation id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\" escalationCode=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\" />" + EOL);
            		}
            	}
            }
            if (node instanceof CompositeNode) {
            	visitEscalations(((CompositeNode) node).getNodes(), xmlDump, escalations);
            }
        }
    }

    protected void visitErrors(Definitions definitions, StringBuilder xmlDump) { 
        if( definitions == null ) { 
            return;
        }
        List<Error> errors = definitions.getErrors();
        if( errors == null || errors.isEmpty() ) { 
            return;
        }
        for( org.jbpm.bpmn2.core.Error error : errors ) { 
            String id = XmlBPMNProcessDumper.replaceIllegalCharsAttribute(error.getId());
            String code = error.getErrorCode();
            xmlDump.append("  <error id=\"" + id + "\"" );
            if (error.getErrorCode() != null) {
            	code = XmlBPMNProcessDumper.replaceIllegalCharsAttribute(code);
            	xmlDump.append(" errorCode=\"" + code + "\"" );
            }
            String structureRef = error.getStructureRef();
            if( structureRef != null ) { 
                structureRef = XmlBPMNProcessDumper.replaceIllegalCharsAttribute(structureRef);
                xmlDump.append(" structureRef=\"" + structureRef + "\"");
            }
            xmlDump.append("/>" + EOL );
        }
    }

    public void visitNodes(List<org.jbpm.workflow.core.Node> nodes, StringBuilder xmlDump, int metaDataType) {
    	xmlDump.append("    <!-- nodes -->" + EOL);
        for (Node node: nodes) {
            visitNode(node, xmlDump, metaDataType);
        }
        xmlDump.append(EOL);
    }

    private void visitNode(Node node, StringBuilder xmlDump, int metaDataType) {
     	Handler handler = semanticModule.getHandlerByClass(node.getClass());
        if (handler != null) {
        	((AbstractNodeHandler) handler).writeNode((org.jbpm.workflow.core.Node) node, xmlDump, metaDataType);
        } else {
        	throw new IllegalArgumentException(
                "Unknown node type: " + node);
        }
    }

    private void visitNodesDi(Node[] nodes, StringBuilder xmlDump) {
    	for (Node node: nodes) {
            Integer x = (Integer) node.getMetaData().get("x");
            Integer y = (Integer) node.getMetaData().get("y");
            Integer width = (Integer) node.getMetaData().get("width");
            Integer height = (Integer) node.getMetaData().get("height");
    		if (x == null) {
    			x = 0;
    		}
    		if (y == null) {
    			y = 0;
    		}
    		if (width == null) {
    			width = 48;
    		}
    		if (height == null) {
    			height = 48;
    		}
    		if (node instanceof StartNode || node instanceof EndNode || node instanceof EventNode || node instanceof FaultNode) {
    			int offsetX = (int) ((width - 48) / 2);
    			width = 48;
    	        x = x + offsetX;
    	        int offsetY = (int) ((height - 48) / 2);
    	        y = y + offsetY;
    	        height = 48;
    		} else if (node instanceof Join || node instanceof Split) {
    			int offsetX = (int) ((width - 48) / 2);
    			width = 48;
    	        x = x + offsetX;
    	        int offsetY = (int) ((height - 48) / 2);
    	        y = y + offsetY;
    	        height = 48;
    		}
    		int parentOffsetX = 0;
    		int parentOffsetY = 0;
    		NodeContainer nodeContainer = node.getNodeContainer();
    		while (nodeContainer instanceof CompositeNode) {
    			CompositeNode parent = (CompositeNode) nodeContainer;
    			Integer parentX = (Integer) parent.getMetaData().get("x");
    			if (parentX != null) {
    				parentOffsetX += parentX;
    			}
    			Integer parentY = (Integer) parent.getMetaData().get("y");
    			if (parentY != null) {
    				parentOffsetY += (Integer) parent.getMetaData().get("y");
    			}
    			nodeContainer = parent.getNodeContainer();
    		}
    		x += parentOffsetX;
    		y += parentOffsetY;
			xmlDump.append(
				"      <bpmndi:BPMNShape bpmnElement=\"" + getUniqueNodeId(node) + "\" >" + EOL +
				"        <dc:Bounds x=\"" + x + "\" " + "y=\"" + y + "\" " + 
								   "width=\"" + width + "\" " + "height=\"" + height + "\" />" + EOL +
			    "      </bpmndi:BPMNShape>" + EOL);
			if (node instanceof CompositeNode) {
				visitNodesDi(((CompositeNode) node).getNodes(), xmlDump);
			}
    	}

    }

    private void visitConnections(Node[] nodes, StringBuilder xmlDump, int metaDataType) {
    	xmlDump.append("    <!-- connections -->" + EOL);
        List<Connection> connections = new ArrayList<Connection>();
        for (Node node: nodes) {
            for (List<Connection> connectionList: node.getIncomingConnections().values()) {
                connections.addAll(connectionList);
            }
        }
        for (Connection connection: connections) {
            visitConnection(connection, xmlDump, metaDataType);
        }
        xmlDump.append(EOL);
    }

    private boolean isConnectionRepresentingLinkEvent(Connection connection) {
        boolean bValue = connection.getMetaData().get("linkNodeHidden") != null;
        return bValue;
    }

    public void visitConnection(Connection connection, StringBuilder xmlDump, int metaDataType) {
    	// if the connection was generated by a link event, don't dump.
        if (isConnectionRepresentingLinkEvent(connection)) {
        	return;
        }
        // if the connection is a hidden one (compensations), don't dump
        Object hidden = ((ConnectionImpl) connection).getMetaData("hidden");
        if( hidden != null && ((Boolean) hidden) ) { 
           return; 
        }

        xmlDump.append("    <sequenceFlow id=\"" +
    		getUniqueNodeId(connection.getFrom()) + "-" + 
    		getUniqueNodeId(connection.getTo()) + 
    		"\" sourceRef=\"" + getUniqueNodeId(connection.getFrom()) + "\" ");
        // TODO fromType, toType
        xmlDump.append("targetRef=\"" + getUniqueNodeId(connection.getTo()) + "\" ");
        if (metaDataType == META_DATA_AS_NODE_PROPERTY) {
            String bendpoints = (String) connection.getMetaData().get("bendpoints");
            if (bendpoints != null) {
                xmlDump.append("g:bendpoints=\"" + bendpoints + "\" ");
            }
        }
        if (connection.getFrom() instanceof Split) {
        	Split split = (Split) connection.getFrom();
        	if (split.getType() == Split.TYPE_XOR || split.getType() == Split.TYPE_OR) {
        		Constraint constraint = split.getConstraint(connection);
        		if (constraint == null) {
            		xmlDump.append(">" + EOL +
    					"      <conditionExpression xsi:type=\"tFormalExpression\" />");
        		} else {
                    if (constraint.getName() != null && constraint.getName().trim().length() > 0) {
            			xmlDump.append("name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(constraint.getName()) + "\" ");
            		}
                    if (constraint.getPriority() != 0) {
                    	xmlDump.append("tns:priority=\"" + constraint.getPriority() + "\" ");
                    }
            		xmlDump.append(">" + EOL +
    				"      <conditionExpression xsi:type=\"tFormalExpression\" ");
                    if ("code".equals(constraint.getType())) {
                        if (JavaDialect.ID.equals(constraint.getDialect())) {
                            xmlDump.append("language=\"" + JAVA_LANGUAGE + "\" ");
                        } else if ("XPath".equals(constraint.getDialect())) {
                            xmlDump.append("language=\"" + XPATH_LANGUAGE + "\" ");
                        }  else if ("JavaScript".equals(constraint.getDialect())) {
                            xmlDump.append("language=\"" + JAVASCRIPT_LANGUAGE + "\" ");
                        }
                    } else {
                        xmlDump.append("language=\"" + RULE_LANGUAGE + "\" ");
                    }
                    String constraintString = constraint.getConstraint();
                    if (constraintString == null) {
                        constraintString = "";
                    }
                    xmlDump.append(">" + XmlDumper.replaceIllegalChars(constraintString) + "</conditionExpression>");
        		}
        		xmlDump.append(EOL
    		        + "    </sequenceFlow>" + EOL);
        	} else {
            	xmlDump.append("/>" + EOL);
            }
        } else {
        	xmlDump.append("/>" + EOL);
        }
    }

    private void visitConnectionsDi(Node[] nodes, StringBuilder xmlDump) {
        List<Connection> connections = new ArrayList<Connection>();
        for (Node node: nodes) {
            for (List<Connection> connectionList: node.getIncomingConnections().values()) {
                connections.addAll(connectionList);
            }
            if (node instanceof CompositeNode) {
            	visitConnectionsDi(((CompositeNode) node).getNodes(), xmlDump);
            }
        }
        for (Connection connection: connections) {
            String bendpoints = (String) connection.getMetaData().get("bendpoints");
            xmlDump.append(
        		"      <bpmndi:BPMNEdge bpmnElement=\"" + 
        			getUniqueNodeId(connection.getFrom()) + "-" + getUniqueNodeId(connection.getTo()) + "\" >" + EOL);
        	Integer x = (Integer) connection.getFrom().getMetaData().get("x");
        	if (x == null) {
        		x = 0;
        	}
        	Integer y = (Integer) connection.getFrom().getMetaData().get("y");
        	if (y == null) {
        		y = 0;
        	}
        	Integer width = (Integer) connection.getFrom().getMetaData().get("width");
        	if (width == null) {
        		width = 40;
        	}
        	Integer height = (Integer) connection.getFrom().getMetaData().get("height");
        	if (height == null) {
        		height = 40;
        	}
			xmlDump.append(
				"        <di:waypoint x=\"" + (x + width/2) + "\" y=\"" + (y + height/2) + "\" />" + EOL);
            if (bendpoints != null) {
            	bendpoints = bendpoints.substring(1, bendpoints.length() - 1);
            	String[] points = bendpoints.split(";");
            	for (String point: points) {
            		String[] coords = point.split(",");
            		if (coords.length == 2) {
            			xmlDump.append(
        					"        <di:waypoint x=\"" + coords[0] + "\" y=\"" + coords[1] + "\" />" + EOL);
            		}
            	}
            }
        	x = (Integer) connection.getTo().getMetaData().get("x");
        	if (x == null) {
        		x = 0;
        	}
        	y = (Integer) connection.getTo().getMetaData().get("y");
        	if (y == null) {
        		y = 0;
        	}
        	width = (Integer) connection.getTo().getMetaData().get("width");
        	if (width == null) {
        		width = 40;
        	}
        	height = (Integer) connection.getTo().getMetaData().get("height");
        	if (height == null) {
        		height = 40;
        	}
			xmlDump.append(
				"        <di:waypoint x=\"" + (x + width/2) + "\" y=\"" + (y + height/2) + "\" />" + EOL);
        	xmlDump.append(
        		"      </bpmndi:BPMNEdge>" + EOL);
        }
    }

    public static String getUniqueNodeId(Node node) {
    	String result = (String) node.getMetaData().get("UniqueId");
    	if (result != null) {
    		return result;
    	}
    	result = node.getId() + "";
    	NodeContainer nodeContainer = node.getNodeContainer();
    	while (nodeContainer instanceof CompositeNode) {
    		CompositeNode composite = (CompositeNode) nodeContainer;
    		result = composite.getId() + "-" + result;
    		nodeContainer = composite.getNodeContainer();
    	}
    	return "_" + result;
    }

    public static String replaceIllegalCharsAttribute(final String code) {
        final StringBuilder sb = new StringBuilder();
        if ( code != null ) {
            final int n = code.length();
            for ( int i = 0; i < n; i++ ) {
                final char c = code.charAt( i );
                switch ( c ) {
	                case '<' :
	                    sb.append( "&lt;" );
	                    break;
	                case '>' :
	                    sb.append( "&gt;" );
	                    break;
	                case '&' :
	                    sb.append( "&amp;" );
	                    break;
                    case '"' :
                        sb.append( "&quot;" );
                        break;
                    default :
                        sb.append( c );
                        break;
                }
            }
        } else {
            sb.append( "null" );
        }
        return sb.toString();
    }

	@Override
	public String dumpProcess(Process process) {
		return dump((RuleFlowProcess) process, false);
	}

	@Override
	public Process readProcess(String processXml) {
		SemanticModules semanticModules = new SemanticModules();
        semanticModules.addSemanticModule(new BPMNSemanticModule());
	    semanticModules.addSemanticModule(new BPMNExtensionsSemanticModule());
        semanticModules.addSemanticModule(new BPMNDISemanticModule());
        XmlProcessReader xmlReader = new XmlProcessReader(semanticModules, Thread.currentThread().getContextClassLoader());
        try {
        	List<Process> processes = xmlReader.read(new StringReader(processXml));
            return processes.get(0);
        } catch (Throwable t) {
        	t.printStackTrace();
        	return null;
        }
	}
}
