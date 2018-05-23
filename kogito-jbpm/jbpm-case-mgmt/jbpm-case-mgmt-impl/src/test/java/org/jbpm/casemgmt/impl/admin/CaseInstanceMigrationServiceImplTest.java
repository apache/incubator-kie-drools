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

package org.jbpm.casemgmt.impl.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.admin.CaseMigrationReport;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.admin.ProcessInstanceMigrationServiceImpl;
import org.jbpm.services.api.admin.ProcessInstanceMigrationService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaseInstanceMigrationServiceImplTest extends AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CaseInstanceMigrationServiceImplTest.class);
    protected static final String MIGRATION_ARTIFACT_ID = "test-migration";
    protected static final String MIGRATION_GROUP_ID = "org.jbpm.test";
    protected static final String MIGRATION_VERSION_V1 = "1.0.0";
    protected static final String MIGRATION_VERSION_V2 = "2.0.0";
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    private KModuleDeploymentUnit deploymentUnitV1;
    private KModuleDeploymentUnit deploymentUnitV2;
    
    protected ProcessInstanceMigrationService migrationService;
    
    @Before
    public void setUp() throws Exception {
    
        configureServices();
        identityProvider.setName(USER);

        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        
        // version 1 of kjar
        ReleaseId releaseId = ks.newReleaseId(MIGRATION_GROUP_ID, MIGRATION_ARTIFACT_ID, MIGRATION_VERSION_V1);
        List<String> processes = new ArrayList<String>();
        processes.add("migration/v1/UserTaskCase-v1.bpmn2");
        processes.add("migration/v1/UserTaskProcess-v1.bpmn2");
        

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/migration-v1", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, pom);

        // version 2 of kjar
        ReleaseId releaseId2 = ks.newReleaseId(MIGRATION_GROUP_ID, MIGRATION_ARTIFACT_ID, MIGRATION_VERSION_V2);
        processes = new ArrayList<String>();
        processes.add("migration/v2/UserTaskCase-v2.bpmn2");
        processes.add("migration/v2/UserTaskProcess-v2.bpmn2");
        processes.add("migration/v2/UserTaskProcess-v3.bpmn2");
        InternalKieModule kJar2 = createKieJar(ks, releaseId2, processes);
        File pom2 = new File("target/migration-v2", "pom.xml");
        pom2.getParentFile().mkdirs();
        try {
            FileOutputStream fs = new FileOutputStream(pom2);
            fs.write(getPom(releaseId2).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        repository = getKieMavenRepository();
        repository.installArtifact(releaseId2, kJar2, pom2);
        
        migrationService = new ProcessInstanceMigrationServiceImpl();
        
        // now let's deploy to runtime both kjars
        deploymentUnitV1 = new KModuleDeploymentUnit(MIGRATION_GROUP_ID, MIGRATION_ARTIFACT_ID, MIGRATION_VERSION_V1);
        deploymentService.deploy(deploymentUnitV1);
        units.add(deploymentUnitV1);
        
        deploymentUnitV2 = new KModuleDeploymentUnit(MIGRATION_GROUP_ID, MIGRATION_ARTIFACT_ID, MIGRATION_VERSION_V2);
        deploymentService.deploy(deploymentUnitV2);
        units.add(deploymentUnitV2);
    }

    @After
    public void cleanup() {
        
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                try {
                deploymentService.undeploy(unit);
                } catch (Exception e) {
                    // do nothing in case of some failed tests to avoid next test to fail as well
                }
            }
            units.clear();
        }        
    }

    private static final String CASE_DEF_ID_V1 = "UserTaskCase_V1";
    private static final String CASE_DEF_ID_V2 = "UserTaskCase_V2";
    
    private static final String PROCESS_DEF_ID_V1 = "UserTask_V1";
    private static final String PROCESS_DEF_ID_V2 = "UserTask_V2";
    private static final String PROCESS_DEF_ID_V3 = "UserTask_V3";
    
    @Test
    public void testMigrateSingleCaseInstance() {
        
        String caseId = createCaseInstanceV1();
        try {
            assertCaseInstance(caseId, deploymentUnitV1.getIdentifier(), CASE_DEF_ID_V1);
            
            Map<String, String> processMapping = new HashMap<>();
            processMapping.put(CASE_DEF_ID_V1, CASE_DEF_ID_V2);
            
            CaseMigrationReport report = caseInstanceMigrationService.migrate(caseId, deploymentUnitV2.getIdentifier(), processMapping);
            assertNotNull(report);
            assertThat(report.isSuccessful()).isTrue();
            assertThat(report.getReports()).hasSize(1);
            assertCaseInstance(caseId, deploymentUnitV2.getIdentifier(), CASE_DEF_ID_V2);
            
            caseService.destroyCase(caseId);
            CaseInstance instance = caseService.getCaseInstance(caseId);
            Assertions.assertThat(instance.getStatus()).isEqualTo(CaseStatus.CANCELLED.getId());
            caseId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testMigrateSingleCaseInstanceMissingMapping() {
        
        String caseId = createCaseInstanceV1();
        try {
            assertCaseInstance(caseId, deploymentUnitV1.getIdentifier(), CASE_DEF_ID_V1);
            
            Map<String, String> processMapping = new HashMap<>();
            final String caseIdFinal = caseId;
            assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> { 
                caseInstanceMigrationService.migrate(caseIdFinal, deploymentUnitV2.getIdentifier(), processMapping); })
            .withMessageContaining("Not possible to migrate case instance " + caseId+ " due to missing process mapping");
            
            
            assertCaseInstance(caseId, deploymentUnitV1.getIdentifier(), CASE_DEF_ID_V1);
            
            caseService.destroyCase(caseId);
            CaseInstance instance = caseService.getCaseInstance(caseId);
            Assertions.assertThat(instance.getStatus()).isEqualTo(CaseStatus.CANCELLED.getId());
            caseId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testMigrateSingleCaseInstanceNotExisting() {
                
        assertThatExceptionOfType(CaseNotFoundException.class).isThrownBy(() -> { 
            caseInstanceMigrationService.migrate("not-existing", deploymentUnitV2.getIdentifier(), new HashMap<>()); })
        .withMessageContaining("Case not-existing does not exist or is not active, cannot be migrated"); 
    }
    
    @Test
    public void testMigrateSingleCaseInstanceWithDynamicSubprocess() {
        
        String caseId = createCaseInstanceV1();
        try {
            assertCaseInstance(caseId, deploymentUnitV1.getIdentifier(), CASE_DEF_ID_V1);
            
            caseService.addDynamicSubprocess(caseId, PROCESS_DEF_ID_V1, null);
            
            Collection<ProcessInstanceDesc> pInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertEquals(2, pInstances.size());
            
            for (ProcessInstanceDesc instance : pInstances) {
                assertEquals(deploymentUnitV1.getIdentifier(), instance.getDeploymentId());                
            }
            
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
            assertNotNull(tasks);
            assertThat(tasks).hasSize(2);
            
            Map<String, TaskSummary> mappedTasks = mapTaskSummaries(tasks);
            assertThat(mappedTasks).containsKeys("Hello1", "Hello_V1");
            
            Map<String, String> processMapping = new HashMap<>();
            processMapping.put(CASE_DEF_ID_V1, CASE_DEF_ID_V2);
            processMapping.put(PROCESS_DEF_ID_V1, PROCESS_DEF_ID_V2);
            
            CaseMigrationReport report = caseInstanceMigrationService.migrate(caseId, deploymentUnitV2.getIdentifier(), processMapping);
            assertNotNull(report);
            assertThat(report.isSuccessful()).isTrue();
            assertThat(report.getReports()).hasSize(2);
            
            assertCaseInstance(caseId, deploymentUnitV2.getIdentifier(), CASE_DEF_ID_V2);
            
            pInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertEquals(2, pInstances.size());
            
            for (ProcessInstanceDesc instance : pInstances) {
                assertEquals(deploymentUnitV2.getIdentifier(), instance.getDeploymentId());                
            }
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
            assertNotNull(tasks);
            assertThat(tasks).hasSize(2);
            
            mappedTasks = mapTaskSummaries(tasks);
            assertThat(mappedTasks).containsKeys("Hello1", "Hello_V2");
            
            userTaskService.completeAutoProgress(mappedTasks.get("Hello_V2").getId(), USER, null);
            
            caseService.destroyCase(caseId);
            CaseInstance instance = caseService.getCaseInstance(caseId);
            Assertions.assertThat(instance.getStatus()).isEqualTo(CaseStatus.CANCELLED.getId());
            caseId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testMigrateSingleCaseInstanceWithDynamicSubprocessNodeMapping() {
        
        String caseId = createCaseInstanceV1();
        try {
            assertCaseInstance(caseId, deploymentUnitV1.getIdentifier(), CASE_DEF_ID_V1);
            
            caseService.addDynamicSubprocess(caseId, PROCESS_DEF_ID_V1, null);
            
            Collection<ProcessInstanceDesc> pInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertEquals(2, pInstances.size());
            
            for (ProcessInstanceDesc instance : pInstances) {
                assertEquals(deploymentUnitV1.getIdentifier(), instance.getDeploymentId());                
            }
            
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
            assertNotNull(tasks);
            assertThat(tasks).hasSize(2);
            
            Map<String, TaskSummary> mappedTasks = mapTaskSummaries(tasks);
            assertThat(mappedTasks).containsKeys("Hello1", "Hello_V1");
            
            Map<String, String> processMapping = new HashMap<>();
            processMapping.put(CASE_DEF_ID_V1, CASE_DEF_ID_V2);
            processMapping.put(PROCESS_DEF_ID_V1, PROCESS_DEF_ID_V3);
            
            Map<String, String> nodeMapping = new HashMap<>();
            nodeMapping.put("_22", "_33");
            
            CaseMigrationReport report = caseInstanceMigrationService.migrate(caseId, deploymentUnitV2.getIdentifier(), processMapping, nodeMapping);
            assertNotNull(report);
            assertThat(report.isSuccessful()).isTrue();
            assertThat(report.getReports()).hasSize(2);
            
            assertCaseInstance(caseId, deploymentUnitV2.getIdentifier(), CASE_DEF_ID_V2);
            
            pInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertEquals(2, pInstances.size());
            
            for (ProcessInstanceDesc instance : pInstances) {
                assertEquals(deploymentUnitV2.getIdentifier(), instance.getDeploymentId());                
            }
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
            assertNotNull(tasks);
            assertThat(tasks).hasSize(2);
            
            mappedTasks = mapTaskSummaries(tasks);
            assertThat(mappedTasks).containsKeys("Hello1", "Hello_V3");
            
            userTaskService.completeAutoProgress(mappedTasks.get("Hello_V3").getId(), USER, null);
            
            caseService.destroyCase(caseId);
            CaseInstance instance = caseService.getCaseInstance(caseId);
            Assertions.assertThat(instance.getStatus()).isEqualTo(CaseStatus.CANCELLED.getId());
            caseId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testMigrateSingleCaseInstanceWithDynamicSubprocessRevert() {
        
        String caseId = createCaseInstanceV1();
        try {
            assertCaseInstance(caseId, deploymentUnitV1.getIdentifier(), CASE_DEF_ID_V1);
            
            caseService.addDynamicSubprocess(caseId, PROCESS_DEF_ID_V1, null);
            
            Collection<ProcessInstanceDesc> pInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertEquals(2, pInstances.size());
            
            for (ProcessInstanceDesc instance : pInstances) {
                assertEquals(deploymentUnitV1.getIdentifier(), instance.getDeploymentId());                
            }
            
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
            assertNotNull(tasks);
            assertThat(tasks).hasSize(2);
            
            Map<String, TaskSummary> mappedTasks = mapTaskSummaries(tasks);
            assertThat(mappedTasks).containsKeys("Hello1", "Hello_V1");
            
            Map<String, String> processMapping = new HashMap<>();
            processMapping.put(CASE_DEF_ID_V1, CASE_DEF_ID_V2);
            processMapping.put(PROCESS_DEF_ID_V1, PROCESS_DEF_ID_V3);
            
            // explicitly without generic to cause error (class cast) in migration process to test revert of case instance migration
            Map erronousMapping = Collections.singletonMap("_22", 2); 
            
            CaseMigrationReport report = caseInstanceMigrationService.migrate(caseId, deploymentUnitV2.getIdentifier(), processMapping, erronousMapping);
            assertNotNull(report);
            assertThat(report.isSuccessful()).isFalse();
            assertThat(report.getReports()).hasSize(2);
            
            assertCaseInstance(caseId, deploymentUnitV1.getIdentifier(), CASE_DEF_ID_V1);
            
            pInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertEquals(2, pInstances.size());
            
            for (ProcessInstanceDesc instance : pInstances) {
                assertEquals(deploymentUnitV1.getIdentifier(), instance.getDeploymentId());                
            }
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
            assertNotNull(tasks);
            assertThat(tasks).hasSize(2);
            
            mappedTasks = mapTaskSummaries(tasks);
            assertThat(mappedTasks).containsKeys("Hello1", "Hello_V1");
            
            userTaskService.completeAutoProgress(mappedTasks.get("Hello_V1").getId(), USER, null);
            
            caseService.destroyCase(caseId);
            CaseInstance instance = caseService.getCaseInstance(caseId);
            Assertions.assertThat(instance.getStatus()).isEqualTo(CaseStatus.CANCELLED.getId());
            caseId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    /*
     * Helper methods
     */
    
    protected String createCaseInstanceV1() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl(USER));

        Map<String, Object> data = new HashMap<>();
        data.put("s", "case description");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnitV1.getIdentifier(), CASE_DEF_ID_V1, data, roleAssignments);
               
        String caseId = caseService.startCase(deploymentUnitV1.getIdentifier(), CASE_DEF_ID_V1, caseFile);
        assertNotNull(caseId);
        assertEquals(HR_CASE_ID, caseId);
        
        return caseId;
    }
    
    protected void assertCaseInstance(String caseId, String deploymentId, String definitionId) {
        CaseInstance cInstance = caseService.getCaseInstance(caseId);
        assertNotNull(cInstance);
        assertEquals(deploymentId, cInstance.getDeploymentId());
        assertEquals(definitionId, cInstance.getCaseDefinitionId());
        
        CaseFileInstance caseFileInstance = caseService.getCaseFileInstance(caseId);
        assertNotNull(caseFileInstance);
        assertEquals(definitionId, caseFileInstance.getDefinitionId());
    }

    @Override
    protected List<String> getProcessDefinitionFiles() {
        return null;
    }
}
