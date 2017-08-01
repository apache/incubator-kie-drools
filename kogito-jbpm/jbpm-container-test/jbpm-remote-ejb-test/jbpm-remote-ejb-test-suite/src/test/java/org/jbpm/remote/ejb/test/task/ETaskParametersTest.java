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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;

import org.jboss.qa.bpms.remote.ejb.domain.ListType;
import org.jboss.qa.bpms.remote.ejb.domain.MyType;
import org.jboss.qa.bpms.remote.ejb.domain.NestedType;
import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.services.api.model.VariableDesc;
import org.junit.Test;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;

public class ETaskParametersTest extends RemoteEjbTest {

    private void startProcessAndCompleteTask(String processId, String paramName, Object paramValue, String varName) {
        Map<String, Object> processParams = new HashMap<>();
        processParams.put("userId", userId);

        ProcessInstance pi = ejb.startAndGetProcess(processId, processParams);
        Assertions.assertThat(pi).isNotNull();
        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<Long> ts = ejb.getTasksByProcessInstanceId(pi.getId());
        Assertions.assertThat(ts).hasSize(1);

        long taskId = ts.get(0);
        ejb.start(taskId, userId);

        Map<String, Object> taskParams = new HashMap<>();
        taskParams.put(paramName, paramValue);
        ejb.complete(taskId, userId, taskParams);

        Task task = ejb.getTask(taskId);
        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Completed);

        VariableDesc log = ejb.getVariableHistory(pi.getId(), varName).get(0);
        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getNewValue()).isEqualTo(paramValue.toString());
    }

    @Test
    public void testCompleteTaskWithMyType() {
        MyType myObject = new MyType("my object text");

        startProcessAndCompleteTask(ProcessDefinitions.HUMAN_TASK_WITH_DIFFERENT_TYPES, "outMyObject", myObject, "myObject");
    }

    @Test
    public void testCompleteTaskWithNestedType() {
        MyType myObject = new MyType("my object text");
        NestedType nestedObject = new NestedType("nested object text", myObject);

        startProcessAndCompleteTask(ProcessDefinitions.HUMAN_TASK_WITH_DIFFERENT_TYPES, "outNestedObject", nestedObject, "nestedObject");
    }

    @Test
    public void testCompleteTaskWithListType() {
        MyType myObject1 = new MyType("my object 1");
        MyType myObject2 = new MyType("my object 2");

        List<MyType> myObjects = new ArrayList<>();
        myObjects.add(myObject1);
        myObjects.add(myObject2);

        ListType listObject = new ListType(myObjects);
        startProcessAndCompleteTask(ProcessDefinitions.HUMAN_TASK_WITH_DIFFERENT_TYPES, "outListObject", listObject, "listObject");
    }

    @Test
    public void testMapVariable() {
        MyType myObject1 = new MyType("my object 1");
        MyType myObject2 = new MyType("my object 2");

        Map<String, MyType> mapVariable = new LinkedHashMap<>();
        mapVariable.put("object1", myObject1);
        mapVariable.put("object2", myObject2);

        startProcessAndCompleteTask(ProcessDefinitions.HUMAN_TASK_WITH_DIFFERENT_TYPES, "outMapVariable", mapVariable, "mapVariable");
    }

    @Test
    public void testSetVariable() {
        MyType myObject = new MyType("my object");

        Set<MyType> setVariable = new LinkedHashSet<>();
        setVariable.add(myObject);

        startProcessAndCompleteTask(ProcessDefinitions.HUMAN_TASK_WITH_DIFFERENT_TYPES, "outSetVariable", setVariable, "setVariable");
    }

}
