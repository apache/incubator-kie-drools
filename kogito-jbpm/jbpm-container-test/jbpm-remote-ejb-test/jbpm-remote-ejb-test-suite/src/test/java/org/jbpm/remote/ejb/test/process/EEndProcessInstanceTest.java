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

package org.jbpm.remote.ejb.test.process;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.Test;

import org.kie.api.runtime.process.ProcessInstance;

public class EEndProcessInstanceTest extends RemoteEjbTest {

    @Test()
    public void testAbortProcessInstance() {
        long pid = ejb.startProcessSimple(ProcessDefinitions.SIGNAL);

        ejb.abortProcessInstance(pid);

        ProcessInstanceDesc log = ejb.getProcessInstanceById(pid);
        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getState().intValue()).isEqualTo(ProcessInstance.STATE_ABORTED);
    }

}
