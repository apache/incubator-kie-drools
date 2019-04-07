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
import static org.junit.Assert.fail;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.VariableDesc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class ProcessServiceWithServiceRegistryTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);
    private static final String PROCESS_ID_SCRIPT_TASK = "service-registry-test.script-task-test";    
    
    @Parameters(name = "Strategy : {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 {RuntimeStrategy.SINGLETON}, 
                 {RuntimeStrategy.PER_PROCESS_INSTANCE},
                 {RuntimeStrategy.PER_REQUEST},
                 {RuntimeStrategy.PER_CASE}
           });
    }
    
    private RuntimeStrategy strategy;
    private KModuleDeploymentUnit deploymentUnit;
    
    public ProcessServiceWithServiceRegistryTest(RuntimeStrategy strategy) {
        this.strategy = strategy;
        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentUnit.setStrategy(strategy);
    }

    @Before
    public void prepare() {
        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        processes.add("repo/processes/general/signal.bpmn");
        processes.add("repo/processes/ServiceRegistryScriptTask.bpmn2");

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

        deploymentService.deploy(deploymentUnit);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        try {
            deploymentService.undeploy(deploymentUnit);
        } catch (Exception e) {
            // do nothing in case of some failed tests to avoid next test to fail as well
        }
        close();
    }

    @Test
    public void testRunScriptProcessWithServiceRegistryInScriptTask() {
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_ID_SCRIPT_TASK);
        assertNotNull(processInstanceId);
        try {
            ProcessInstance pi = processService.getProcessInstance(processInstanceId);
            if (pi != null) {
                fail("Process should be already completed");
            }
        } catch (ProcessInstanceNotFoundException e) {
          // expected
        }
        
        Collection<VariableDesc> variables = runtimeDataService.getVariableHistory(processInstanceId, "correlationKey", new QueryContext());
        assertNotNull(variables);
        assertEquals(1, variables.size());
        
        VariableDesc ckVar = variables.iterator().next();
        assertNotNull(ckVar);
        assertEquals("1", ckVar.getNewValue());
    }

}
