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
package org.kie.kogito.codegen.tests;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.jbpm.usertask.handler.UserTaskKogitoWorkItemHandlerProcessListener;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.usertask.UserTaskConfig;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTasks;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertEmpty;
import static org.kie.kogito.usertask.impl.lifecycle.DefaultUserTaskLifeCycle.COMPLETE;

public class GatewayIT extends AbstractCodegenIT {

    @Test
    public void testEventBasedGatewayWithData() throws Exception {
        Application app = generateCodeProcessesOnly("gateway/EventBasedSplit.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("EventBasedSplit");

        Model m = p.createModel();

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        processInstance.send(Sig.of("First", "test"));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("x");
        assertThat(result.toMap().get("x")).isEqualTo("test");

        assertEmpty(p.instances());

        // not test the other branch
        processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        processInstance.send(Sig.of("Second", "value"));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("x");
        assertThat(result.toMap().get("x")).isEqualTo("value");

        assertEmpty(p.instances());
    }

    @Test
    public void testMultipleJoin() throws Exception {
        Application app = generateCodeProcessesOnly("gateway/MultipleJoin.bpmn2");
        assertThat(app).isNotNull();
        // we wired user tasks and processes
        app.config().get(UserTaskConfig.class).userTaskEventListeners().listeners().add(new UserTaskKogitoWorkItemHandlerProcessListener(app.get(Processes.class)));

        Process<? extends Model> p = app.get(Processes.class).processById("hiring_join");

        Model m = p.createModel();

        CandidateData data = new CandidateData();
        data.setEmail("candidate@gmail.com");
        data.setExperience(10);
        data.setSkills(List.of("programmer", "soft"));
        data.setName("minor");
        data.setLastName("last name");

        m.update(Map.of("candidateData", data));
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        UserTasks userTasks = app.get(UserTasks.class);

        Awaitility.given().until(() -> {
            return userTasks.instances().findByIdentity(IdentityProviders.of("mary")).size() == 1;
        });

        List<UserTaskInstance> userTaskList = userTasks.instances().findByIdentity(IdentityProviders.of("mary"));
        assertThat(userTaskList).hasSize(1);

        UserTaskInstance userTaskInstance_1 = userTaskList.get(0);
        Offer offer = new Offer();
        offer.setCategory("custom category");
        offer.setSalary(50000);
        userTaskInstance_1.transition(COMPLETE, Map.of("Offer", offer), IdentityProviders.of("mary"));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
    }
}
