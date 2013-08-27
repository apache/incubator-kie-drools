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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.drools.core.xml.ExtensibleXmlParser;

import org.jbpm.bpmn2.xml.UserTaskHandler;
import org.jbpm.services.task.impl.model.TaskDefImpl;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.WorkItemNode;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@ApplicationScoped
public class HumanTaskGetInformationHandler extends UserTaskHandler {

    private ProcessDescRepoHelper repositoryHelper;
    
    @Inject
    private ProcessDescriptionRepository repository;

    /**
     * Creates a new {@link HumanTaskGetInformationHandler} instance.
     *
     * @param humanTaskRepository the {@link HumanTaskRepository}.
     */
    public HumanTaskGetInformationHandler() {
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
        NamedNodeMap map = xmlNode.getParentNode().getAttributes();
        Node nodeName = map.getNamedItem("name");
        String name = nodeName.getNodeValue();
        TaskDefImpl task = new TaskDefImpl();
        task.setName(name);

        Map<String, String> inputParams = new HashMap<String, String>();
        
        String mainProcessId = repositoryHelper.getProcess().getId();
        for (Map.Entry<String, String> in : dataInputs.entrySet()) {
                inputParams.put(in.getKey(), in.getValue());

        }
        Map<String, String> outputParams = new HashMap<String, String>();
        for (Map.Entry<String, String> out : dataOutputs.entrySet()) {
            outputParams.put(out.getKey(), out.getValue());
        }
        

        repository.getProcessDesc(mainProcessId).getTasks().put(task.getName(), task);
        repository.getProcessDesc(mainProcessId).getTaskInputMappings().put(task.getName(), inputParams);
        repository.getProcessDesc(mainProcessId).getTaskOutputMappings().put(task.getName(), outputParams);
    }

    @Override
    protected void handleNode(final org.jbpm.workflow.core.Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
            super.handleNode(node, element, uri, localName, parser);
        WorkItemNode humanTaskNode = (WorkItemNode) node;
        Map<String, Object> parameters = humanTaskNode.getWork().getParameters();
        String mainProcessId = repositoryHelper.getProcess().getId();
        for(String parameter : parameters.keySet()){
            if(parameter.equals("GroupId")){
               String currentAssignment = "";
               if(repository.getProcessDesc(mainProcessId).getTaskAssignments().get(humanTaskNode.getName()) != null){
                   currentAssignment = repository.getProcessDesc(mainProcessId).getTaskAssignments().get(humanTaskNode.getName());
               } 
               if(!currentAssignment.equals("")){
                    repository.getProcessDesc(mainProcessId).getTaskAssignments().put(humanTaskNode.getName(),
                                     humanTaskNode.getWork().getParameter(parameter).toString()+ ","+ currentAssignment);     
               }else{
                    repository.getProcessDesc(mainProcessId).getTaskAssignments().put(humanTaskNode.getName(),
                                     humanTaskNode.getWork().getParameter(parameter).toString());
               }
            }
        }
        
       
    }
    
    @Override
    protected String readPotentialOwner(org.w3c.dom.Node xmlNode, HumanTaskNode humanTaskNode) {
        String user = xmlNode.getFirstChild().getFirstChild().getFirstChild().getTextContent();
        String mainProcessId = repositoryHelper.getProcess().getId();
        String currentAssignment = "";
        if(repository.getProcessDesc(mainProcessId).getTaskAssignments().get(humanTaskNode.getName()) != null){
            currentAssignment = repository.getProcessDesc(mainProcessId).getTaskAssignments().get(humanTaskNode.getName());
        }
        if(!currentAssignment.equals("")){
            repository.getProcessDesc(mainProcessId).getTaskAssignments().put(humanTaskNode.getName(), user + ","+currentAssignment);
        }else{
            repository.getProcessDesc(mainProcessId).getTaskAssignments().put(humanTaskNode.getName(), user);
        }
        return user;
    }


    
    public void setRepositoryHelper(ProcessDescRepoHelper repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setRepository(ProcessDescriptionRepository repository) {
        this.repository = repository;
    }
    
    
}
