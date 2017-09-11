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

package org.jbpm.kie.services.impl.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.compiler.xml.ProcessDataEventListener;
import org.jbpm.kie.services.impl.bpmn2.ProcessDescriptor;
import org.jbpm.kie.services.impl.bpmn2.UserTaskDefinitionImpl;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.impl.ProcessImpl;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServicesProcessDataEventListener implements ProcessDataEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ServicesProcessDataEventListener.class);

    private ProcessDescriptor processDescriptor = new ProcessDescriptor();

    private Map<String, ItemDefinition> itemDefinitions;
    private Set<String> signals;

    private List<Variable> variables = new ArrayList<Variable>();

    @SuppressWarnings("unchecked")
    @Override
    public void onNodeAdded(Node node) {
        logger.debug("Added node " + node);

        if (node instanceof HumanTaskNode) {
            HumanTaskNode humanTaskNode = (HumanTaskNode) node;
            String name = humanTaskNode.getName();
            UserTaskDefinitionImpl task = (UserTaskDefinitionImpl)processDescriptor.getTasks().get(name);
            if (task == null) {
                task = new UserTaskDefinitionImpl();
                task.setId(humanTaskNode.getUniqueId());
                task.setName(name);                
                processDescriptor.getTasks().put(task.getName(), task);
            }

            Map<String, Object> parameters = humanTaskNode.getWork().getParameters();

            Collection<String> currentAssignment = processDescriptor.getTaskAssignments().get(humanTaskNode.getName());
            for(String parameter : parameters.keySet()){
                if(parameter.equals("GroupId") || parameter.equals("ActorId")){

                   if(currentAssignment == null){
                       currentAssignment = new ArrayList<String>();
                       processDescriptor.getTaskAssignments().put(humanTaskNode.getName(), currentAssignment);
                   }
                   currentAssignment.add(humanTaskNode.getWork().getParameter(parameter).toString());
                }
            }
            ((UserTaskDefinitionImpl)processDescriptor.getTasks().get(humanTaskNode.getName())).setAssociatedEntities(currentAssignment);

            Map<String, String> inputParams = new HashMap<String, String>();
            for (Map.Entry<String, String> in : ((Map<String, String>) humanTaskNode.getMetaData("DataInputs")).entrySet()) {
                inputParams.put(in.getKey(), in.getValue());
            }
            Map<String, String> outputParams = new HashMap<String, String>();
            for (Map.Entry<String, String> out : ((Map<String, String>) humanTaskNode.getMetaData("DataOutputs")).entrySet()) {
                outputParams.put(out.getKey(), out.getValue());
            }

            task.setTaskInputMappings(inputParams);
            task.setTaskOutputMappings(outputParams);

            task.setComment(asString(humanTaskNode.getWork().getParameter("Comment")));
            task.setCreatedBy(asString(humanTaskNode.getWork().getParameter("CreatedBy")));
            task.setPriority(asInt(humanTaskNode.getWork().getParameter("Priority")));
            task.setSkippable(asBoolean(humanTaskNode.getWork().getParameter("Skippable")));            
            task.setFormName(asString(humanTaskNode.getWork().getParameter("TaskName")));

            processDescriptor.getTaskInputMappings().put(task.getName(), inputParams);
            processDescriptor.getTaskOutputMappings().put(task.getName(), outputParams);

        } else if (node instanceof RuleSetNode) {
            RuleSetNode ruleSetNode = (RuleSetNode) node;
            String ruleFlowGroup = ruleSetNode.getRuleFlowGroup();
            if( ruleFlowGroup != null ) {
                processDescriptor.getReferencedRules().add(ruleFlowGroup);
            }
        } else if (node instanceof WorkItemNode) {
            processDescriptor.getServiceTasks().put(node.getName(), ((WorkItemNode) node).getWork().getName());
        } else if (node instanceof SubProcessNode) {
            SubProcessNode subProcess = (SubProcessNode) node;
            String processId = subProcess.getProcessId();


            if (subProcess.getProcessName() != null) {
                processDescriptor.addReusableSubProcessName(subProcess.getProcessName());
            } else {
                processDescriptor.getReusableSubProcesses().add(processId);
            }
        }
    }

    @Override
    public void onProcessAdded(Process process) {
        logger.debug("Added process with id {} and name {}", process.getId(), process.getName());
        ProcessAssetDesc processDesc = new ProcessAssetDesc(process.getId(), process.getName(), process.getVersion()
                , process.getPackageName(), process.getType(), process.getKnowledgeType().name(), process.getNamespace(), "", ((WorkflowProcess)process).isDynamic());

        processDescriptor.setProcess(processDesc);

        //add process descriptor as process meta data
        process.getMetaData().put("ProcessDescriptor", processDescriptor);

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onMetaDataAdded(String name, Object data) {
        if (name.equals("Variable")) {
            variables.add((Variable) data);
        } else if ("ItemDefinitions".equals(name)) {
            itemDefinitions = (Map<String, ItemDefinition>) data;
        } else if ("signalNames".equals(name)) {
            signals = (Set<String>) data;
        }
    }


    @Override
    public void onComplete(Process process) {

        // process item definitions
        if (itemDefinitions != null) {
            for (ItemDefinition item : itemDefinitions.values()) {
                String id = item.getId();
                String structureRef = item.getStructureRef();
                // NPE!
                String itemDefinitionId = processDescriptor.getGlobalItemDefinitions().get(id);

                if(itemDefinitionId == null) {
                    processDescriptor.getGlobalItemDefinitions().put(id, structureRef);

                    if( structureRef.contains(".") ) {
                        processDescriptor.getReferencedClasses().add(structureRef);
                    } else {
                        processDescriptor.getUnqualifiedClasses().add(structureRef);
                    }
                }
            }
        }

        // process globals
        Map<String, String> globals = ((RuleFlowProcess)process).getGlobals();
        if (globals != null) {
            Set<String> globalNames = new HashSet<>();
            for (Entry<String, String> globalEntry : globals.entrySet() ) {
                globalNames.add(globalEntry.getKey());
                String type = globalEntry.getValue();
                if( type.contains(".") ) {
                    processDescriptor.getReferencedClasses().add(type);
                } else {
                    processDescriptor.getUnqualifiedClasses().add(type);
                }
            }
            processDescriptor.setGlobals(globalNames);
        }

         // process imports
        Set<String> imports = ((RuleFlowProcess)process).getImports();
        if (imports != null) {
            for (String type : imports) {
                if( type.contains(".") ) {
                    processDescriptor.getReferencedClasses().add(type);
                } else {
                    processDescriptor.getUnqualifiedClasses().add(type);
                }
            }
        }
    }

    // helper methods
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

    protected void resolveUnqualifiedClasses() {
        Set<String> qualifiedClassSimpleNames = new HashSet<String>();
        for( String className : processDescriptor.getReferencedClasses() ) {
            qualifiedClassSimpleNames.add(className.substring(className.lastIndexOf('.') + 1));
        }
        for( Iterator<String> iter = processDescriptor.getUnqualifiedClasses().iterator(); iter.hasNext(); ) {
            if( qualifiedClassSimpleNames.contains(iter.next()) ) {
                iter.remove();
            }
        }
        for( Iterator<String> iter = processDescriptor.getUnqualifiedClasses().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            if( "Object".equals(name) || "String".equals(name)
                || "Float".equals(name) || "Integer".equals(name)
                || "Boolean".equals(name) ) {
                processDescriptor.getReferencedClasses().add("java.lang." + name );
               iter.remove();
            }
        }
        for( String className : processDescriptor.getUnqualifiedClasses() ) {
            logger.warn("Unable to resolve unqualified class name, adding to list of classes: '{}'", className );
            processDescriptor.getReferencedClasses().add(className);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBuildComplete(Process process) {
        // process java dialect types
        Set<String> referencedTypes = (Set<String>) process.getMetaData().get("JavaDialectReferencedTypes");
        if (referencedTypes != null && !referencedTypes.isEmpty()) {
            processDescriptor.getReferencedClasses().addAll(referencedTypes);
        }
        Set<String> unqualifiedClasses = (Set<String>) process.getMetaData().get("JavaDialectUnqualifiedTypes");
        if (unqualifiedClasses != null && !unqualifiedClasses.isEmpty()) {
            processDescriptor.getUnqualifiedClasses().addAll(unqualifiedClasses);
        }

        // process java return value types
        referencedTypes = (Set<String>) process.getMetaData().get("JavaReturnValueReferencedTypes");
        if (referencedTypes != null && !referencedTypes.isEmpty()) {
            processDescriptor.getReferencedClasses().addAll(referencedTypes);
        }
        unqualifiedClasses = (Set<String>) process.getMetaData().get("JavaReturnValueUnqualifiedTypes");
        if (unqualifiedClasses != null && !unqualifiedClasses.isEmpty()) {
            processDescriptor.getUnqualifiedClasses().addAll(unqualifiedClasses);
        }

        // process mvel dialect types
        referencedTypes = (Set<String>) process.getMetaData().get("MVELDialectReferencedTypes");
        if (referencedTypes != null && !referencedTypes.isEmpty()) {
            processDescriptor.getReferencedClasses().addAll(referencedTypes);
        }

        // process mvel return value types
        referencedTypes = (Set<String>) process.getMetaData().get("MVELReturnValueReferencedTypes");
        if (referencedTypes != null && !referencedTypes.isEmpty()) {
            processDescriptor.getReferencedClasses().addAll(referencedTypes);
        }

        // process unqualified classes
        resolveUnqualifiedClasses();


        // process variables
        if (variables != null) {
            for (Variable data : variables) {
                String type = data.getType().getStringType();
                String itemSubjectRef = (String) data.getMetaData("ItemSubjectRef");
                if (itemSubjectRef != null && itemDefinitions != null) {
                    ItemDefinition itemDef = itemDefinitions.get(itemSubjectRef);
                    type = itemDef.getStructureRef();
                }

                processDescriptor.getInputs().put(data.getName(), type);
            }
        }

        // process signals
        if( signals != null ) {
            processDescriptor.setSignals(signals);
        }
    }

    protected Integer asInt(Object value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    protected Boolean asBoolean(Object value) {
        if (value == null) {
            return true;
        }
        return Boolean.valueOf(value.toString());
    }

    protected String asString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
