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

package org.jbpm.usertask.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jbpm.usertask.jpa.mapper.utils.TestUtils;
import org.jbpm.usertask.jpa.model.TaskProcessInfoEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.UserTaskInstanceRepository;
import org.jbpm.usertask.jpa.repository.UserTaskJPAContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.process.Processes;
import org.kie.kogito.usertask.UserTaskInstance;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractUserTaskInstancesDataIsolationIT {

    public static final String ORDER_PROCESS = "orderProcess";
    public static final String SHIPPING_PROCESS = "shippingProcess";
    public static final String TEST_USER = "testUser";
    public static final String TEST_ROLE = "testRole";
    protected final JPAUserTaskInstances userTaskInstances;
    protected final UserTaskInstanceRepository userTaskInstanceRepository;
    protected final UserTaskJPAContext context;
    protected final Processes processes;

    protected IdentityProvider identityProvider;

    protected AbstractUserTaskInstancesDataIsolationIT(JPAUserTaskInstances userTaskInstances,
            UserTaskInstanceRepository userTaskInstanceRepository,
            UserTaskJPAContext context, Processes processes) {
        this.userTaskInstances = userTaskInstances;
        this.userTaskInstanceRepository = userTaskInstanceRepository;
        this.context = context;
        this.processes = processes;
    }

    @BeforeEach
    public void setup() {
        identityProvider = IdentityProviders.of(TEST_USER, List.of(TEST_ROLE));
        userTaskInstances.setReconnectUserTaskInstance(userTaskInstance -> userTaskInstance);
        userTaskInstances.setDisconnectUserTaskInstance(userTaskInstance -> userTaskInstance);
        userTaskInstanceRepository.findAll().forEach(userTaskInstanceRepository::delete);
    }

    @AfterEach
    public void cleanup() {
        userTaskInstanceRepository.findAll().forEach(userTaskInstanceRepository::delete);
    }

    @Test
    public void testDataIsolationWithProcessesBean() {
        String localProcessId = ORDER_PROCESS;
        userTaskInstanceRepository.persist(createUserTaskEntity("local-process-task-1", localProcessId, processes.processById(localProcessId).version(), TEST_USER));
        userTaskInstanceRepository.persist(createUserTaskEntity("local-process-task-2", localProcessId, processes.processById(localProcessId).version(), TEST_USER));
        userTaskInstanceRepository.persist(createUserTaskEntity("remote-process-task-1", "remoteProcess1", "v1", TEST_USER));
        userTaskInstanceRepository.persist(createUserTaskEntity("remote-process-task-2", "remoteProcess2", "v1", TEST_USER));

        List<UserTaskInstance> tasks = userTaskInstances.findByIdentity(identityProvider);

        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(UserTaskInstance::getId)
                .containsExactlyInAnyOrder("local-process-task-1", "local-process-task-2");
    }

    @Test
    public void testFindByIdFiltersRemoteProcessTasks() {
        String localProcessId = ORDER_PROCESS;

        userTaskInstanceRepository.persist(createUserTaskEntity("findById-localTask", localProcessId, processes.processById(localProcessId).version(), TEST_USER));
        userTaskInstanceRepository.persist(createUserTaskEntity("findById-remoteTask", "remoteProcess", "v1", TEST_USER));

        assertThat(userTaskInstances.findById("findById-localTask")).isPresent();
        assertThat(userTaskInstances.findById("findById-remoteTask")).isEmpty();
    }

    @Test
    public void testExistsFiltersRemoteProcessTasks() {
        String localProcessId = ORDER_PROCESS;

        userTaskInstanceRepository.persist(createUserTaskEntity("exists-localTask", localProcessId, processes.processById(localProcessId).version(), TEST_USER));
        userTaskInstanceRepository.persist(createUserTaskEntity("exists-remoteTask", "remoteProcess", "v1", TEST_USER));

        assertThat(userTaskInstances.exists("exists-localTask")).isTrue();
        assertThat(userTaskInstances.exists("exists-remoteTask")).isFalse();
    }

    @Test
    public void testFindByIdentityWithProcessFiltering() {
        userTaskInstanceRepository.persist(createUserTaskEntity("identity-task-1", ORDER_PROCESS, processes.processById(ORDER_PROCESS).version(), TEST_USER));
        userTaskInstanceRepository.persist(createUserTaskEntity("identity-task-2", SHIPPING_PROCESS, processes.processById(SHIPPING_PROCESS).version(), TEST_USER));

        List<UserTaskInstance> tasks = userTaskInstances.findByIdentity(identityProvider);

        assertThat(tasks).isNotNull();
    }

    @Test
    public void testFindAllFiltersRemoteProcessTasks() {
        String localProcessId = ORDER_PROCESS;

        userTaskInstanceRepository.persist(createUserTaskEntity("findAll-task1", localProcessId, processes.processById(localProcessId).version(), TEST_USER));
        userTaskInstanceRepository.persist(createUserTaskEntity("findAll-task2", localProcessId, processes.processById(localProcessId).version(), TEST_USER));
        userTaskInstanceRepository.persist(createUserTaskEntity("findAll-task3", "remoteProcess", "v1", TEST_USER));

        List<UserTaskInstanceEntity> filteredTasks = userTaskInstanceRepository.findAll();

        assertThat(filteredTasks).hasSize(2);
        assertThat(filteredTasks).extracting(UserTaskInstanceEntity::getId)
                .containsExactlyInAnyOrder("findAll-task1", "findAll-task2");
    }

    @Test
    public void testFilteringWithMultipleProcessIds() {
        Collection<String> localProcessIds = context.getProcesses().processIds();
        assertThat(localProcessIds).hasSizeGreaterThanOrEqualTo(2);

        for (String processId : localProcessIds) {
            userTaskInstanceRepository.persist(createUserTaskEntity("multi-" + processId, processId, processes.processById(processId).version(), TEST_USER));
        }

        assertThat(userTaskInstanceRepository.findAll()).hasSize(localProcessIds.size());
    }

    @Test
    public void testNoResultsWhenProcessIdsDoNotMatch() {
        userTaskInstanceRepository.persist(createUserTaskEntity("no-match-remote-1", "remoteProcess1", "v1", TEST_USER));
        userTaskInstanceRepository.persist(createUserTaskEntity("no-match-remote-2", "remoteProcess2", "v2", TEST_USER));

        List<UserTaskInstance> tasks = userTaskInstances.findByIdentity(identityProvider);

        assertThat(tasks).isEmpty();
    }

    @Test
    public void testFindAllMatchesProcessIdWhenRootProcessIdIsNull() {
        String localProcessId = ORDER_PROCESS;

        userTaskInstanceRepository.persist(createUserTaskEntity("task-null-root", localProcessId, processes.processById(localProcessId).version(), TEST_USER));
        userTaskInstanceRepository.persist(createUserTaskEntity("task-remote-root", "remoteProcess", "some-version", TEST_USER));

        List<UserTaskInstanceEntity> tasks = userTaskInstanceRepository.findAll();

        assertThat(tasks).hasSize(1);
        assertThat(tasks).extracting(UserTaskInstanceEntity::getId)
                .containsExactly("task-null-root");
    }

    protected static Set<String> localProcessIds() {
        return Set.of(ORDER_PROCESS, SHIPPING_PROCESS);
    }

    protected UserTaskInstanceEntity createUserTaskEntity(String taskId, String processId, String processVersion, String actualOwner) {
        return createUserTaskEntity(taskId, processId, processVersion, null, null, actualOwner);
    }

    protected UserTaskInstanceEntity createUserTaskEntity(String taskId, String processId, String processVersion, String rootProcessId, String rootProcessVersion, String actualOwner) {
        UserTaskInstanceEntity entity = TestUtils.createUserTaskInstanceEntity();

        entity.setId(taskId);
        entity.setUserTaskId(taskId);
        entity.setTaskName("Test Task " + taskId);
        entity.setStatus("Ready");
        entity.setActualOwner(actualOwner);

        TaskProcessInfoEntity processInfo = new TaskProcessInfoEntity();
        processInfo.setProcessId(processId);
        processInfo.setProcessVersion(processVersion);
        processInfo.setRootProcessId(rootProcessId);
        processInfo.setRootProcessVersion(rootProcessVersion);
        processInfo.setProcessInstanceId(UUID.randomUUID().toString());
        entity.setProcessInfo(processInfo);

        entity.setPotentialUsers(Set.of(actualOwner));
        entity.setPotentialGroups(Set.of(TEST_ROLE));

        return entity;
    }
}
