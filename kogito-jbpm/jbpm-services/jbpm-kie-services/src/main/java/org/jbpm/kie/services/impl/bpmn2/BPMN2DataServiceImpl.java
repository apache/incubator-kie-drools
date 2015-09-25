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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.compiler.compiler.BPMN2ProcessProvider;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.util.StringUtils;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.kie.services.impl.bpmn2.builder.DataServiceExpressionBuilder;
import org.jbpm.kie.services.impl.bpmn2.builder.dialect.ThreadLocalAbstractBuilderFacade;
import org.jbpm.kie.services.impl.bpmn2.builder.dialect.java.DataServiceJavaProcessDialect;
import org.jbpm.kie.services.impl.bpmn2.builder.dialect.mvel.DataServiceMvelProcessDialect;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.kie.api.definition.process.Process;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BPMN2DataServiceImpl implements DefinitionService, DeploymentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(BPMN2DataServiceImpl.class);

    private static final BPMN2DataServiceSemanticModule MODULE = new BPMN2DataServiceSemanticModule();
    private static final BPMN2DataServiceExtensionSemanticModule EXTENSIONS_MODULE = new BPMN2DataServiceExtensionSemanticModule(MODULE);

    private ConcurrentMap<String, Map<String, ProcessDescRepoHelper>> definitionCache =
    		new ConcurrentHashMap<String, Map<String, ProcessDescRepoHelper>>();

    static final String [] SCRIPT_DIALECT_NAMES = { "java", "mvel" };
    private static final ProcessDialect [] dataServiceDialects = {
            new DataServiceJavaProcessDialect(),
            new DataServiceMvelProcessDialect()
    };

    static {
        /**
         * Replace the original dialects with the data service dialect instances.
         */
        for( int i = 0; i < SCRIPT_DIALECT_NAMES.length; ++i ) {
            ProcessDialectRegistry.setDialect(SCRIPT_DIALECT_NAMES[i], dataServiceDialects[i]);
        }
    }

    public BPMN2DataServiceImpl() {
    }

    public BPMN2ProcessProvider getProvider(final BPMN2DataServiceSemanticModule module, final BPMN2DataServiceExtensionSemanticModule extensionsModule) {
         return new BPMN2ProcessProvider() {
            @Override
            public void configurePackageBuilder(KnowledgeBuilder packageBuilder) {
                KnowledgeBuilderConfigurationImpl conf
                    = (KnowledgeBuilderConfigurationImpl) ((KnowledgeBuilderImpl) packageBuilder).getBuilderConfiguration();
                if (conf.getSemanticModules().getSemanticModule("http://www.jboss.org/bpmn2-data-services") == null) {
                    conf.addSemanticModule(module);
                }
                if (conf.getSemanticModules().getSemanticModule("http://www.jboss.org/bpmn2-data-services-ext") == null) {
                    conf.addSemanticModule(extensionsModule);
                }
                conf.addSemanticModule( new BPMNDISemanticModule() );
            }
        };
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

	@Override
	public ProcessDefinition buildProcessDefinition(String deploymentId,String bpmn2Content, KieContainer kieContainer, boolean cache)
			throws IllegalArgumentException {
		if (StringUtils.isEmpty(bpmn2Content)) {
            return null;
        }

		validateNonEmptyDeploymentIdAndProcessId(deploymentId, "no proc id");

        BPMN2ProcessProvider provider = getProvider(MODULE, EXTENSIONS_MODULE);
        BPMN2ProcessProvider originalProvider = BPMN2ProcessFactory.getBPMN2ProcessProvider();
        if (originalProvider != provider) {
            BPMN2ProcessFactory.setBPMN2ProcessProvider(provider);
        }
        try {
            BPMN2DataServiceSemanticModule.setUseByThisThread(true);
            BPMN2DataServiceExtensionSemanticModule.setUseByThisThread(true);

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

	        KnowledgePackage pckg = kbuilder.getKnowledgePackages().iterator().next();

	        Process process = pckg.getProcesses().iterator().next();

	        ProcessDescRepoHelper helper = MODULE.getRepo().removeProcessDescription(process.getId());
	        ProcessAssetDesc definition = helper.getProcess();

	        definition.setAssociatedEntities(helper.getTaskAssignments());
	        definition.setProcessVariables(helper.getInputs());
	        definition.setServiceTasks(helper.getServiceTasks());

	        if( kieContainer != null && helper.hasUnresolvedReusableSubProcessNames() ) {
	           helper.resolveReusableSubProcessNames(kieContainer.getKieBase().getProcesses());
	        }
	        definition.setReusableSubProcesses(helper.getReusableSubProcesses());

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
        } finally {
        	BPMN2ProcessFactory.setBPMN2ProcessProvider(originalProvider);
            BPMN2DataServiceSemanticModule.dispose();
            // BPMN2DataServiceExtensionSemanticModule.dispose() <-- not needed because the modules share the same ThreadLocal instance

            // threads are sometimes recycled
            BPMN2DataServiceSemanticModule.setUseByThisThread(false);
            BPMN2DataServiceExtensionSemanticModule.setUseByThisThread(false);
        }
	}

	/**
	 * This method is used to set the process {@link ProcessDescRepoHelper} instance.
	 * @param processHelper
	 */
	static void useDataServiceExpressionBuilders(ProcessDescRepoHelper processHelper) {
	    for( int i = 0; i < SCRIPT_DIALECT_NAMES.length; ++i ) {
	        ProcessDialect dialect = ProcessDialectRegistry.getDialect(SCRIPT_DIALECT_NAMES[i]);

	        ActionBuilder actionBuilder = null;
	        try {
	            // why must I wait so long for Java 8?!? :)
	            // (and copy/paste this code 4 times.. :/ )
	            actionBuilder = dialect.getActionBuilder();
	            if( actionBuilder instanceof ThreadLocalAbstractBuilderFacade ) {
	                ((ThreadLocalAbstractBuilderFacade) actionBuilder).useDataServiceBuilder(processHelper);
	            }
	        } catch( UnsupportedOperationException uoe ) {
	            // do nothing
	        }

	        AssignmentBuilder assignmentBuilder = null;
	        try {
	            assignmentBuilder = dialect.getAssignmentBuilder();
	            if( assignmentBuilder instanceof DataServiceExpressionBuilder ) {
	                ((DataServiceExpressionBuilder) assignmentBuilder).setProcessHelperForThread(processHelper);
	            }
	        } catch( UnsupportedOperationException uoe ) {
	            // do nothing
	        }

	        ReturnValueEvaluatorBuilder returnValueEvaluatorBuilder = null;
	        try {
	            returnValueEvaluatorBuilder = dialect.getReturnValueEvaluatorBuilder();
	            if( returnValueEvaluatorBuilder instanceof DataServiceExpressionBuilder ) {
	                ((DataServiceExpressionBuilder) returnValueEvaluatorBuilder).setProcessHelperForThread(processHelper);
	            }
	        } catch( UnsupportedOperationException uoe ) {
	            // do nothing
	        }

	        ProcessClassBuilder processClassBuilder = null;
	        try {
	            processClassBuilder = dialect.getProcessClassBuilder();
	            if( processClassBuilder instanceof DataServiceExpressionBuilder ) {
	                ((DataServiceExpressionBuilder) processClassBuilder).setProcessHelperForThread(processHelper);
	            }
	        } catch( UnsupportedOperationException uoe ) {
	            // do nothing
	        }
	    }
	}

	static void resetDialectExpressionBuilders() {
	    for( int i = 0; i < SCRIPT_DIALECT_NAMES.length; ++i ) {
	        ProcessDialect dialect = ProcessDialectRegistry.getDialect(SCRIPT_DIALECT_NAMES[i]);

	        ActionBuilder actionBuilder = null;
	        try {
	            // why must I wait so long for Java 8?!? :)
	            // (and instead have to copy/paste this code 4 times.. :/ )
	            actionBuilder = dialect.getActionBuilder();
	            if( actionBuilder instanceof ThreadLocalAbstractBuilderFacade ) {
	                ((ThreadLocalAbstractBuilderFacade) actionBuilder).resetThreadLocalBuilder();
	            }
	        } catch( UnsupportedOperationException uoe ) {
	            // do nothing
	        }

	        AssignmentBuilder assignmentBuilder = null;
	        try {
	            assignmentBuilder = dialect.getAssignmentBuilder();
	            if( assignmentBuilder instanceof ThreadLocalAbstractBuilderFacade ) {
	                ((ThreadLocalAbstractBuilderFacade) assignmentBuilder).resetThreadLocalBuilder();
	            }
	        } catch( UnsupportedOperationException uoe ) {
	            // do nothing
	        }

	        ReturnValueEvaluatorBuilder returnValueEvaluatorBuilder = null;
	        try {
	            returnValueEvaluatorBuilder = dialect.getReturnValueEvaluatorBuilder();
	            if( returnValueEvaluatorBuilder instanceof ThreadLocalAbstractBuilderFacade ) {
	                ((ThreadLocalAbstractBuilderFacade) returnValueEvaluatorBuilder).resetThreadLocalBuilder();
	            }
	        } catch( UnsupportedOperationException uoe ) {
	            // do nothing
	        }

	        ProcessClassBuilder processClassBuilder = null;
	        try {
	            processClassBuilder = dialect.getProcessClassBuilder();
	            if( processClassBuilder instanceof ThreadLocalAbstractBuilderFacade ) {
	                ((ThreadLocalAbstractBuilderFacade) processClassBuilder).resetThreadLocalBuilder();
	            }
	        } catch( UnsupportedOperationException uoe ) {
	            // do nothing
	        }
	    }
	}


	@Override
	public Map<String, String> getServiceTasks(String deploymentId, String processId) {
	    validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

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
	    validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

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
	    validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

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
        validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

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
        validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

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
        validateNonEmptyDeploymentIdAndProcessId(deploymentId, processId);

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
        validateNonEmptyDeploymentIdAndProcessIdAndTaskName(deploymentId, processId, taskName);

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
        validateNonEmptyDeploymentIdAndProcessIdAndTaskName(deploymentId, processId, taskName);

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
            ProcessDescRepoHelper helper = definitionCache.get(deploymentId).get(processId);
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
            ProcessDescRepoHelper helper = definitionCache.get(deploymentId).get(processId);
            if (helper == null) {
                throw new IllegalStateException("No process available with given id : " + processId);
            }

            return Collections.unmodifiableSet(helper.getReferencedRules());
        }

        return Collections.emptySet();
    }
}
