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

package org.jbpm.kie.services.impl.utils;

import javax.persistence.EntityManagerFactory;

import org.jbpm.kie.services.impl.FormManagerService;
import org.jbpm.kie.services.impl.FormManagerServiceImpl;
import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.ProcessServiceImpl;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.kie.services.impl.UserTaskServiceImpl;
import org.jbpm.kie.services.impl.admin.ProcessInstanceAdminServiceImpl;
import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.jbpm.kie.services.impl.query.QueryServiceImpl;
import org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.ProcessInstanceAdminService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.query.QueryService;
import org.jbpm.services.api.utils.KieServiceConfigurator;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;

public class DefaultKieServiceConfigurator implements KieServiceConfigurator {

    protected EntityManagerFactory emf;
    protected DeploymentService deploymentService;
    protected DefinitionService bpmn2Service;
    protected RuntimeDataService runtimeDataService;
    protected ProcessService processService;
    protected UserTaskService userTaskService;
    protected QueryService queryService;
    protected ProcessInstanceAdminService processAdminService;

    protected IdentityProvider identityProvider;
    protected UserGroupCallback userGroupCallback;

    protected FormManagerService formManagerService;

    @Override
    public void configureServices(String puName, IdentityProvider identityProvider, UserGroupCallback userGroupCallback) {

        emf = EntityManagerFactoryManager.get().getOrCreate(puName);
        this.identityProvider = identityProvider;
        this.userGroupCallback = userGroupCallback;
        formManagerService = new FormManagerServiceImpl();

        // build definition service
        bpmn2Service = new BPMN2DataServiceImpl();

        queryService = new QueryServiceImpl();
        ((QueryServiceImpl) queryService).setIdentityProvider(identityProvider);
        ((QueryServiceImpl) queryService).setUserGroupCallback(userGroupCallback);
        ((QueryServiceImpl) queryService).setCommandService(new TransactionalCommandService(emf));
        ((QueryServiceImpl) queryService).init();

        // build deployment service
        deploymentService = new KModuleDeploymentService();
        ((KModuleDeploymentService) deploymentService).setBpmn2Service(bpmn2Service);
        ((KModuleDeploymentService) deploymentService).setEmf(emf);
        ((KModuleDeploymentService) deploymentService).setIdentityProvider(identityProvider);
        ((KModuleDeploymentService) deploymentService).setManagerFactory(new RuntimeManagerFactoryImpl());
        ((KModuleDeploymentService) deploymentService).setFormManagerService(formManagerService);

        TaskService taskService = HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).getTaskService();

        // build runtime data service
        runtimeDataService = new RuntimeDataServiceImpl();
        ((RuntimeDataServiceImpl) runtimeDataService).setCommandService(new TransactionalCommandService(emf));
        ((RuntimeDataServiceImpl) runtimeDataService).setIdentityProvider(identityProvider);
        ((RuntimeDataServiceImpl) runtimeDataService).setTaskService(taskService);
        ((RuntimeDataServiceImpl) runtimeDataService).setTaskAuditService(TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService());
        ((KModuleDeploymentService) deploymentService).setRuntimeDataService(runtimeDataService);

        // set runtime data service as listener on deployment service
        ((KModuleDeploymentService) deploymentService).addListener(((RuntimeDataServiceImpl) runtimeDataService));
        ((KModuleDeploymentService) deploymentService).addListener(((BPMN2DataServiceImpl) bpmn2Service));
        ((KModuleDeploymentService) deploymentService).addListener(((QueryServiceImpl) queryService));

        // build process service
        processService = new ProcessServiceImpl();
        ((ProcessServiceImpl) processService).setDataService(runtimeDataService);
        ((ProcessServiceImpl) processService).setDeploymentService(deploymentService);

        // build user task service
        userTaskService = new UserTaskServiceImpl();
        ((UserTaskServiceImpl) userTaskService).setDataService(runtimeDataService);
        ((UserTaskServiceImpl) userTaskService).setDeploymentService(deploymentService);

        processAdminService = new ProcessInstanceAdminServiceImpl();
        ((ProcessInstanceAdminServiceImpl) processAdminService).setProcessService(processService);
        ((ProcessInstanceAdminServiceImpl) processAdminService).setRuntimeDataService(runtimeDataService);
        ((ProcessInstanceAdminServiceImpl) processAdminService).setCommandService(new TransactionalCommandService(emf));
        ((ProcessInstanceAdminServiceImpl) processAdminService).setIdentityProvider(identityProvider);
    }

    @Override
    public DeploymentUnit createDeploymentUnit(String groupId, String artifactid, String version) {
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(groupId, artifactid, version);

        return deploymentUnit;
    }

    @Override
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public EntityManagerFactory getEmf() {
        return emf;
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
    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    @Override
    public UserGroupCallback getUserGroupCallback() {
        return userGroupCallback;
    }

    @Override
    public ProcessInstanceAdminService getProcessAdminService() {
        return processAdminService;
    }

    public FormManagerService getFormManagerService() {
        return formManagerService;
    }

}
