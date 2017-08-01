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

package org.jbpm.remote.ejb.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.remote.ejb.test.client.EJBClient;
import org.jbpm.remote.ejb.test.maven.MavenProject;
import org.jbpm.remote.ejb.test.users.Users;
import org.jbpm.services.api.model.ProcessInstanceDesc;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.query.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteEjbTest {

    public static final String userId = System.getProperty("userId", Users.USER_EJB.getUsername());
    public static final String SESSION_STRATEGY = "PER_PROCESS_INSTANCE";
    protected static final Logger logger = LoggerFactory.getLogger(RemoteEjbTest.class);

    protected static EJBClient ejb;

    @Rule
    public TestRule watcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            logger.info(" >>> " + description.getMethodName() + " <<< ");
        }

        @Override
        protected void finished(Description description) {
            logger.info(System.lineSeparator());
        }
    };

    @BeforeClass
    public static void setUpClass() throws Exception {
        MavenProject kjar = TestKjars.INTEGRATION;
        ejb = new EJBClient(kjar.getGav());
        if (!ejb.isDeployed(kjar.getGav())) {
            deployKjar(kjar);
        }
    }

    protected static void deployKjar(MavenProject kjar) {
        ejb.deploy(kjar.getGroupId(), kjar.getArtifactId(), kjar.getVersion(), null, null, SESSION_STRATEGY);
    }

    protected static void abortAllProcesses() {
        List<Integer> stateList = new ArrayList<>();

        stateList.add(ProcessInstance.STATE_ACTIVE);
        QueryContext context = new QueryContext(0, Integer.MAX_VALUE, "log.status", true);

        List<ProcessInstanceDesc> instanceDescList = ejb.getProcessInstances(stateList, null, context);
        logger.info("Cleaning up ...");
        logger.info("\tFound '" + instanceDescList.size() + "' active instances.");
        for (ProcessInstanceDesc instanceDesc : instanceDescList) {
            logger.info("\tAborting process instance with id '" + instanceDesc.getId() + "' of type '" +
                    instanceDesc.getProcessId() + "'");
            ejb.abortProcessInstance(instanceDesc.getId());
        }
    }

    @Before
    public void cleanUp() {
        abortAllProcesses();
    }

    public long startProcess(String definitionId) {
        return ejb.startProcessSimple(definitionId);
    }

    public List<Long> startProcess(String processId, int count) {
        List<Long> pidList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Long pid = ejb.startProcess(processId);
            if (pid != null) {
                pidList.add(pid);
            }

            sleep(500);
        }
        return pidList;
    }

    public List<Long> startProcess(String processId, Map<String, Object> parameters, int count) {
        List<Long> processInstanceList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Long processInstance = ejb.startProcess(processId, parameters);
            processInstanceList.add(processInstance);

            sleep(500);
        }
        return processInstanceList;
    }

    protected static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.error("Caught interrupted exception.", e);
        }
    }
}
