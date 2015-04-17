/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.kie.services.impl.bpmn2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.xml.UserTaskHandler;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class HumanTaskGetInformationHandler extends UserTaskHandler {

    private ProcessDescriptionRepository repository;
    
    private BPMN2DataServiceSemanticModule module;
    
    private Map<String, ItemDefinition> itemDefinitions;    

    /**
     * Creates a new {@link HumanTaskGetInformationHandler} instance.
     *
     * @param humanTaskRepository the {@link HumanTaskRepository}.
     */
    public HumanTaskGetInformationHandler() {
    }

    public HumanTaskGetInformationHandler(BPMN2DataServiceSemanticModule module) {
    	this.module = module;
    	this.repository = module.getRepo();

	}

	/**
     * Reads the io specification and put the information in the
     * {@link HumanTaskRepository}.
     */
    @Override
    protected void readIoSpecification(org.w3c.dom.Node xmlNode,
            Map<String, String> dataInputs, Map<String, String> dataOutputs) {
        dataInputs.clear();
        dataOutputs.clear(); 
        
        Map<String, String> dataTypeInputs = new HashMap<String, String>();
        Map<String, String> dataTypeOutputs = new HashMap<String, String>();

        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        while (subNode instanceof Element) {
            String subNodeName = subNode.getNodeName();
            if ("dataInput".equals(subNodeName)) {
            	String id = ((Element) subNode).getAttribute("id");
            	String inputName = ((Element) subNode).getAttribute("name");
                String itemSubjectRef = ((Element) subNode).getAttribute("itemSubjectRef");
                dataInputs.put(id, inputName);
                if (itemSubjectRef == null || itemSubjectRef.isEmpty()) {
                	String dataType = ((Element) subNode).getAttribute("dtype");
                	if (dataType == null || dataType.isEmpty()) {
                		dataType = "java.lang.String";
                	}
                	dataTypeInputs.put(inputName, dataType);
                } else {
                	dataTypeInputs.put(inputName, itemDefinitions.get(itemSubjectRef).getStructureRef());
                }
            }
            if ("dataOutput".equals(subNodeName)) {
            	String id = ((Element) subNode).getAttribute("id");
                String outputName = ((Element) subNode).getAttribute("name");
                String itemSubjectRef = ((Element) subNode).getAttribute("itemSubjectRef");
                
                dataOutputs.put(id, outputName);
                if (itemSubjectRef == null || itemSubjectRef.isEmpty()) {
                	String dataType = ((Element) subNode).getAttribute("dtype");
                	if (dataType == null || dataType.isEmpty()) {
                		dataType = "java.lang.String";
                	}
                	dataTypeOutputs.put(outputName, dataType);
                } else {
                	dataTypeOutputs.put(outputName, itemDefinitions.get(itemSubjectRef).getStructureRef());
                }
            }
            subNode = subNode.getNextSibling();
        }
        NamedNodeMap map = xmlNode.getParentNode().getAttributes();
        Node nodeName = map.getNamedItem("name");
        String name = nodeName.getNodeValue();

        String mainProcessId = module.getRepoHelper().getProcess().getId();
        UserTaskDefinitionImpl task = (UserTaskDefinitionImpl)repository.getProcessDesc(mainProcessId).getTasks().get(name);
        if (task == null) {
        	task = new UserTaskDefinitionImpl();
        	task.setName(name);
            repository.getProcessDesc(mainProcessId).getTasks().put(task.getName(), task);
        }

        Map<String, String> inputParams = new HashMap<String, String>();
        
        
        for (Map.Entry<String, String> in : dataTypeInputs.entrySet()) {
        	inputParams.put(in.getKey(), in.getValue());
        }
        Map<String, String> outputParams = new HashMap<String, String>();
        for (Map.Entry<String, String> out : dataTypeOutputs.entrySet()) {
            outputParams.put(out.getKey(), out.getValue());
        }

        task.setTaskInputMappings(inputParams);
        task.setTaskOutputMappings(outputParams);
        task.setComment(inputParams.get("Comment"));
        task.setCreatedBy(inputParams.get("CreatedBy"));
        task.setPriority(getInteger(inputParams.get("Priority")));
        task.setSkippable("true".equalsIgnoreCase(inputParams.get("Skippable")));
        repository.getProcessDesc(mainProcessId).getTaskInputMappings().put(task.getName(), inputParams);
        repository.getProcessDesc(mainProcessId).getTaskOutputMappings().put(task.getName(), outputParams);
    }
    
    

    @SuppressWarnings("unchecked")
	@Override
    protected void handleNode(final org.jbpm.workflow.core.Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	itemDefinitions = (Map<String, ItemDefinition>)((ProcessBuildData) parser.getData()).getMetaData("ItemDefinitions");
    	super.handleNode(node, element, uri, localName, parser);
        WorkItemNode humanTaskNode = (WorkItemNode) node;
        Map<String, Object> parameters = humanTaskNode.getWork().getParameters();
        String mainProcessId = module.getRepoHelper().getProcess().getId();
        
        String name = humanTaskNode.getName();
        UserTaskDefinitionImpl task = (UserTaskDefinitionImpl)repository.getProcessDesc(mainProcessId).getTasks().get(name);
        if (task == null) {
        	task = new UserTaskDefinitionImpl();
        	task.setName(name);
            repository.getProcessDesc(mainProcessId).getTasks().put(task.getName(), task);
        }
        
        Collection<String> currentAssignment = repository.getProcessDesc(mainProcessId).getTaskAssignments().get(humanTaskNode.getName());
        for(String parameter : parameters.keySet()){
            if(parameter.equals("GroupId")){
              
               if(currentAssignment == null){
            	   currentAssignment = new ArrayList<String>();
            	   repository.getProcessDesc(mainProcessId).getTaskAssignments().put(humanTaskNode.getName(), currentAssignment);                   
               } 
               currentAssignment.add(humanTaskNode.getWork().getParameter(parameter).toString());               
            }
        }
        ((UserTaskDefinitionImpl)repository.getProcessDesc(mainProcessId).getTasks().get(humanTaskNode.getName())).setAssociatedEntities(currentAssignment);
        itemDefinitions = null;
    }
    
    @Override
    protected String readPotentialOwner(org.w3c.dom.Node xmlNode, HumanTaskNode humanTaskNode) {
        String user = xmlNode.getFirstChild().getFirstChild().getFirstChild().getTextContent();
        String mainProcessId = module.getRepoHelper().getProcess().getId();
        
        String name = humanTaskNode.getName();
        UserTaskDefinitionImpl task = (UserTaskDefinitionImpl)repository.getProcessDesc(mainProcessId).getTasks().get(name);
        if (task == null) {
        	task = new UserTaskDefinitionImpl();
        	task.setName(name);
            repository.getProcessDesc(mainProcessId).getTasks().put(task.getName(), task);
        }
        Collection<String> currentAssignment = repository.getProcessDesc(mainProcessId).getTaskAssignments().get(humanTaskNode.getName());
        if(currentAssignment == null) {
        	currentAssignment = new ArrayList<String>();
            repository.getProcessDesc(mainProcessId).getTaskAssignments().put(humanTaskNode.getName(), currentAssignment);
        }
        currentAssignment.add(user);
        task.setAssociatedEntities(currentAssignment);
        return user;
    }


    public void setRepository(ProcessDescriptionRepository repository) {
        this.repository = repository;
    }
    
    private Integer getInteger(String value) {
    	int priority = 0;
        if (value != null) {
            try {
                priority = new Integer(value);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        
        return priority;
    }
    
}
