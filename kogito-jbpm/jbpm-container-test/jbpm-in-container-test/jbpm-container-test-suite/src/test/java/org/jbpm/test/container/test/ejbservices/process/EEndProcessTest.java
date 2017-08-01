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

package org.jbpm.test.container.test.ejbservices.process;

import org.assertj.core.api.Assertions;
import org.jbpm.test.container.AbstractRuntimeEJBServicesTest;
import org.jbpm.test.container.groups.EAP;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.runtime.process.ProcessInstance;

@Category({EAP.class, WAS.class, WLS.class})
public class EEndProcessTest extends AbstractRuntimeEJBServicesTest {

    @Test
    public void testAbortProcessInstance() {
        Long pid = archive.startProcess(kieJar, HUMAN_TASK_PROCESS_ID);
        Assertions.assertThat(pid).isNotNull();
        
        processService.abortProcessInstance(pid);
        
        ProcessInstanceDesc log = runtimeDataService.getProcessInstanceById(pid);
        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getState()).isEqualTo(ProcessInstance.STATE_ABORTED);
    }
    
}
