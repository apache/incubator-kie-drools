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

package org.jbpm.test.services;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.persistence.EntityManagerFactory;

import org.jbpm.casemgmt.api.CaseRuntimeDataService;
import org.jbpm.casemgmt.api.CaseService;
import org.jbpm.casemgmt.api.admin.CaseInstanceMigrationService;
import org.jbpm.casemgmt.api.auth.AuthorizationManager;
import org.jbpm.casemgmt.api.generator.CaseIdGenerator;
import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.CaseMilestone;
import org.jbpm.casemgmt.api.model.CaseRole;
import org.jbpm.casemgmt.api.model.CaseStage;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.utils.CaseServiceConfigurator;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.ProcessInstanceMigrationService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.jbpm.services.api.query.QueryService;
import org.jbpm.services.api.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryContext;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.DeploymentDescriptorBuilder;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.RuntimeStrategy;

public abstract class AbstractCaseServicesTest extends AbstractServicesTest {

    protected EntityManagerFactory emf;    
    protected DefinitionService bpmn2Service;
    protected RuntimeDataService runtimeDataService;
    protected ProcessService processService;
    protected UserTaskService userTaskService;
    protected QueryService queryService;

    protected CaseRuntimeDataService caseRuntimeDataService;
    protected CaseService caseService;
    protected CaseInstanceMigrationService caseInstanceMigrationService;

    protected ProcessInstanceMigrationService migrationService;

    protected TestIdentityProvider identityProvider;
    protected CaseIdGenerator caseIdGenerator;

    protected AuthorizationManager authorizationManager;

    protected List<String> listenerMvelDefinitions = new ArrayList<>();

    protected DeploymentUnit deploymentUnit;    

    protected CaseServiceConfigurator caseConfigurator;

    public AbstractCaseServicesTest() {
        loadCaseServiceConfigurator();
    }

    protected void loadCaseServiceConfigurator() {
        this.caseConfigurator = ServiceLoader.load(CaseServiceConfigurator.class).iterator().next();
    }

    @Before
    public void setUp() throws Exception {
        prepareDocumentStorage();
        configureServices();
        deploymentUnit = prepareDeploymentUnit();
    }

    @After
    public void tearDown() {
        clearDocumentStorageProperty();        
        List<CaseStatus> caseStatuses = Collections.singletonList(CaseStatus.OPEN);
        caseRuntimeDataService.getCaseInstances(caseStatuses, new QueryContext(0, Integer.MAX_VALUE))
            .forEach(caseInstance -> caseService.cancelCase(caseInstance.getCaseId()));

        cleanupSingletonSessionId();
        identityProvider.reset();
        if (deploymentUnit != null) {
            deploymentService.undeploy(deploymentUnit);
            deploymentUnit = null;
        }

        close();
        ServiceRegistry.get().clear();
    }

    @Override
    protected DeploymentUnit createDeploymentUnit(String groupId, String artifactid, String version) throws Exception {
        return caseConfigurator.createDeploymentUnit(groupId, artifactid, version);
    }

    protected void close() {
        caseConfigurator.close();
        EntityManagerFactoryManager.get().clear();
        closeDataSource();
    }

    protected void configureServices() {
        buildDatasource();
        identityProvider = new TestIdentityProvider();
        caseConfigurator.configureServices("org.jbpm.domain", identityProvider);

        authorizationManager = caseConfigurator.getAuthorizationManager();

        // build definition service
        bpmn2Service = caseConfigurator.getBpmn2Service();

        queryService = caseConfigurator.getQueryService();

        // build deployment service
        deploymentService = caseConfigurator.getDeploymentService();

        // build runtime data service
        runtimeDataService = caseConfigurator.getRuntimeDataService();

        // build process service
        processService = caseConfigurator.getProcessService();

        // build user task service
        userTaskService = caseConfigurator.getUserTaskService();

        // build case id generator
        caseIdGenerator = caseConfigurator.getCaseIdGenerator();

        // build case runtime data service
        caseRuntimeDataService = caseConfigurator.getCaseRuntimeDataService();

        // build case service
        caseService = caseConfigurator.getCaseService();

        // build instance migration service
        migrationService = caseConfigurator.getMigrationService();
        
        // build case instance migration service
        caseInstanceMigrationService = caseConfigurator.getCaseInstanceMigrationService();
    }

