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

package org.jbpm.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.executor.ExecutorServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.kie.api.executor.ExecutorService;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

public class JbpmAsyncJobTestCase extends JbpmTestCase {

    private static final int EXECUTOR_THREADS = 4;
    private static final int EXECUTOR_RETRIES = 3;
    private static final int EXECUTOR_INTERVAL = 0;

    private int executorThreads;
    private int executorRetries;
    private int executorInterval;

    private ExecutorService executorService;

    public JbpmAsyncJobTestCase() {
        this(EXECUTOR_THREADS, EXECUTOR_INTERVAL);
    }

    public JbpmAsyncJobTestCase(int executorRetries) {
        this(EXECUTOR_THREADS, executorRetries, EXECUTOR_INTERVAL);
    }

    public JbpmAsyncJobTestCase(int executorThreads, int executorInterval) {
        this(executorThreads, EXECUTOR_RETRIES, executorInterval);
    }

    public JbpmAsyncJobTestCase(int executorThreads, int executorRetries, int executorInterval) {
        super(true, true);

        this.executorThreads = executorThreads;
        this.executorRetries = executorRetries;
        this.executorInterval = executorInterval;
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        executorService = getExecutorService();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        try {
            executorService.clearAllRequests();
            executorService.clearAllErrors();
            executorService.destroy();
        } finally {
            super.tearDown();
        }
    }

    protected ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = ExecutorServiceFactory.newExecutorService(getEmf());
            executorService.setThreadPoolSize(executorThreads);
            executorService.setRetries(executorRetries);
            executorService.setInterval(executorInterval);
            executorService.init();

            logger.debug("Created ExecutorService with parameters: '" + executorThreads + " threads', '"
                    + executorRetries + " retries', interval '" + executorInterval + "s'");
        }
        return executorService;
    }

    public void assertNodeNotTriggered(long processId, String nodeName) {
        boolean triggered = false;
        try {
            assertNodeTriggered(processId, nodeName);
            triggered = true;
        } catch (AssertionError e) {
            // Assertion passed
        }
        if (triggered) {
            Assertions.fail("Node '" + nodeName + "' was triggered.");
        }
    }

    public List<ProcessInstance> startProcess(KieSession kieSession, String processId, int count) {
        List<ProcessInstance> piList = new ArrayList<ProcessInstance>();
        for (int i = 0; i < count; i++) {
            ProcessInstance pi = kieSession.startProcess(processId);
            if (pi != null) {
                piList.add(pi);
            }
        }
        return piList;
    }

    public List<ProcessInstance> startProcess(KieSession kieSession, String processId, Map<String, Object> parameters, int count) {
        List<ProcessInstance> processInstanceList = new ArrayList<ProcessInstance>();
        for (int i = 0; i < count; i++) {
            ProcessInstance processInstance = kieSession.startProcess(processId, parameters);
            processInstanceList.add(processInstance);
        }
        return processInstanceList;
    }

    public void abortProcess(KieSession kieSession, List<ProcessInstance> processInstanceList) {
        for (ProcessInstance processInstance : processInstanceList) {
            abortProcess(kieSession, processInstance.getId());
        }
    }

    public void abortProcess(KieSession kieSession, long pid) {
        ProcessInstance processInstance = kieSession.getProcessInstance(pid);
        if (processInstance != null && processInstance.getState() == ProcessInstance.STATE_ACTIVE) {
            kieSession.abortProcessInstance(pid);
        }
    }

}