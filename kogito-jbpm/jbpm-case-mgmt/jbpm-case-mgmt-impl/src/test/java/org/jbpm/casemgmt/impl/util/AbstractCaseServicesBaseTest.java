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

package org.jbpm.casemgmt.impl.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import javax.persistence.EntityManagerFactory;

import org.dashbuilder.DataSetCore;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.casemgmt.api.CaseNotFoundException;
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
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.jbpm.casemgmt.impl.AuthorizationManagerImpl;
import org.jbpm.casemgmt.impl.CaseRuntimeDataServiceImpl;
import org.jbpm.casemgmt.impl.CaseServiceImpl;
import org.jbpm.casemgmt.impl.admin.CaseInstanceMigrationServiceImpl;
import org.jbpm.casemgmt.impl.event.CaseConfigurationDeploymentListener;
import org.jbpm.casemgmt.impl.generator.TableCaseIdGenerator;
import org.jbpm.casemgmt.impl.marshalling.CaseMarshallerFactory;
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
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.jbpm.services.api.query.QueryService;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.query.QueryContext;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.DeploymentDescriptorBuilder;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public abstract class AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCaseServicesBaseTest.class);

    protected static final String ARTIFACT_ID = "case-module";
    protected static final String GROUP_ID = "org.jbpm.cases";
    protected static final String VERSION = "1.0.0";

    protected PoolingDataSource ds;

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
    
    protected TestIdentityProvider identityProvider;
    protected CaseIdGenerator caseIdGenerator;

    protected AuthorizationManager authorizationManager;

    protected List<String> listenerMvelDefinitions = new ArrayList<>();

    protected static final String EMPTY_CASE_P_ID = "EmptyCase";
    protected static final String USER_TASK_STAGE_CASE_P_ID = "UserTaskWithStageCase";
    protected static final String USER_TASK_CASE_P_ID = "UserTaskCase";
    protected static final String USER_TASK_STAGE_AUTO_START_CASE_P_ID = "UserTaskWithStageCaseAutoStart";
    protected static final String USER_TASK_STAGE_ADHOC_CASE_P_ID = "UserStageAdhocCase";
    protected static final String NO_START_NODE_CASE_P_ID = "NoStartNodeAdhocCase";
    protected static final String COND_CASE_P_ID = "CaseFileConditionalEvent";
    protected static final String TWO_STAGES_CASE_P_ID = "CaseWithTwoStages";
    protected static final String TWO_STAGES_CONDITIONS_CASE_P_ID = "CaseWithTwoStagesConditions";
    protected static final String EXPRESSION_CASE_P_ID = "ExpressionWithCaseFileItem";
    protected static final String USER_TASK_DATA_RESTRICTIONS_CASE_P_ID = "UserTaskCaseDataRestrictions";
    protected static final String MULTI_STAGE_CASE_P_ID = "multiplestages";

    protected static final String SUBPROCESS_P_ID = "DataVerification";

    protected static final String FIRST_CASE_ID = "CASE-0000000001";
    protected static final String HR_CASE_ID = "HR-0000000001";

    protected static final String USER = "john";

    private static final String TEST_DOC_STORAGE = "target/docs";

    protected DeploymentUnit deploymentUnit;

    protected abstract List<String> getProcessDefinitionFiles();

    @Before
    public void setUp() throws Exception {
        prepareDocumentStorage();

        configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = getProcessDefinitionFiles();
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();

        FileOutputStream fs = new FileOutputStream(pom);
        fs.write(getPom(releaseId).getBytes());
        fs.close();

        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
        // use user name who is part of the case roles assignment
        // so (s)he will be authorized to access case instance
        identityProvider.setName(USER);

        deploymentUnit = prepareDeploymentUnit();
    }

    @After
    public void tearDown() {
        clearDocumentStorageProperty();

        List<CaseStatus> caseStatuses = Collections.singletonList(CaseStatus.OPEN);
        caseRuntimeDataService.getCaseInstances(caseStatuses, new QueryContext(0, Integer.MAX_VALUE))
                .forEach(caseInstance -> caseService.cancelCase(caseInstance.getCaseId()));

        identityProvider.reset();
        identityProvider.setRoles(new ArrayList<>());
        cleanupSingletonSessionId();

        if (deploymentUnit != null) {
            deploymentService.undeploy(deploymentUnit);
            deploymentUnit = null;
        }

        close();
        ServiceRegistry.get().clear();
    }

    protected DeploymentUnit prepareDeploymentUnit() {
        assertThat(deploymentService).isNotNull();
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        final DeploymentDescriptor descriptor = new DeploymentDescriptorImpl();
        descriptor.getBuilder().addEventListener(new NamedObjectModel(
                "mvel",
                "processIdentity",
                "new org.jbpm.kie.services.impl.IdentityProviderAwareProcessListener(ksession)"
        ));
        deploymentUnit.setDeploymentDescriptor(descriptor);
        deploymentUnit.setStrategy(RuntimeStrategy.PER_CASE);

        deploymentService.deploy(deploymentUnit);
        return deploymentUnit;
    }

    protected void close() {
        DataSetCore.set(null);
        if (emf != null) {
            emf.close();
        }
        EntityManagerFactoryManager.get().clear();
        closeDataSource();
    }

    private void prepareDocumentStorage() {
        System.setProperty("org.jbpm.document.storage", TEST_DOC_STORAGE);
        deleteFolder(TEST_DOC_STORAGE);
    }

    private void clearDocumentStorageProperty() {
        System.clearProperty("org.jbpm.document.storage");
    }

    protected void configureServices() {
        buildDatasource();
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.domain");
        identityProvider = new TestIdentityProvider();
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

    protected String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" + "  <modelVersion>4.0.0</modelVersion>\n" + "\n" + "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" + "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" + "  <version>" + releaseId.getVersion() + "</version>\n" + "\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += "  <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        return pom;
    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources) {
        return createKieJar(ks, releaseId, resources, null);
    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources, Map<String, String> extraResources) {

        KieFileSystem kfs = createKieFileSystemWithKProject(ks);
        kfs.writePomXML(getPom(releaseId));

        DeploymentDescriptor customDescriptor = createDeploymentDescriptor();

        if (extraResources == null) {
            extraResources = new HashMap<String, String>();
        }
        extraResources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());

        for (String resource : resources) {
            kfs.write("src/main/resources/KBase-test/" + resource, ResourceFactory.newClassPathResource(resource));
        }
        if (extraResources != null) {
            for (Map.Entry<String, String> entry : extraResources.entrySet()) {
                kfs.write(entry.getKey(), ResourceFactory.newByteArrayResource(entry.getValue().getBytes()));
            }
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        if (!kieBuilder.buildAll().getResults().getMessages().isEmpty()) {
            for (Message message : kieBuilder.buildAll().getResults().getMessages()) {
                logger.error("Error Message: ({}) {}", message.getPath(), message.getText());
            }
            throw new RuntimeException("There are errors builing the package, please check your knowledge assets!");
        }

        return (InternalKieModule) kieBuilder.getKieModule();
    }

    protected DeploymentDescriptor createDeploymentDescriptor() {
        //add this listener by default
        listenerMvelDefinitions.add("new org.jbpm.casemgmt.impl.util.TrackingCaseEventListener()");

        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        DeploymentDescriptorBuilder ddBuilder = customDescriptor.getBuilder()
                .runtimeStrategy(RuntimeStrategy.PER_CASE)
                .addMarshalingStrategy(new ObjectModel("mvel", CaseMarshallerFactory.builder().withDoc().toString()))
                .addWorkItemHandler(new NamedObjectModel("mvel", "StartCaseInstance", "new org.jbpm.casemgmt.impl.wih.StartCaseWorkItemHandler(ksession)"));

        listenerMvelDefinitions.forEach(
                listenerDefinition -> ddBuilder.addEventListener(new ObjectModel("mvel", listenerDefinition))
        );

        getProcessListeners().forEach(
                listener -> ddBuilder.addEventListener(listener)
        );
        
        getWorkItemHandlers().forEach(
               listener -> ddBuilder.addWorkItemHandler(listener)
        );


        return customDescriptor;
    }

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage("*").setEqualsBehavior(EqualityBehaviorOption.EQUALITY).setEventProcessingMode(EventProcessingOption.STREAM);

        KieSessionModel ksessionModel = kieBaseModel1.newKieSessionModel("ksession-test");

        ksessionModel.setDefault(true).setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get("realtime"));

        ksessionModel.newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");
        ksessionModel.newWorkItemHandlerModel("Service Task", "new org.jbpm.bpmn2.handler.ServiceTaskHandler(\"name\")");

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }

    protected void buildDatasource() {
        ds = new PoolingDataSource();
        ds.setUniqueName("jdbc/testDS1");

        //NON XA CONFIGS
        ds.setClassName("org.h2.jdbcx.JdbcDataSource");
        ds.getDriverProperties().put("user", "sa");
        ds.getDriverProperties().put("password", "sasa");
        ds.getDriverProperties().put("URL", "jdbc:h2:mem:mydb");

        ds.init();
    }

    protected void closeDataSource() {
        if (ds != null) {
            ds.close();
        }
    }

    public static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {

            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {

                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                logger.debug("Temp dir to be removed {} file {}", tempDir, file);
                new File(tempDir, file).delete();
            }
        }
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

    protected static void waitForTheOtherThreads(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            fail("Thread 1 was interrupted while waiting for the other threads!");
        } catch (BrokenBarrierException e) {
            fail("Thread 1's barrier was broken while waiting for the other threads!");
        }
    }

    protected void deleteFolder(String pathStr) {
        File path = new File(pathStr);
        if (path.exists()) {
            File[] directories = path.listFiles();
            if (directories != null) {
                for (File file : directories) {
                    if (file.isDirectory()) {
                        deleteFolder(file.getAbsolutePath());
                    }
                    file.delete();
                }
            }
        }
    }

    protected List<ObjectModel> getProcessListeners() {
        return new ArrayList<>();
    }
    
    protected List<NamedObjectModel> getWorkItemHandlers() {
        return new ArrayList<>();
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

    protected void assertComment(CommentInstance comment, String author, String content) {
        assertThat(comment).isNotNull();
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getComment()).isEqualTo(content);
    }

    protected void assertTask(TaskSummary task, String actor, String name, Status status) {
        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(name);
        assertThat(task.getActualOwnerId()).isEqualTo(actor);
        assertThat(task.getStatus()).isEqualTo(status);
    }

    protected void assertCaseInstance(String caseId, String name) {
        CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
        assertThat(cInstance).isNotNull();
        assertThat(cInstance.getCaseId()).isEqualTo(caseId);
        assertThat(cInstance.getCaseFile()).isNotNull();
        assertThat(cInstance.getCaseFile().getData("name")).isEqualTo(name);
    }

    public void assertCaseInstanceActive(String caseId) {
        try {
            CaseInstance caseInstance = caseService.getCaseInstance(caseId);
            assertThat(caseInstance).isNotNull();
            assertThat(caseInstance.getStatus()).isEqualTo(CaseStatus.OPEN.getId());
        } catch (CaseNotFoundException ex) {
            fail("Case instance is not active");
        }
    }

    public void assertCaseInstanceNotActive(String caseId) {
        try {
            CaseInstance caseInstance = caseService.getCaseInstance(caseId);
            assertThat(caseInstance).isNotNull();
            assertThat(caseInstance.getStatus()).isIn(CaseStatus.CLOSED.getId(), CaseStatus.CANCELLED.getId());
        } catch (CaseNotFoundException ex) {
            // in case it does not exist at all
        }
        
    }
}
