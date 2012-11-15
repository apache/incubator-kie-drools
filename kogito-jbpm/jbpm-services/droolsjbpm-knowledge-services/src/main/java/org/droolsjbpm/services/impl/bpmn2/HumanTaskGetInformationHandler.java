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
package org.droolsjbpm.services.impl.bpmn2;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jbpm.bpmn2.xml.UserTaskHandler;
import org.jbpm.task.TaskDef;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.workflow.core.node.HumanTaskNode;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@ApplicationScoped
public class HumanTaskGetInformationHandler extends UserTaskHandler {

    private ProcessDescRepoHelper repo;

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
        TaskDef task = new TaskDef();
        task.setName(name);

        Map<String, String> inputParams = new HashMap<String, String>();
        

        for (Map.Entry<String, String> in : dataInputs.entrySet()) {
            inputParams.put(in.getKey(), in.getValue());

        }
        Map<String, String> outputParams = new HashMap<String, String>();
        for (Map.Entry<String, String> out : dataOutputs.entrySet()) {
            outputParams.put(out.getKey(), out.getValue());
        }

        repo.getTasks().put(task.getName(), task);
        repo.getTaskInputMappings().put(task.getName(), inputParams);
        repo.getTaskOutputMappings().put(task.getName(), outputParams);
    }

    @Override
    protected String readPotentialOwner(org.w3c.dom.Node xmlNode, HumanTaskNode humanTaskNode) {
        String userOrGroup = xmlNode.getFirstChild().getFirstChild().getFirstChild().getTextContent();
        repo.getTaskAssignments().put(humanTaskNode.getName(), userOrGroup);
        return userOrGroup;
    }

    public void setRepo(ProcessDescRepoHelper repo) {
        this.repo = repo;
    }
}
