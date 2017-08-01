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

package org.jbpm.test.container.test.ejbservices.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.test.container.AbstractRuntimeEJBServicesTest;
import org.jbpm.test.container.groups.EAP;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Status;

@Category({EAP.class, WAS.class, WLS.class})
public class ETaskOperationTest extends AbstractRuntimeEJBServicesTest {
    
    @Test
    public void testStartAndComplete() {
        Long processInstanceId = archive.startProcess(kieJar, HUMAN_TASK_PROCESS_ID);
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        Assertions.assertThat(taskIds).isNotNull();
        Assertions.assertThat(taskIds).hasSize(1);
        
        Long taskId = taskIds.get(0);
        
        userTaskService.start(taskId, userId);
        UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getStatus()).isEqualTo(Status.InProgress.toString());
        
        Map<String, Object> results = new HashMap<String, Object>();
        userTaskService.complete(taskId, userId, results);
        task = runtimeDataService.getTaskById(taskId);
        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getStatus()).isEqualTo(Status.Completed.toString());
        
        ProcessInstanceDesc log = runtimeDataService.getProcessInstanceById(processInstanceId);
        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
    
}
