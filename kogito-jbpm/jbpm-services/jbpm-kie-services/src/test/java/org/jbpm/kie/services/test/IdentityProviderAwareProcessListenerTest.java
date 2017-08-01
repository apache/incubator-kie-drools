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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class IdentityProviderAwareProcessListenerTest extends AbstractKieServicesBaseTest {

    private KModuleDeploymentUnit deploymentUnit;

    @Before
    public void init() {
        configureServices();
        final KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        final List<String> processes = Collections.singletonList("repo/processes/general/humanTask.bpmn");

        final InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        final File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try (FileOutputStream fs = new FileOutputStream(pom)) {
            fs.write(getPom(releaseId).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getKieMavenRepository().deployArtifact(releaseId, kJar1, pom);

        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        final DeploymentDescriptor descriptor = new DeploymentDescriptorImpl();
        descriptor.getBuilder().addEventListener(new NamedObjectModel(
                "mvel",
                "processIdentity",
                "new org.jbpm.kie.services.impl.IdentityProviderAwareProcessListener(ksession)"
        ));
        deploymentUnit.setDeploymentDescriptor(descriptor);

        deploymentService.deploy(deploymentUnit);

        final DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processDefinitions = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processDefinitions);
        assertEquals(1, processDefinitions.size());
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (deploymentUnit != null) {
            deploymentService.undeploy(deploymentUnit);
        }
        close();
    }


    @Test
    public void testStartProcessWithIdentityListener() {
        assertNotNull(processService);

        final String userId = "userId";
        identityProvider.setName(userId);

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        try {
            final String initiator = (String) processService.getProcessInstanceVariable(processInstanceId, "initiator");
            assertEquals(userId, initiator);
        } finally {
            processService.abortProcessInstance(processInstanceId);
        }
    }

}