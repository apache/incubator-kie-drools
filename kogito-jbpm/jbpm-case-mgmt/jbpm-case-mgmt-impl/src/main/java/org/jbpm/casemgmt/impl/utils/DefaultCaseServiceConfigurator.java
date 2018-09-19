/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.impl.utils;

import javax.persistence.EntityManagerFactory;

import org.jbpm.casemgmt.api.CaseRuntimeDataService;
import org.jbpm.casemgmt.api.CaseService;
import org.jbpm.casemgmt.api.admin.CaseInstanceMigrationService;
import org.jbpm.casemgmt.api.auth.AuthorizationManager;
import org.jbpm.casemgmt.api.generator.CaseIdGenerator;
import org.jbpm.casemgmt.api.utils.CaseServiceConfigurator;
import org.jbpm.casemgmt.impl.AuthorizationManagerImpl;
import org.jbpm.casemgmt.impl.CaseRuntimeDataServiceImpl;
import org.jbpm.casemgmt.impl.CaseServiceImpl;
import org.jbpm.casemgmt.impl.admin.CaseInstanceMigrationServiceImpl;
import org.jbpm.casemgmt.impl.event.CaseConfigurationDeploymentListener;
import org.jbpm.casemgmt.impl.generator.TableCaseIdGenerator;
import org.jbpm.kie.services.impl.FormManagerServiceImpl;
import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.ProcessServiceImpl;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.kie.services.impl.UserTaskServiceImpl;
import org.jbpm.kie.services.impl.admin.ProcessInstanceMigrationServiceImpl;
import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.jbpm.kie.services.impl.query.QueryServiceImpl;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.ProcessInstanceMigrationService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.query.QueryService;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.task.TaskService;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.RuntimeStrategy;

public class DefaultCaseServiceConfigurator implements CaseServiceConfigurator {

    protected EntityManagerFactory emf;
    
    protected DeploymentService deploymentService;
    protected DefinitionService bpmn2Service;
    protected RuntimeDataService runtimeDataService;
    protected ProcessService processService;
    protected UserTaskService userTaskService;
    protected QueryService queryService;

    protected CaseRuntimeDataService caseRuntimeDataService;
    protected CaseService caseService;
    protected CaseInstanceMigrationService caseInstanceMigrationService;

    protected ProcessInstanceMigrationService migrationService;

    protected IdentityProvider identityProvider;
    protected CaseIdGenerator caseIdGenerator;

    protected AuthorizationManager authorizationManager;


    @Override
    public void configureServices(String puName, IdentityProvider identityProvider) {
        emf = EntityManagerFactoryManager.get().getOrCreate(puName);
        authorizationManager = new AuthorizationManagerImpl(identityProvider, new TransactionalCommandService(emf));

        // build definition service
        bpmn2Service = new BPMN2DataServiceImpl();

        DeploymentRolesManager deploymentRolesManager = new DeploymentRolesManager();

        queryService = new QueryServiceImpl();
        ((QueryServiceImpl) queryService).setIdentityProvider(identityProvider);
        ((QueryServiceImpl) queryService).setCommandService(new TransactionalCommandService(emf));
        ((QueryServiceImpl) queryService).init();

        // build deployment service
        deploymentService = new KModuleDeploymentService();
        ((KModuleDeploymentService) deploymentService).setBpmn2Service(bpmn2Service);
        ((KModuleDeploymentService) deploymentService).setEmf(emf);
        ((KModuleDeploymentService) deploymentService).setIdentityProvider(identityProvider);
        ((KModuleDeploymentService) deploymentService).setManagerFactory(new RuntimeManagerFactoryImpl());
        ((KModuleDeploymentService) deploymentService).setFormManagerService(new FormManagerServiceImpl());

        TaskService taskService = HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).getTaskService();

        // build runtime data service
        runtimeDataService = new RuntimeDataServiceImpl();
        ((RuntimeDataServiceImpl) runtimeDataService).setCommandService(new TransactionalCommandService(emf));
        ((RuntimeDataServiceImpl) runtimeDataService).setIdentityProvider(identityProvider);
        ((RuntimeDataServiceImpl) runtimeDataService).setTaskService(taskService);
        ((RuntimeDataServiceImpl) runtimeDataService).setDeploymentRolesManager(deploymentRolesManager);
        ((RuntimeDataServiceImpl) runtimeDataService).setTaskAuditService(TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService());
        ((KModuleDeploymentService) deploymentService).setRuntimeDataService(runtimeDataService);

        // build process service
        processService = new ProcessServiceImpl();
        ((ProcessServiceImpl) processService).setDataService(runtimeDataService);
        ((ProcessServiceImpl) processService).setDeploymentService(deploymentService);

        // build user task service
        userTaskService = new UserTaskServiceImpl();
        ((UserTaskServiceImpl) userTaskService).setDataService(runtimeDataService);
        ((UserTaskServiceImpl) userTaskService).setDeploymentService(deploymentService);

