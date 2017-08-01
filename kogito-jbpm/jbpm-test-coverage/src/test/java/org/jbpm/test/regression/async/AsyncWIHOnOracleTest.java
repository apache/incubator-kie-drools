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

package org.jbpm.test.regression.async;

import org.assertj.core.api.Assertions;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandler;
import org.jbpm.persistence.jpa.hibernate.DisabledFollowOnLockOracle10gDialect;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.test.JbpmAsyncJobTestCase;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.junit.After;
import org.junit.Test;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;
import qa.tools.ikeeper.annotation.BZ;

public class AsyncWIHOnOracleTest extends JbpmAsyncJobTestCase {

    public static final String PROCESS = "org/jbpm/test/regression/async/AsyncWIHOnOracle.bpmn2";
    public static final String PROCESS_ID = "org.jbpm.test.regression.async.AsyncWIHOnOracle";

    private static final int EXECUTOR_THREADS = 2;
    private static final int EXECUTOR_RETRIES = 2;
    private static final int EXECUTOR_INTERVAL = 1;

    public AsyncWIHOnOracleTest() {
        super(EXECUTOR_THREADS, EXECUTOR_RETRIES, EXECUTOR_INTERVAL);
    }

    @Override
    public void setUp() throws Exception {
        String driverClassName = PersistenceUtil.getDatasourceProperties().getProperty("driverClassName");
        if (driverClassName != null && driverClassName.contains("Oracle")) {
            String hibernateDialect = DisabledFollowOnLockOracle10gDialect.class.getName();
            setPersistenceProperty("hibernate.dialect", hibernateDialect);
            logger.info("Using hibernate.dialect=" + hibernateDialect);
        }
        super.setUp();
    }

    @Test
    @BZ("1234592")
    public void testAsyncWIHExecutedMoreThanOnceOnOracle() throws Exception {
        KieSession ksession = createKSession(PROCESS);

        TrackingProcessEventListener tpel = new TrackingProcessEventListener();
        ksession.addEventListener(tpel);

        WorkItemHandler wih = new AsyncWorkItemHandler(getExecutorService(), CounterCommand.class.getName());
        ksession.getWorkItemManager().registerWorkItemHandler("async", wih);

        ksession.startProcess(PROCESS_ID);

        boolean completed = tpel.waitForProcessToComplete(10000);
        Assertions.assertThat(completed).as("The process should have finished in 10s").isTrue();

        Assertions.assertThat(CounterCommand.getCounter()).as("The job has not been executed").isNotEqualTo(0);
        Assertions.assertThat(CounterCommand.getCounter()).as("The job has been executed multiple times").isEqualTo(1);
    }

    @Override
    @After
    public void tearDown() {
        try {
            super.tearDown();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static class CounterCommand implements Command {

        private static int counter = 0;

        @Override
        public ExecutionResults execute(CommandContext commandContext) throws Exception {
            ++counter;
            return new ExecutionResults();
        }

        public static int getCounter() {
            return counter;
        }

    }

}
