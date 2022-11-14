/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2;

import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceTest extends JbpmBpmn2TestCase {

    @Test
    public void testResourceType() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MinimalProcess.bpmn2");
        kruntime.startProcess("Minimal");
    }

    @Test
    public void testMultipleProcessInOneFile() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultipleProcessInOneFile.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation");
        assertThat(processInstance).isNotNull();
        KogitoProcessInstance processInstance2 = kruntime.startProcess("Simple");
        assertThat(processInstance2).isNotNull();
    }

}
