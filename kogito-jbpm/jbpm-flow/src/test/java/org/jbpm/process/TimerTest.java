/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.process;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.kiesession.session.ProcessRuntimeFactory;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.services.jobs.impl.LegacyInMemoryJobService;
import org.kie.kogito.services.uow.StaticUnitOfWorkManger;
import org.kie.kogito.timer.TimerInstance;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.jbpm.workflow.instance.node.TimerNodeInstance.TIMER_TRIGGERED_EVENT;

public class TimerTest extends AbstractBaseTest {

    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    private int counter = 0;

    static {
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
    }

    @Test
    @Timeout(value = 10L, unit = TimeUnit.SECONDS)
    public void testTimer() throws Exception {
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        try (LegacyInMemoryJobService jobService = new LegacyInMemoryJobService(kruntime, StaticUnitOfWorkManger.staticUnitOfWorkManager());) {
            RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance() {
                private static final long serialVersionUID = 510l;

                public void signalEvent(String type, Object event) {
                    if (TIMER_TRIGGERED_EVENT.equals(type)) {
                        TimerInstance timer = (TimerInstance) event;
                        logger.info("Timer {} triggered", timer.getId());
                        counter++;
                    }
                }
            };
            processInstance.setKnowledgeRuntime(((InternalWorkingMemory) kruntime.getKieSession()).getKnowledgeRuntime());
            processInstance.setId("1234");
            InternalProcessRuntime processRuntime = ((InternalProcessRuntime) ((InternalWorkingMemory) kruntime.getKieSession()).getProcessRuntime());
            processRuntime.getProcessInstanceManager().internalAddProcessInstance(processInstance);

            new Thread(() -> kruntime.getKieSession().fireUntilHalt()).start();

            ProcessInstanceJobDescription desc = ProcessInstanceJobDescription.newProcessInstanceJobDescriptionBuilder()
                    .expirationTime(ExactExpirationTime.now())
                    .processInstanceId(processInstance.getStringId())
                    .processId("test")
                    .id("job1")
                    .timerId("timer1")
                    .build();
            jobService.scheduleJob(desc);

            await().atMost(Duration.ofSeconds(5L)).until(() -> counter == 1);
            assertThat(counter).isEqualTo(1);

            counter = 0;
            desc = ProcessInstanceJobDescription.newProcessInstanceJobDescriptionBuilder()
                    .expirationTime(DurationExpirationTime.after(500))
                    .processInstanceId(processInstance.getStringId())
                    .processId("test")
                    .id("job2")
                    .timerId("timer2")
                    .build();
            jobService.scheduleJob(desc);
            assertThat(counter).isZero();
            await().atMost(Duration.ofSeconds(5L)).until(() -> counter == 1);
            assertThat(counter).isEqualTo(1);

            counter = 0;
            desc = ProcessInstanceJobDescription.newProcessInstanceJobDescriptionBuilder()
                    .expirationTime(DurationExpirationTime.repeat(500, 300L))
                    .processInstanceId(processInstance.getStringId())
                    .processId("test")
                    .id("job3")
                    .timerId("timer3")
                    .build();
            String jobId = jobService.scheduleJob(desc);
            assertThat(counter).isZero();

            await().atMost(Duration.ofSeconds(5L)).until(() -> counter == 1);
            assertThat(counter).isEqualTo(1);

            await().atMost(Duration.ofSeconds(5L)).until(() -> counter == 4);
            assertThat(counter).isEqualTo(4);

            jobService.cancelJob(jobId);
            int lastCount = counter;
            // value is preserved
            await().during(Duration.ofSeconds(2L)).until(() -> counter == lastCount);

        }
    }

}
