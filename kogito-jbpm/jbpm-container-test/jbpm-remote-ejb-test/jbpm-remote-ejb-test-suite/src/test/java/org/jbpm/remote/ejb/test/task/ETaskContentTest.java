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

package org.jbpm.remote.ejb.test.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.assertj.core.api.Assertions;

import org.jboss.qa.bpms.remote.ejb.domain.MyType;
import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.junit.Test;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

public class ETaskContentTest extends RemoteEjbTest {

    private static final String FIELD_VALUE = "johndoe";

    @Test
    public void testGetTaskContent() {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", FIELD_VALUE);

        ProcessInstance pi = ejb.startAndGetProcess(ProcessDefinitions.HUMAN_TASK_WITH_FORM, params);
        Assertions.assertThat(pi).isNotNull();
        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<Status> statusList = new ArrayList<>();
        statusList.add(Status.Reserved);
        List<TaskSummary> ts = ejb.getTasksByStatusByProcessInstanceId(pi.getId(), statusList);
        Assertions.assertThat(ts.size()).isEqualTo(1);

        TaskSummary taskSummary = ts.get(0);
        Assertions.assertThat(taskSummary.getActualOwner().getId()).isEqualTo(userId);

        Map<String, Object> content = ejb.getTaskInputContentByTaskId(taskSummary.getId());
        for (Entry<String, Object> c : content.entrySet()) {
            System.out.println(c.getKey() + " : " + c.getValue());
        }
        Assertions.assertThat(content.get("ActorId")).isEqualTo(userId);
        Assertions.assertThat((String) content.get("inUserName")).isEqualTo(FIELD_VALUE);
    }

    @Test
    public void testGetTaskContentWithOwnType() {
        MyType myObject = new MyType("my object text");

        Map<String, Object> params = new HashMap<>();
        params.put("myObject", myObject);
        params.put("textContent", "HelloContent");

        ProcessInstance pi = ejb.startAndGetProcess(ProcessDefinitions.HUMAN_TASK_WITH_OWN_TYPE, params);
        Assertions.assertThat(pi).isNotNull();
        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<Long> ts = ejb.getTasksByProcessInstanceId(pi.getId());
        Assertions.assertThat(ts.size()).isEqualTo(1);

        long taskId = ts.get(0);
        ejb.start(taskId, userId);
        Map<String, Object> content = ejb.getTaskInputContentByTaskId(taskId);
        for (Entry<String, Object> c : content.entrySet()) {
            System.out.println(c.getKey() + " : " + c.getValue());
        }
        Assertions.assertThat(((MyType) content.get("inputMyObject")).getText()).isEqualTo(myObject.getText());

    }

}
