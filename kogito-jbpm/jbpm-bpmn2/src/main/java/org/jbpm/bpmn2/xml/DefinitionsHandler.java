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

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.BooleanDataType;
import org.jbpm.process.core.datatype.impl.type.FloatDataType;
import org.jbpm.process.core.datatype.impl.type.IntegerDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.core.datatype.impl.type.UndefinedDataType;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.Interface;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.core.Interface.Operation;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DefinitionsHandler extends BaseAbstractHandler implements Handler {

	@SuppressWarnings("unchecked")
	public DefinitionsHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet();
			this.validParents.add(null);

			this.validPeers = new HashSet();
			this.validPeers.add(null);

			this.allowNesting = false;
		}
	}

	public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);
		return new Definitions();
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		Definitions definitions = (Definitions) parser.getCurrent();
        String namespace = element.getAttribute("targetNamespace");
        List<Process> processes = ((ProcessBuildData) parser.getData()).getProcesses();
		Map<String, ItemDefinition> itemDefinitions = (Map<String, ItemDefinition>)
            ((ProcessBuildData) parser.getData()).getMetaData("ItemDefinitions");
		
		List<Interface> interfaces = (List<Interface>) ((ProcessBuildData) parser.getData()).getMetaData("Interfaces");
		
        for (Process process : processes) {
            RuleFlowProcess ruleFlowProcess = (RuleFlowProcess)process;
            ruleFlowProcess.setMetaData("TargetNamespace", namespace);
            postProcessItemDefinitions(ruleFlowProcess, itemDefinitions, parser.getClassLoader());
            postProcessInterfaces(ruleFlowProcess, interfaces);
        }
        definitions.setTargetNamespace(namespace);
        return definitions;
	}

	public Class<?> generateNodeFor() {
		return Definitions.class;
	}
	
	private void postProcessInterfaces(NodeContainer nodeContainer, List<Interface> interfaces) {

		for (Node node: nodeContainer.getNodes()) {
			if (node instanceof NodeContainer) {
				postProcessInterfaces((NodeContainer) node, interfaces);
			}
			if (node instanceof WorkItemNode && "Service Task".equals(((WorkItemNode) node).getMetaData("Type"))) {
				WorkItemNode workItemNode = (WorkItemNode) node;
				if (interfaces == null) {
		            throw new IllegalArgumentException("No interfaces found");
		        }
				String operationRef = (String) workItemNode.getMetaData("OperationRef");
		        String implementation = (String) workItemNode.getMetaData("Implementation");
		        Operation operation = null;
		        for (Interface i: interfaces) {
		            operation = i.getOperation(operationRef);
		            if (operation != null) {
		                break;
		            }
		        }
		        if (operation == null) {
		            throw new IllegalArgumentException("Could not find operation " + operationRef);
		        }
		        // avoid overriding parameters set by data input associations
		        if (workItemNode.getWork().getParameter("Interface") == null) {
		            workItemNode.getWork().setParameter("Interface", operation.getInterface().getName());
		        }
		        if (workItemNode.getWork().getParameter("Operation") == null) {
		            workItemNode.getWork().setParameter("Operation", operation.getName());
		        }
		        if (workItemNode.getWork().getParameter("ParameterType") == null) {
		            workItemNode.getWork().setParameter("ParameterType", operation.getMessage().getType());
		        }
		        // parameters to support web service invocation 
		        if (implementation != null) {
		            workItemNode.getWork().setParameter("interfaceImplementationRef", operation.getInterface().getImplementationRef());
		            workItemNode.getWork().setParameter("operationImplementationRef", operation.getImplementationRef());
		            workItemNode.getWork().setParameter("implementation", implementation);
		        }
			}
		}
	}
	
	private void postProcessItemDefinitions(NodeContainer nodeContainer, Map<String, ItemDefinition> itemDefinitions, ClassLoader cl) {
		if (nodeContainer instanceof ContextContainer) {
			setVariablesDataType((ContextContainer) nodeContainer, itemDefinitions, cl);
		}
		// process composite context node of for each to enhance its variables with types
		if (nodeContainer instanceof ForEachNode) {
		    setVariablesDataType(((ForEachNode) nodeContainer).getCompositeNode(), itemDefinitions, cl);
		}
		for (Node node: nodeContainer.getNodes()) {
			if (node instanceof NodeContainer) {
				postProcessItemDefinitions((NodeContainer) node, itemDefinitions, cl);
			}
			if (node instanceof ContextContainer) {
				setVariablesDataType((ContextContainer) node, itemDefinitions, cl);
			}
		}
	}
	
	private void setVariablesDataType(ContextContainer container, Map<String, ItemDefinition> itemDefinitions, ClassLoader cl) {
		VariableScope variableScope = (VariableScope) container.getDefaultContext(VariableScope.VARIABLE_SCOPE);
		if (variableScope != null) {
			for (Variable variable: variableScope.getVariables()) {
				setVariableDataType(variable, itemDefinitions, cl);
			}
		}
	}
	
	private void setVariableDataType(Variable variable, Map<String, ItemDefinition> itemDefinitions, ClassLoader cl) {
		// retrieve type from item definition
		
		String itemSubjectRef = (String) variable.getMetaData("ItemSubjectRef");
        if (UndefinedDataType.getInstance().equals(variable.getType()) && itemDefinitions != null && itemSubjectRef != null) {
    		DataType dataType = new ObjectDataType();
        	ItemDefinition itemDefinition = itemDefinitions.get(itemSubjectRef);
        	if (itemDefinition != null) {
        	    String structureRef = itemDefinition.getStructureRef();
        	    
        	    if ("java.lang.Boolean".equals(structureRef) || "Boolean".equals(structureRef)) {
        	        dataType = new BooleanDataType();
        	        
        	    } else if ("java.lang.Integer".equals(structureRef) || "Integer".equals(structureRef)) {
        	        dataType = new IntegerDataType();
                    
        	    } else if ("java.lang.Float".equals(structureRef) || "Float".equals(structureRef)) {
        	        dataType = new FloatDataType();
                    
                } else if ("java.lang.String".equals(structureRef) || "String".equals(structureRef)) {
                    dataType = new StringDataType();
                    
                } else if ("java.lang.Object".equals(structureRef) || "Object".equals(structureRef)) {
                	// use FQCN of Object
                    dataType = new ObjectDataType("java.lang.Object");
                    
                } else {
                    dataType = new ObjectDataType(structureRef, cl);
                }
        		
        	}
    		variable.setType(dataType);
        }
	}
	
}
