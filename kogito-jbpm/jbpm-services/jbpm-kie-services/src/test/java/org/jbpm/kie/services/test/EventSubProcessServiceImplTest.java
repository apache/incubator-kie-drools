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

package org.jbpm.kie.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSubProcessServiceImplTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(EventSubProcessServiceImplTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @Before
    public void prepare() {
        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/BPMN2-EventSubprocessSignal.bpmn2");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
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
        close();
    }

    @Test
    public void testStartProcessWithEventSubprocessAndGetWorkItems() {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        assertTrue(isDeployed);

        assertNotNull(processService);

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "Telesure.Telesure");
        assertNotNull(processInstanceId);

        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        assertNotNull(pi);

        Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());
        assertEquals("customer", activeNodes.iterator().next().getName());

        List<WorkItem> wis = processService.getWorkItemByProcessInstance(processInstanceId);
        assertNotNull(wis);
        assertEquals(1, wis.size());
        assertEquals("Human Task", wis.get(0).getName());
        assertEquals("customer", wis.get(0).getParameter("NodeName"));

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(1, tasks.size());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("towing", true);
        params.put("taxi", true);
        params.put("emergency", false);
        userTaskService.completeAutoProgress(tasks.get(0).getId(), "john", params);

        activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
        assertNotNull(activeNodes);
        assertEquals(2, activeNodes.size());
        Iterator<NodeInstanceDesc> it = activeNodes.iterator();
        assertEquals("taxiUser", it.next().getName());
        assertEquals("towingUser", it.next().getName());

        wis = processService.getWorkItemByProcessInstance(processInstanceId);
        assertNotNull(wis);
        assertEquals(2, wis.size());
        assertEquals("Human Task", wis.get(0).getName());
        assertEquals("taxiUser", wis.get(0).getParameter("NodeName"));

        assertEquals("Human Task", wis.get(1).getName());
        assertEquals("towingUser", wis.get(1).getParameter("NodeName"));

        processService.abortProcessInstance(processInstanceId);

        pi = processService.getProcessInstance(processInstanceId);
        assertNull(pi);
    }

}
