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

package org.jbpm.test.functional.task;

import org.assertj.core.api.Assertions;
import org.jbpm.services.task.identity.LDAPUserGroupCallbackImpl;
import org.jbpm.test.LdapJbpmTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;

public class HumanTaskWithLDAPTest extends LdapJbpmTestCase {

    private static final String LDAP_HUMAN_TASK = "org/jbpm/test/functional/task/HumanTask-ldap.bpmn2";
    private static final String LDAP_HUMAN_TASK_ID = "org.jbpm.test.functional.task.HumanTask-ldap";
    private static final String LDAP_TASK_LDIF = "src/test/resources/org/jbpm/test/functional/task/HumanTask-task.ldif";

    private KieSession kieSession;
    private TaskService taskService;

    public HumanTaskWithLDAPTest() {
        super(LDAP_TASK_LDIF);
    }

    @Before
    public void init() {
        userGroupCallback = new LDAPUserGroupCallbackImpl(createUserGroupCallbackProperties());
        createRuntimeManager(LDAP_HUMAN_TASK);
        kieSession = getRuntimeEngine().getKieSession();
        taskService = getRuntimeEngine().getTaskService();
    }

    @Test
    public void testCompleteTask() {
        long pid = kieSession.startProcess(LDAP_HUMAN_TASK_ID).getId();

        long taskId = taskService.getTasksByProcessInstanceId(pid).get(0);

        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);

        Status status = taskService.getTaskById(taskId).getTaskData().getStatus();
        Assertions.assertThat(status).isEqualTo(Status.Completed);
    }

}