    protected DeploymentDescriptor createDeploymentDescriptor() {
        //add this listener by default
        listenerMvelDefinitions.add("new org.jbpm.casemgmt.impl.util.TrackingCaseEventListener()");

        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        DeploymentDescriptorBuilder ddBuilder = customDescriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_CASE).addMarshalingStrategy(new ObjectModel("mvel", "org.jbpm.casemgmt.impl.marshalling.CaseMarshallerFactory.builder().withDoc().get()")).addWorkItemHandler(new NamedObjectModel("mvel", "StartCaseInstance", "new org.jbpm.casemgmt.impl.wih.StartCaseWorkItemHandler(ksession)"));

        listenerMvelDefinitions.forEach(listenerDefinition -> ddBuilder.addEventListener(new ObjectModel("mvel", listenerDefinition)));

        getProcessListeners().forEach(listener -> ddBuilder.addEventListener(listener));

        getWorkItemHandlers().forEach(listener -> ddBuilder.addWorkItemHandler(listener));

        return customDescriptor;
    }

    protected void registerListenerMvelDefinition(String listenerMvelDefinition) {
        this.listenerMvelDefinitions.add(listenerMvelDefinition);
    }

    public void setDeploymentService(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    public void setBpmn2Service(DefinitionService bpmn2Service) {
        this.bpmn2Service = bpmn2Service;
    }

    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        this.runtimeDataService = runtimeDataService;
    }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    public void setUserTaskService(UserTaskService userTaskService) {
        this.userTaskService = userTaskService;
    }

    public void setQueryService(QueryService queryService) {
        this.queryService = queryService;
    }

    public void setIdentityProvider(TestIdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public void setCaseRuntimeDataService(CaseRuntimeDataService caseRuntimeDataService) {
        this.caseRuntimeDataService = caseRuntimeDataService;
    }

    protected Map<String, CaseDefinition> mapCases(Collection<CaseDefinition> cases) {
        return cases.stream().collect(toMap(CaseDefinition::getId, c -> c));
    }

    protected Map<String, CaseRole> mapRoles(Collection<CaseRole> caseRoles) {
        return caseRoles.stream().collect(toMap(CaseRole::getName, c -> c));
    }

    protected Map<String, CaseMilestone> mapMilestones(Collection<CaseMilestone> caseMilestones) {
        return caseMilestones.stream().collect(toMap(CaseMilestone::getName, c -> c));
    }

    protected Map<String, CaseStage> mapStages(Collection<CaseStage> caseStages) {
        return caseStages.stream().collect(toMap(CaseStage::getName, c -> c));
    }

    protected Map<String, UserTaskDefinition> mapTasksDef(Collection<UserTaskDefinition> tasks) {
        return tasks.stream().collect(toMap(UserTaskDefinition::getName, t -> t));
    }

    protected Map<String, AdHocFragment> mapAdHocFragments(Collection<AdHocFragment> adHocFragments) {
        return adHocFragments.stream().collect(toMap(AdHocFragment::getName, t -> t));
    }

    protected Map<String, ProcessDefinition> mapProcesses(Collection<ProcessDefinition> processes) {
        return processes.stream().collect(toMap(ProcessDefinition::getId, p -> p));
    }

    protected Map<String, NodeInstanceDesc> mapNodeInstances(Collection<NodeInstanceDesc> nodes) {
        return nodes.stream().collect(toMap(NodeInstanceDesc::getName, n -> n));
    }

    protected Map<String, TaskSummary> mapTaskSummaries(Collection<TaskSummary> tasks) {
        return tasks.stream().collect(toMap(TaskSummary::getName, t -> t));
    }

    protected Map<Long, ProcessInstanceDesc> mapProcessesInstances(Collection<ProcessInstanceDesc> processes) {
        return processes.stream().collect(toMap(ProcessInstanceDesc::getId, p -> p));
    }

}
