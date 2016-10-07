/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.CaseMilestone;
import org.jbpm.casemgmt.api.model.CaseRole;
import org.jbpm.casemgmt.api.model.CaseStage;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.query.QueryContext;
import org.kie.scanner.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CaseRuntimeDataServiceImplTest extends AbstractCaseServicesBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(CaseRuntimeDataServiceImplTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    public static final String SORT_BY_CASE_DEFINITION_NAME = "CaseName";

    @Before
    public void prepare() {
        configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("cases/EmptyCase.bpmn2");
        processes.add("cases/UserTaskCase.bpmn2");
        processes.add("cases/UserTaskWithStageCase.bpmn2");
        // add processes that can be used by cases but are not cases themselves
        processes.add("processes/DataVerificationProcess.bpmn2");
        
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
            
        }
        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
        close();
    }

    @Test
    public void testGetCaseDefinitions() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Collection<CaseDefinition> cases = caseRuntimeDataService.getCases(new QueryContext());
        assertNotNull(cases);
        assertEquals(3, cases.size());

        Map<String, CaseDefinition> mappedCases = mapCases(cases);        
        assertTrue(mappedCases.containsKey("EmptyCase"));
        assertTrue(mappedCases.containsKey("UserTaskCase"));
        assertTrue(mappedCases.containsKey("UserTaskWithStageCase"));

        // EmptyCase asserts
        CaseDefinition caseDef = mappedCases.get("EmptyCase");
        assertNotNull(caseDef);
        assertEquals("EmptyCase", caseDef.getId());
        assertEquals("New Case", caseDef.getName());
        assertEquals("", caseDef.getVersion());
        assertEquals(CaseDefinition.DEFAULT_PREFIX, caseDef.getIdentifierPrefix());
        assertNotNull(caseDef.getCaseMilestones());
        assertTrue(caseDef.getCaseMilestones().isEmpty());
        assertNotNull(caseDef.getCaseStages());
        assertTrue(caseDef.getCaseStages().isEmpty());
        assertNotNull(caseDef.getCaseRoles());
        assertTrue(caseDef.getCaseRoles().isEmpty());
        assertEquals(deploymentUnit.getIdentifier(), caseDef.getDeploymentId());

        // UserTaskCase asserts
        caseDef = mappedCases.get("UserTaskCase");
        assertNotNull(caseDef);
        assertEquals("UserTaskCase", caseDef.getId());
        assertEquals("Simple Case with User Tasks", caseDef.getName());
        assertEquals("1.0", caseDef.getVersion());
        assertEquals("HR", caseDef.getIdentifierPrefix());
        assertNotNull(caseDef.getCaseMilestones());
        assertEquals(2, caseDef.getCaseMilestones().size());

        Map<String, CaseMilestone> mappedMilestones = mapMilestones(caseDef.getCaseMilestones());
        assertTrue(mappedMilestones.containsKey("Milestone1"));
        assertTrue(mappedMilestones.containsKey("Milestone2"));

        CaseMilestone milestone = mappedMilestones.get("Milestone1");
        assertEquals("_SomeID4", milestone.getId());
        assertEquals("Milestone1", milestone.getName());
        assertEquals("", milestone.getAchievementCondition());
        assertEquals(false, milestone.isMandatory());

        milestone = mappedMilestones.get("Milestone2");
        assertEquals("_5", milestone.getId());
        assertEquals("Milestone2", milestone.getName());
        assertEquals("org.kie.api.runtime.process.CaseData(data.get(\"dataComplete\") == true)", milestone.getAchievementCondition());
        assertEquals(false, milestone.isMandatory());

        assertNotNull(caseDef.getCaseStages());
        assertEquals(0, caseDef.getCaseStages().size());

        assertNotNull(caseDef.getCaseRoles());
        assertEquals(3, caseDef.getCaseRoles().size());

        Map<String, CaseRole> mappedRoles = mapRoles(caseDef.getCaseRoles());        
        assertTrue(mappedRoles.containsKey("owner"));
        assertTrue(mappedRoles.containsKey("contact"));
        assertTrue(mappedRoles.containsKey("participant"));

        assertEquals(1, mappedRoles.get("owner").getCardinality().intValue());
        assertEquals(2, mappedRoles.get("contact").getCardinality().intValue());
        assertEquals(-1, mappedRoles.get("participant").getCardinality().intValue());

        // UserTaskWithStageCase asserts
        caseDef = mappedCases.get("UserTaskWithStageCase");
        assertNotNull(caseDef);
        assertEquals("UserTaskWithStageCase", caseDef.getId());
        assertEquals("UserTaskWithStageCase", caseDef.getName());
        assertEquals("1.0", caseDef.getVersion());
        assertEquals(CaseDefinition.DEFAULT_PREFIX, caseDef.getIdentifierPrefix());
        assertNotNull(caseDef.getCaseMilestones());
        assertEquals(0, caseDef.getCaseMilestones().size());
        assertEquals(deploymentUnit.getIdentifier(), caseDef.getDeploymentId());

        assertNotNull(caseDef.getCaseStages());
        assertEquals(1, caseDef.getCaseStages().size());

        Map<String, CaseStage> mappedStages = mapStages(caseDef.getCaseStages());
        assertTrue(mappedStages.containsKey("Collect input"));

        CaseStage caseStage = mappedStages.get("Collect input");
        assertNotNull(caseStage);
        assertEquals("Collect input", caseStage.getName());
        assertEquals(2, caseStage.getAdHocFragments().size());

        Map<String, AdHocFragment> mappedFragments = mapAdHocFragments(caseStage.getAdHocFragments());
        assertTrue(mappedFragments.containsKey("Missing data"));
        assertEquals("HumanTaskNode", mappedFragments.get("Missing data").getType());
        assertTrue(mappedFragments.containsKey("Verification of data"));
        assertEquals("SubProcessNode", mappedFragments.get("Verification of data").getType());


        assertNotNull(caseDef.getCaseRoles());
        assertEquals(3, caseDef.getCaseRoles().size());

        mappedRoles = mapRoles(caseDef.getCaseRoles());        
        assertTrue(mappedRoles.containsKey("owner"));
        assertTrue(mappedRoles.containsKey("contact"));
        assertTrue(mappedRoles.containsKey("participant"));

        assertEquals(1, mappedRoles.get("owner").getCardinality().intValue());
        assertEquals(2, mappedRoles.get("contact").getCardinality().intValue());
        assertEquals(-1, mappedRoles.get("participant").getCardinality().intValue());
    }

    @Test
    public void testGetCaseDefinitionsSorted() {
        assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        Collection<CaseDefinition> cases = caseRuntimeDataService.getCases(new QueryContext(0, 1, SORT_BY_CASE_DEFINITION_NAME, true));
        assertNotNull(cases);
        assertEquals(1, cases.size());
        assertEquals("EmptyCase", cases.iterator().next().getId());

        cases = caseRuntimeDataService.getCases(new QueryContext(1, 1, SORT_BY_CASE_DEFINITION_NAME, true));
        assertNotNull(cases);
        assertEquals(1, cases.size());
        assertEquals("UserTaskCase", cases.iterator().next().getId());

        cases = caseRuntimeDataService.getCases(new QueryContext(2, 1, SORT_BY_CASE_DEFINITION_NAME, true));
        assertNotNull(cases);
        assertEquals(1, cases.size());
        assertEquals("UserTaskWithStageCase", cases.iterator().next().getId());

        cases = caseRuntimeDataService.getCases(new QueryContext(SORT_BY_CASE_DEFINITION_NAME, false));
        assertNotNull(cases);
        assertEquals(3, cases.size());

        List<CaseDefinition> sortedCases = new ArrayList<>(cases);
        assertEquals("UserTaskWithStageCase", sortedCases.get(0).getId());
        assertEquals("UserTaskCase", sortedCases.get(1).getId());
        assertEquals("EmptyCase", sortedCases.get(2).getId());
    }

    @Test
    public void testGetCaseDefinitionsByDeploymentId() {
        assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Collection<CaseDefinition> cases = caseRuntimeDataService.getCasesByDeployment(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(cases);
        assertEquals(3, cases.size());

        Map<String, CaseDefinition> mappedCases = mapCases(cases);        
        assertTrue(mappedCases.containsKey("EmptyCase"));
        assertTrue(mappedCases.containsKey("UserTaskCase"));
        assertTrue(mappedCases.containsKey("UserTaskWithStageCase"));

        cases = caseRuntimeDataService.getCasesByDeployment("not-existing", new QueryContext());
        assertNotNull(cases);        
        assertEquals(0, cases.size());
    }

    @Test
    public void testGetCaseDefinitionsByDeploymentIdSorted() {
        assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        Collection<CaseDefinition> cases = caseRuntimeDataService.getCasesByDeployment(deploymentUnit.getIdentifier(), new QueryContext(0, 1, SORT_BY_CASE_DEFINITION_NAME, true));
        assertNotNull(cases);
        assertEquals(1, cases.size());
        assertEquals("EmptyCase", cases.iterator().next().getId());

        cases = caseRuntimeDataService.getCasesByDeployment(deploymentUnit.getIdentifier(), new QueryContext(1, 1, SORT_BY_CASE_DEFINITION_NAME, true));
        assertNotNull(cases);
        assertEquals(1, cases.size());
        assertEquals("UserTaskCase", cases.iterator().next().getId());

        cases = caseRuntimeDataService.getCasesByDeployment(deploymentUnit.getIdentifier(), new QueryContext(2, 1, SORT_BY_CASE_DEFINITION_NAME, true));
        assertNotNull(cases);
        assertEquals(1, cases.size());
        assertEquals("UserTaskWithStageCase", cases.iterator().next().getId());

        cases = caseRuntimeDataService.getCasesByDeployment(deploymentUnit.getIdentifier(), new QueryContext(SORT_BY_CASE_DEFINITION_NAME, false));
        assertNotNull(cases);
        assertEquals(3, cases.size());

        List<CaseDefinition> sortedCases = new ArrayList<>(cases);
        assertEquals("UserTaskWithStageCase", sortedCases.get(0).getId());
        assertEquals("UserTaskCase", sortedCases.get(1).getId());
        assertEquals("EmptyCase", sortedCases.get(2).getId());
    }

    @Test
    public void testGetCaseDefinitionsByFilter() {
        assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Collection<CaseDefinition> cases = caseRuntimeDataService.getCases("empty", new QueryContext());
        assertNotNull(cases);
        assertEquals(1, cases.size());

        Map<String, CaseDefinition> mappedCases = mapCases(cases);        
        assertTrue(mappedCases.containsKey("EmptyCase"));

        cases = caseRuntimeDataService.getCases("User", new QueryContext());
        assertNotNull(cases);
        assertEquals(2, cases.size());

        mappedCases = mapCases(cases);
        assertTrue(mappedCases.containsKey("UserTaskCase"));
        assertTrue(mappedCases.containsKey("UserTaskWithStageCase"));

        cases = caseRuntimeDataService.getCases("nomatch", new QueryContext());
        assertNotNull(cases);
        assertEquals(0, cases.size());
    }

    @Test
    public void testGetCaseDefinitionsByFilterSorted() {
        assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        Collection<CaseDefinition> cases = caseRuntimeDataService.getCases("User", new QueryContext(0, 1, SORT_BY_CASE_DEFINITION_NAME, true));
        assertNotNull(cases);
        assertEquals(1, cases.size());
        assertEquals("UserTaskCase", cases.iterator().next().getId());

        cases = caseRuntimeDataService.getCases("User", new QueryContext(1, 1, SORT_BY_CASE_DEFINITION_NAME, true));
        assertNotNull(cases);
        assertEquals(1, cases.size());
        assertEquals("UserTaskWithStageCase", cases.iterator().next().getId());

        cases = caseRuntimeDataService.getCases("User", new QueryContext(SORT_BY_CASE_DEFINITION_NAME, false));
        assertNotNull(cases);
        assertEquals(2, cases.size());

        List<CaseDefinition >sortedCases = new ArrayList<>(cases);
        assertEquals("UserTaskWithStageCase", sortedCases.get(0).getId());
        assertEquals("UserTaskCase", sortedCases.get(1).getId());
    }

    @Test
    public void testGetCaseDefinitionById() {
        assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        CaseDefinition caseDef = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(), "UserTaskWithStageCase");
        // UserTaskWithStageCase asserts        
        assertNotNull(caseDef);
        assertEquals("UserTaskWithStageCase", caseDef.getId());
        assertEquals("UserTaskWithStageCase", caseDef.getName());
        assertEquals("1.0", caseDef.getVersion());
        assertEquals(CaseDefinition.DEFAULT_PREFIX, caseDef.getIdentifierPrefix());
        assertNotNull(caseDef.getCaseMilestones());
        assertEquals(0, caseDef.getCaseMilestones().size());
        assertEquals(deploymentUnit.getIdentifier(), caseDef.getDeploymentId());

        assertNotNull(caseDef.getCaseStages());
        assertEquals(1, caseDef.getCaseStages().size());

        Map<String, CaseStage> mappedStages = mapStages(caseDef.getCaseStages());
        assertTrue(mappedStages.containsKey("Collect input"));

        CaseStage caseStage = mappedStages.get("Collect input");
        assertNotNull(caseStage);
        assertEquals("Collect input", caseStage.getName());
        assertEquals(2, caseStage.getAdHocFragments().size());

        Map<String, AdHocFragment> mappedFragments = mapAdHocFragments(caseStage.getAdHocFragments());
        assertTrue(mappedFragments.containsKey("Missing data"));
        assertEquals("HumanTaskNode", mappedFragments.get("Missing data").getType());
        assertTrue(mappedFragments.containsKey("Verification of data"));
        assertEquals("SubProcessNode", mappedFragments.get("Verification of data").getType());

        assertNotNull(caseDef.getCaseRoles());
        assertEquals(3, caseDef.getCaseRoles().size());

        Map<String, CaseRole> mappedRoles = mapRoles(caseDef.getCaseRoles());
        assertTrue(mappedRoles.containsKey("owner"));
        assertTrue(mappedRoles.containsKey("contact"));
        assertTrue(mappedRoles.containsKey("participant"));

        assertEquals(1, mappedRoles.get("owner").getCardinality().intValue());
        assertEquals(2, mappedRoles.get("contact").getCardinality().intValue());
        assertEquals(-1, mappedRoles.get("participant").getCardinality().intValue());
    }


    /*
     * Case instance queries
     */

    @Test
    public void testStartEmptyCaseWithCaseFile() {
        assertNotNull(deploymentService);
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertNotNull(cInstance.getCaseFile());
            assertEquals("my first case", cInstance.getCaseFile().getData("name"));

            Collection<CaseInstance> instances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(instances);

            assertEquals(1, instances.size());
            CaseInstance instance = instances.iterator().next();
            assertNotNull(instance);

            assertEquals(FIRST_CASE_ID, instance.getCaseId());
            assertEquals(EMPTY_CASE_P_ID, instance.getCaseDefinitionId());
            assertEquals("my first case", instance.getCaseDescription());
            assertEquals("testUser", instance.getOwner());
            assertEquals(ProcessInstance.STATE_ACTIVE, instance.getStatus().intValue());
            assertEquals(deploymentUnit.getIdentifier(), instance.getDeploymentId());
            assertNotNull(instance.getStartedAt());

            // add dynamic user task to empty case instance - first by case id
            Map<String, Object> parameters = new HashMap<>();            
            caseService.addDynamicTask(FIRST_CASE_ID, caseService.newHumanTaskSpec("First task", "test", "john", null, parameters));

            Collection<NodeInstanceDesc> activeNodes = caseRuntimeDataService.getActiveNodesForCase(FIRST_CASE_ID, new QueryContext());
            assertNotNull(activeNodes);
            assertEquals(1, activeNodes.size());

            NodeInstanceDesc activeNode = activeNodes.iterator().next();
            assertNotNull(activeNodes);
            assertEquals("[Dynamic] First task", activeNode.getName());

        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
}