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

package org.jbpm.kie.services.impl.form;

import java.util.Collections;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.task.commands.GetUserTaskCommand;
import org.jbpm.services.task.impl.model.TaskDataImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.task.TaskService;
import org.kie.internal.identity.IdentityProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FormProviderServiceImplTest {

    @Mock
    TaskService taskService;

    @Mock
    RuntimeDataService dataService;

    @Mock
    FormProvider formProvider;

    @Mock
    IdentityProvider identityProvider;

    @Mock
    DefinitionService bpmn2Service;

    @InjectMocks
    FormProviderServiceImpl formProviderService;

    @Before
    public void init() {
        formProviderService.setProviders(Collections.singleton(formProvider));
        formProviderService.setIdentityProvider(identityProvider);
        when(identityProvider.getName()).thenReturn("admin");
    }

    @Test
    public void testGetFormDisplayTaskWithoutProcess() {
        long taskId = 1;
        final TaskImpl task = new TaskImpl();
        task.setId(taskId);
        task.setName("TaskName");
        task.setTaskData(new TaskDataImpl());
        when(taskService.execute(any(GetUserTaskCommand.class))).thenReturn(task);

        final String form = formProviderService.getFormDisplayTask(1);

        assertEquals("", form);
        verify(dataService, never()).getProcessesByDeploymentIdProcessId(anyString(), anyString());
        verify(formProvider).render(eq(task.getName()), eq(task), isNull(ProcessDefinition.class), anyMap());
    }

    @Test
    public void testGetFormDisplayTask() {
        long taskId = 1;
        final TaskImpl task = new TaskImpl();
        task.setId(taskId);
        task.setName("TaskName");
        final TaskDataImpl taskData = new TaskDataImpl();
        final String deploymentId = "org.jbpm";
        taskData.setDeploymentId(deploymentId);
        final String processId = "org.jbpm.evaluation";
        taskData.setProcessId(processId);
        task.setTaskData(taskData);
        when(taskService.execute(any(GetUserTaskCommand.class))).thenReturn(task);
        final ProcessDefinition processDefinition = mock(ProcessDefinition.class);
        when(dataService.getProcessesByDeploymentIdProcessId(deploymentId, processId)).thenReturn(processDefinition);

        final String form = formProviderService.getFormDisplayTask(1);

        assertEquals("", form);
        verify(dataService).getProcessesByDeploymentIdProcessId(deploymentId, processId);
        verify(formProvider).render(eq(task.getName()), eq(task), eq(processDefinition), anyMap());
    }

}
