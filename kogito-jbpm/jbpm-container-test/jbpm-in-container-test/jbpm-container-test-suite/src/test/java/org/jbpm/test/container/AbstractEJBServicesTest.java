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

package org.jbpm.test.container;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import javax.ejb.EJB;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jbpm.test.container.archive.EJBService;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEJBServicesTest extends JbpmContainerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEJBServicesTest.class);

    public static final String userId = "ibek";

    protected static EJBService archive = new EJBService();

    @EJB
    protected DeploymentServiceEJBLocal deploymentService;

    @EJB
    protected RuntimeDataServiceEJBLocal runtimeDataService;

    @Before
    public void testEJBs() {
        Assertions.assertThat(runtimeDataService).isNotNull();
        Assertions.assertThat(deploymentService).isNotNull();

        archive.setDeploymentService(deploymentService);
    }

    @Deployment(name = "EJBServices")
    @TargetsContainer(REMOTE_CONTAINER)
    public static Archive<WebArchive> deployEJBTest() throws Exception {
        WebArchive war = archive.buildArchive();

        System.out.println("### Deploying war '" + war + "'");

        return war;

    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        List<DeploymentUnit> units = archive.getUnits();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
    }

    protected static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {
            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                LOGGER.info("Temp dir to be removed {} file {}", tempDir, file);
                new File(tempDir, file).delete();
            }
        }
    }

    // BASIC
    public static final String SCRIPT_TASK_PROCESS_ID = "org.jboss.qa.bpms.ScriptTask";
    public static final String HUMAN_TASK_PROCESS_ID = "org.jboss.qa.bpms.HumanTask";
    public static final String SIGNAL_PROCESS_ID = "org.jboss.qa.bpms.IntermediateSignalProcess";

    // VARIABLE
    public static final String OBJECT_VARIABLE_PROCESS_ID = "org.jboss.qa.bpms.ObjectVariableProcess";

    // SERVICE
    public static final String REST_WORK_ITEM_PROCESS_ID = "org.jboss.qa.bpms.RestWorkItem";

    //EJB COMPLIANCE
    public static final String THREAD_INFO_PROCESS_ID = "org.jboss.qa.bpms.ThreadInfo";

    //MIGRATION
    public static final String EVALUATION_PROCESS_ID_V1 = "definition-project.evaluation";
    public static final String EVALUATION_PROCESS_ID_V2 = "definition-project.evaluation2";

}
