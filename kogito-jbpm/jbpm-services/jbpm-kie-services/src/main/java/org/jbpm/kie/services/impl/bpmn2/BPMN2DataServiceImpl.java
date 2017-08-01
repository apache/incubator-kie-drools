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
package org.jbpm.kie.services.impl.bpmn2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.util.StringUtils;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentNotFoundException;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.ProcessDefinitionNotFoundException;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.jbpm.services.api.service.ServiceRegistry;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.process.Process;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPMN2DataServiceImpl implements DefinitionService, DeploymentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(BPMN2DataServiceImpl.class);

    private ConcurrentMap<String, Map<String, ProcessDescriptor>> definitionCache =
    		new ConcurrentHashMap<String, Map<String, ProcessDescriptor>>();

    public BPMN2DataServiceImpl() {
        
        ServiceRegistry.get().register(DefinitionService.class.getSimpleName(), this);
    }

    private void validateNonEmptyDeploymentIdAndProcessId(String deploymentId, String processId) {
       validateNonEmptyDeploymentIdAndProcessIdAndTaskName(deploymentId, processId, "x");
    }

    private void validateNonEmptyDeploymentIdAndProcessIdAndTaskName(String deploymentId, String processId, String taskName) {
        boolean emptyDepId = StringUtils.isEmpty(deploymentId);
        boolean emptyProcId = StringUtils.isEmpty(processId);
        boolean emptyTaskName = StringUtils.isEmpty(taskName);
        if ( emptyDepId || emptyProcId || emptyTaskName ) {
            StringBuffer msg = new StringBuffer("The ");
            if( emptyDepId ) {
                msg.append( "deployment id " );
            }
            if( emptyDepId && ( emptyProcId || emptyTaskName ) ) {
                msg.append( "and the " );
            }
            if( emptyProcId ) {
                msg.append( "process id " );
            }
            if( emptyDepId && emptyTaskName ) {
                msg.append( "and the " );
            }
            if( emptyTaskName ) {
                msg.append( "task name " );
            }
            msg.append( "may not be empty or null!");
            throw new IllegalStateException( msg.toString() );
        }
    }

    public void addProcessDefinition(String deploymentId, String processId, Object processDescriptor, KieContainer kieContainer) {
        Map<String, ProcessDescriptor> definitions = null;
        synchronized (definitionCache) {
            Map<String, ProcessDescriptor> newDef = new ConcurrentHashMap<String, ProcessDescriptor>();
            definitions = definitionCache.putIfAbsent(deploymentId, newDef);
            if( definitions == null ) {
                definitions = newDef;
            }

            ProcessDescriptor descriptor = (ProcessDescriptor) processDescriptor;
            fillProcessDefinition(descriptor, kieContainer);

            definitions.put(processId, descriptor);
        }
    }

	@Override
	public ProcessDefinition buildProcessDefinition(String deploymentId, String bpmn2Content, KieContainer kieContainer, boolean cache)
			throws IllegalArgumentException {
		if (StringUtils.isEmpty(bpmn2Content)) {
            return null;
        }

        KnowledgeBuilder kbuilder = null;

        if (kieContainer != null && kieContainer.getClassLoader() != null ) {
            KnowledgeBuilderConfigurationImpl pconf = new KnowledgeBuilderConfigurationImpl(kieContainer.getClassLoader());
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pconf);
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        }
        kbuilder.add(new ByteArrayResource(bpmn2Content.getBytes()), ResourceType.BPMN2);
        if (kbuilder.hasErrors()) {
            for(KnowledgeBuilderError error: kbuilder.getErrors()){
                logger.error("Error: {}", error.getMessage());
            }
            logger.debug("Process Cannot be Parsed! \n {} \n", bpmn2Content);
            return null;
        }

        KiePackage pckg = kbuilder.getKnowledgePackages().iterator().next();

        Process process = pckg.getProcesses().iterator().next();

        ProcessDescriptor helper = (ProcessDescriptor) process.getMetaData().get("ProcessDescriptor");
        ProcessAssetDesc definition = fillProcessDefinition(helper, kieContainer);

        // cache the data if requested
        if (cache) {
            validateNonEmptyDeploymentIdAndProcessId(deploymentId, "no proc id");
        	Map<String, ProcessDescriptor> definitions = null;
        	synchronized (definitionCache) {
        		Map<String, ProcessDescriptor> newDef = new ConcurrentHashMap<String, ProcessDescriptor>();
                definitions = definitionCache.putIfAbsent(deploymentId, newDef);
                if( definitions == null ) {
                    definitions = newDef;
                }
        		definitions.put(process.getId(), helper);
			}
        }


        return definition;

	}

	private ProcessAssetDesc fillProcessDefinition(ProcessDescriptor helper, KieContainer kieContainer ) {

        ProcessAssetDesc definition = helper.getProcess();

	    definition.setAssociatedEntities(helper.getTaskAssignments());
	    definition.setProcessVariables(helper.getInputs());
	    definition.setServiceTasks(helper.getServiceTasks());

	    definition.setSignals(helper.getSignals() );
	    definition.setGlobals(helper.getGlobals() );
	    definition.setReferencedRules(helper.getReferencedRules() );

        if( kieContainer != null && helper.hasUnresolvedReusableSubProcessNames() ) {
            helper.resolveReusableSubProcessNames(kieContainer.getKieBase().getProcesses());
         }
         definition.setReusableSubProcesses(helper.getReusableSubProcesses());

         return definition;
	}


	@Override
	public Map<String, String> getServiceTasks(String deploymentId, String processId) {
	    validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

	    if (definitionCache.containsKey(deploymentId)) {

	        ProcessDescriptor helper = definitionCache.get(deploymentId).get(processId);
	        if (helper == null) {
	            throw new IllegalStateException("No process available with given id : " + processId);
	        }
	        return Collections.unmodifiableMap(helper.getServiceTasks());
	    }

	    return Collections.emptyMap();
	}

	@Override
	public ProcessDefinition getProcessDefinition(String deploymentId, String processId) {
        if (definitionCache.containsKey(deploymentId)) {
            ProcessDescriptor helper = definitionCache.get(deploymentId).get(processId);
            if(helper != null && helper.getProcess() != null) {
                return helper.getProcess();
            } else {
                throw new ProcessDefinitionNotFoundException("No process available with given id : " + processId);
            }
        } else {
            throw new DeploymentNotFoundException("No deployments available for " + deploymentId);
        }
	}

	@Override
	public Collection<String> getReusableSubProcesses(String deploymentId, String processId) {
	    validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

	    if (definitionCache.containsKey(deploymentId)) {

	        ProcessDescriptor helper = definitionCache.get(deploymentId).get(processId);
	        if (helper == null) {
	            throw new IllegalStateException("No process available with given id : " + processId);
	        }

	        if (helper.getReusableSubProcesses() != null) {
	            return new ArrayList<String>(helper.getReusableSubProcesses());
	        }
	    }

	    return Collections.emptyList();
	}



	@Override
	public Map<String, String> getProcessVariables(String deploymentId, String processId) {
        validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

        if (definitionCache.containsKey(deploymentId)) {

	        ProcessDescriptor helper = definitionCache.get(deploymentId).get(processId);
	        if (helper == null) {
	            throw new IllegalStateException("No process available with given id : " + processId);
	        }
	        if (helper.getInputs() != null) {
	        	return Collections.unmodifiableMap(helper.getInputs());
	        }
        }

        return Collections.emptyMap();
	}


	@Override
	public Map<String, Collection<String>> getAssociatedEntities(String deploymentId, String processId) {
        validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

        if (definitionCache.containsKey(deploymentId)) {

	        ProcessDescriptor helper = definitionCache.get(deploymentId).get(processId);
	        if (helper == null) {
	            throw new IllegalStateException("No process available with given id : " + processId);
	        }

	        if (helper.getTaskAssignments() != null) {
	        	return Collections.unmodifiableMap(helper.getTaskAssignments());
	        }
        }

        return Collections.emptyMap();
	}

	@Override
	public Collection<UserTaskDefinition> getTasksDefinitions(String deploymentId, String processId) {
        validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

        if (definitionCache.containsKey(deploymentId)) {

	        ProcessDescriptor helper = definitionCache.get(deploymentId).get(processId);
	        if (helper == null) {
	            throw new IllegalStateException("No process available with given id : " + processId);
	        }
	        if (helper.getTasks() != null) {
	        	return new ArrayList<UserTaskDefinition>(helper.getTasks().values());
	        }
        }

        return Collections.emptyList();
	}



	@Override
	public Map<String, String> getTaskInputMappings(String deploymentId,String processId, String taskName) {
        validateNonEmptyDeploymentIdAndProcessIdAndTaskName(deploymentId, processId, taskName);

        if (definitionCache.containsKey(deploymentId)) {

	        ProcessDescriptor helper = definitionCache.get(deploymentId).get(processId);
	        if (helper == null) {
	            throw new IllegalStateException("No process available with given id : " + processId);
	        }
	        if (helper.getTaskInputMappings().containsKey(taskName)) {
	        	return Collections.unmodifiableMap(helper.getTaskInputMappings().get(taskName));
	        }
        }

        return Collections.emptyMap();
	}



	@Override
	public Map<String, String> getTaskOutputMappings(String deploymentId, String processId, String taskName) {
        validateNonEmptyDeploymentIdAndProcessIdAndTaskName(deploymentId, processId, taskName);

        if (definitionCache.containsKey(deploymentId)) {

	        ProcessDescriptor helper = definitionCache.get(deploymentId).get(processId);
	        if (helper == null) {
	            throw new IllegalStateException("No process available with given id : " + processId);
	        }

	        if (helper.getTaskOutputMappings().containsKey(taskName)) {
	        	return Collections.unmodifiableMap(helper.getTaskOutputMappings().get(taskName));
	        }
        }

        return Collections.emptyMap();
	}

	@Override
	public void onDeploy(DeploymentEvent event) {
		// no op
	}

	@Override
	public void onUnDeploy(DeploymentEvent event) {
		// remove process definitions from the cache
		definitionCache.remove(event.getDeploymentId());
	}

	@Override
	public void onActivate(DeploymentEvent event) {
		// no op
	}

	@Override
	public void onDeactivate(DeploymentEvent event) {
		// no op
	}

    @Override
    public Set<String> getJavaClasses( String deploymentId, String processId ) {
        validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

        if (definitionCache.containsKey(deploymentId)) {
            ProcessDescriptor helper = definitionCache.get(deploymentId).get(processId);
            if (helper == null) {
                throw new IllegalStateException("No process available with given id : " + processId);
            }

            return Collections.unmodifiableSet(helper.getReferencedClasses());
        }

        return Collections.emptySet();
    }

    @Override
    public Set<String> getRuleSets( String deploymentId, String processId ) {
        validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

        if (definitionCache.containsKey(deploymentId)) {
            ProcessDescriptor helper = definitionCache.get(deploymentId).get(processId);
            if (helper == null) {
                throw new IllegalStateException("No process available with given id : " + processId);
            }

            return Collections.unmodifiableSet(helper.getReferencedRules());
        }

        return Collections.emptySet();
    }
}
