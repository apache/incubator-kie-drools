/*
 * Copyright 2012 JBoss by Red Hat.
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
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.compiler.compiler.BPMN2ProcessProvider;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.util.StringUtils;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BPMN2DataServiceImpl implements DefinitionService {
    
    private static final Logger logger = LoggerFactory.getLogger(BPMN2DataServiceImpl.class);
    
    private ConcurrentHashMap<String, Map<String, ProcessDescRepoHelper>> definitionCache = 
    		new ConcurrentHashMap<String, Map<String, ProcessDescRepoHelper>>();
    


    public BPMN2DataServiceImpl() {
    }
        
    public BPMN2ProcessProvider getProvider(final BPMN2DataServiceSemanticModule module) {
         return new BPMN2ProcessProvider() {
            @Override
            public void configurePackageBuilder(KnowledgeBuilder packageBuilder) {
                KnowledgeBuilderConfigurationImpl conf 
                    = (KnowledgeBuilderConfigurationImpl) ((KnowledgeBuilderImpl) packageBuilder).getBuilderConfiguration();
                if (conf.getSemanticModules().getSemanticModule("http://www.jboss.org/bpmn2-data-services") == null) {
                    conf.addSemanticModule(module);
                }
                conf.addSemanticModule( new BPMNExtensionsSemanticModule() );
                conf.addSemanticModule( new BPMNDISemanticModule() );
            }
        };
    }
    
	@Override
	public ProcessDefinition buildProcessDefinition(String deploymentId,String bpmn2Content, ClassLoader classLoader, boolean cache)
			throws IllegalArgumentException {
		if (StringUtils.isEmpty(bpmn2Content)) {
            return null;
        }
		BPMN2DataServiceSemanticModule module = new BPMN2DataServiceSemanticModule();
        BPMN2ProcessProvider provider = getProvider(module);
        BPMN2ProcessProvider originalProvider = BPMN2ProcessFactory.getBPMN2ProcessProvider();
        if (originalProvider != provider) {
            BPMN2ProcessFactory.setBPMN2ProcessProvider(provider);
        }

        KnowledgeBuilder kbuilder = null;
        if (classLoader != null) {
            KnowledgeBuilderConfigurationImpl pconf = new KnowledgeBuilderConfigurationImpl(classLoader);
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
        
        BPMN2ProcessFactory.setBPMN2ProcessProvider(originalProvider);
        
        KnowledgePackage pckg = kbuilder.getKnowledgePackages().iterator().next();
        
        org.kie.api.definition.process.Process process = pckg.getProcesses().iterator().next();
        ProcessAssetDesc definition = new ProcessAssetDesc(process.getId(), process.getName(), process.getVersion()
                , process.getPackageName(), process.getType(), process.getKnowledgeType().name(),
                process.getNamespace(), "");
        
        ProcessDescRepoHelper helper = module.getRepo().removeProcessDescription(process.getId());
        
        definition.setAssociatedEntities(helper.getTaskAssignments());
        definition.setProcessVariables(helper.getInputs());
        definition.setReusableSubProcesses(helper.getReusableSubProcesses());
        definition.setServiceTasks(helper.getServiceTasks());
        
        // cache the data if requested
        if (cache) {
        	Map<String, ProcessDescRepoHelper> definitions = null;
        	synchronized (definitionCache) {
        		definitions = definitionCache.get(deploymentId);
        		if (definitions == null) {
        			definitions = new ConcurrentHashMap<String, ProcessDescRepoHelper>();
        			definitionCache.put(deploymentId, definitions);
        		}
        		definitions.put(process.getId(), helper);
			}
        }
        
        
        return definition;
	}

	@Override
	public Map<String, String> getServiceTasks(String deploymentId, String processId) {
        if (StringUtils.isEmpty(deploymentId) || StringUtils.isEmpty(processId)) {
            throw new IllegalStateException("The Deployment id and Process id cannot be Empty!");
        }
        
        if (definitionCache.containsKey(deploymentId)) {
	        
	        ProcessDescRepoHelper helper = definitionCache.get(deploymentId).get(processId);
	        if (helper == null) {
	            throw new IllegalStateException("No process available with given id : " + processId);
	        }
	        return Collections.unmodifiableMap(helper.getServiceTasks());
        }
        
        return Collections.emptyMap();
    }

	@Override
	public ProcessDefinition getProcessDefinition(String deploymentId, String processId) {
		if (StringUtils.isEmpty(deploymentId) || StringUtils.isEmpty(processId)) {
            throw new IllegalStateException("The Process id cannot be Empty!");
        }
        
        if (definitionCache.containsKey(deploymentId)) {
	        
	        ProcessDescRepoHelper helper = definitionCache.get(deploymentId).get(processId);
	        if (helper == null) {
	            throw new IllegalStateException("No process available with given id : " + processId);
	        }
	        return helper.getProcess();
        }
        
        return null;
	}



	@Override
	public Collection<String> getReusableSubProcesses(String deploymentId, String processId) {
		if (StringUtils.isEmpty(deploymentId) || StringUtils.isEmpty(processId)) {
            throw new IllegalStateException("The Process id cannot be Empty!");
        }
        if (definitionCache.containsKey(deploymentId)) {
	        
	        ProcessDescRepoHelper helper = definitionCache.get(deploymentId).get(processId);
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
		if (StringUtils.isEmpty(deploymentId) || StringUtils.isEmpty(processId)) {
			throw new IllegalStateException("The Process id cannot be Empty!");
        }
        if (definitionCache.containsKey(deploymentId)) {
	        
	        ProcessDescRepoHelper helper = definitionCache.get(deploymentId).get(processId);
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
		if (StringUtils.isEmpty(deploymentId) || StringUtils.isEmpty(processId)) {
		     throw new IllegalStateException("The Process id cannot be Empty!");
        }
        
        if (definitionCache.containsKey(deploymentId)) {
	        
	        ProcessDescRepoHelper helper = definitionCache.get(deploymentId).get(processId);
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
		if (StringUtils.isEmpty(deploymentId) || StringUtils.isEmpty(processId)) {
			 throw new IllegalStateException("The Process id cannot be Empty!");
        }
    
        if (definitionCache.containsKey(deploymentId)) {
	        
	        ProcessDescRepoHelper helper = definitionCache.get(deploymentId).get(processId);
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
		if (StringUtils.isEmpty(deploymentId) || StringUtils.isEmpty(processId) || StringUtils.isEmpty(taskName)) {
			 throw new IllegalStateException("The Process id cannot be Empty!");
        }
        if (definitionCache.containsKey(deploymentId)) {
	        
	        ProcessDescRepoHelper helper = definitionCache.get(deploymentId).get(processId);
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
		if (StringUtils.isEmpty(deploymentId) || StringUtils.isEmpty(processId) || StringUtils.isEmpty(taskName)) {
			throw new IllegalStateException("The Process id cannot be Empty!");
        }
        
        if (definitionCache.containsKey(deploymentId)) {
	        
	        ProcessDescRepoHelper helper = definitionCache.get(deploymentId).get(processId);
	        if (helper == null) {
	            throw new IllegalStateException("No process available with given id : " + processId);
	        }
	        
	        if (helper.getTaskOutputMappings().containsKey(taskName)) {
	        	return Collections.unmodifiableMap(helper.getTaskOutputMappings().get(taskName));
	        }
        }
        
        return Collections.emptyMap();
	}
}
