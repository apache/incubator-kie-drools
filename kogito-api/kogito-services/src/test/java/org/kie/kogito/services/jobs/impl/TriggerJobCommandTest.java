/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.services.jobs.impl;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.uow.UnitOfWork;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TriggerJobCommandTest {

    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String TIMER_ID = "TIMER:1:8be48533-beed-4c7b-ad85-bd7b543e7925";
    private static final int LIMIT = 1;

    @Mock
    private UnitOfWorkManager unitOfWorkManager;

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private Process<?> process;

    @Mock
    private ProcessInstances<?> instances;

    @Mock
    private ProcessInstance<?> processInstance;

    private TriggerJobCommand command;

    @BeforeEach
    void setUp() {
        command = new TriggerJobCommand(PROCESS_INSTANCE_ID, TIMER_ID, LIMIT, process, unitOfWorkManager);
    }

    @Test
    void executeWhenProcessInstanceNotFound() {
        doReturn(unitOfWork).when(unitOfWorkManager).newUnitOfWork();
        doReturn(instances).when(process).instances();
        doReturn(Optional.empty()).when(instances).findById(PROCESS_INSTANCE_ID);
        assertThat(command.execute()).isFalse();
    }

    @Test
    void executeWhenProcessInstanceFound() {
        doReturn(unitOfWork).when(unitOfWorkManager).newUnitOfWork();
        doReturn(instances).when(process).instances();
        doReturn(Optional.of(processInstance)).when(instances).findById(PROCESS_INSTANCE_ID);
        assertThat(command.execute()).isTrue();
        verify(processInstance).send(any());
    }
}