        // build case id generator
        caseIdGenerator = new TableCaseIdGenerator(new TransactionalCommandService(emf));

        // build case runtime data service
        caseRuntimeDataService = new CaseRuntimeDataServiceImpl();
        ((CaseRuntimeDataServiceImpl) caseRuntimeDataService).setCaseIdGenerator(caseIdGenerator);
        ((CaseRuntimeDataServiceImpl) caseRuntimeDataService).setRuntimeDataService(runtimeDataService);
        ((CaseRuntimeDataServiceImpl) caseRuntimeDataService).setCommandService(new TransactionalCommandService(emf));
        ((CaseRuntimeDataServiceImpl) caseRuntimeDataService).setIdentityProvider(identityProvider);
        ((CaseRuntimeDataServiceImpl) caseRuntimeDataService).setDeploymentRolesManager(deploymentRolesManager);

        // build case service
        caseService = new CaseServiceImpl();
        ((CaseServiceImpl) caseService).setCaseIdGenerator(caseIdGenerator);
        ((CaseServiceImpl) caseService).setCaseRuntimeDataService(caseRuntimeDataService);
        ((CaseServiceImpl) caseService).setProcessService(processService);
        ((CaseServiceImpl) caseService).setDeploymentService(deploymentService);
        ((CaseServiceImpl) caseService).setRuntimeDataService(runtimeDataService);
        ((CaseServiceImpl) caseService).setCommandService(new TransactionalCommandService(emf));
        ((CaseServiceImpl) caseService).setAuthorizationManager(authorizationManager);
        ((CaseServiceImpl) caseService).setIdentityProvider(identityProvider);

        CaseConfigurationDeploymentListener configurationListener = new CaseConfigurationDeploymentListener(identityProvider);

        // set runtime data service as listener on deployment service
        ((KModuleDeploymentService) deploymentService).addListener((RuntimeDataServiceImpl) runtimeDataService);
        ((KModuleDeploymentService) deploymentService).addListener((BPMN2DataServiceImpl) bpmn2Service);
        ((KModuleDeploymentService) deploymentService).addListener((QueryServiceImpl) queryService);
        ((KModuleDeploymentService) deploymentService).addListener((CaseRuntimeDataServiceImpl) caseRuntimeDataService);
        ((KModuleDeploymentService) deploymentService).addListener(configurationListener);

        // build case instance migration service
        migrationService = new ProcessInstanceMigrationServiceImpl();
        caseInstanceMigrationService = new CaseInstanceMigrationServiceImpl();
        ((CaseInstanceMigrationServiceImpl) caseInstanceMigrationService).setCaseRuntimeDataService(caseRuntimeDataService);
        ((CaseInstanceMigrationServiceImpl) caseInstanceMigrationService).setCommandService(new TransactionalCommandService(emf));
        ((CaseInstanceMigrationServiceImpl) caseInstanceMigrationService).setProcessInstanceMigrationService(migrationService);
        ((CaseInstanceMigrationServiceImpl) caseInstanceMigrationService).setProcessService(processService);
    }    

    @Override
    public void close() {
        emf.close();
    }

    @Override
    public DeploymentService getDeploymentService() {
        return deploymentService;
    }

    @Override
    public DefinitionService getBpmn2Service() {
        return bpmn2Service;
    }

    @Override
    public RuntimeDataService getRuntimeDataService() {
        return runtimeDataService;
    }

    @Override
    public ProcessService getProcessService() {
        return processService;
    }

    @Override
    public UserTaskService getUserTaskService() {
        return userTaskService;
    }

    @Override
    public QueryService getQueryService() {
        return queryService;
    }

    @Override
    public CaseRuntimeDataService getCaseRuntimeDataService() {
        return caseRuntimeDataService;
    }

    @Override
    public CaseService getCaseService() {
        return caseService;
    }

    @Override
    public CaseInstanceMigrationService getCaseInstanceMigrationService() {
        return caseInstanceMigrationService;
    }

    @Override
    public ProcessInstanceMigrationService getMigrationService() {
        return migrationService;
    }

    @Override
    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    @Override
    public CaseIdGenerator getCaseIdGenerator() {
        return caseIdGenerator;
    }

    @Override
    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    @Override
    public DeploymentUnit createDeploymentUnit(String groupId, String artifactid, String version) {
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(groupId, artifactid, version);

        final DeploymentDescriptor descriptor = new DeploymentDescriptorImpl();
        descriptor.getBuilder().addEventListener(new NamedObjectModel(
                "mvel",
                "processIdentity",
                "new org.jbpm.kie.services.impl.IdentityProviderAwareProcessListener(ksession)"
        ));
        deploymentUnit.setDeploymentDescriptor(descriptor);
        deploymentUnit.setStrategy(RuntimeStrategy.PER_CASE);
        
        return deploymentUnit;
    }
    
    public EntityManagerFactory getEmf() {
        return emf;
    }
}
