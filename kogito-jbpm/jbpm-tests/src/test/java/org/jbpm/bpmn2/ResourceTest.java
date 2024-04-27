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
package org.jbpm.bpmn2;

import org.jbpm.bpmn2.flow.MinimalModel;
import org.jbpm.bpmn2.flow.MinimalProcess;
import org.jbpm.bpmn2.flow.MultipleProcessInOneFile1Model;
import org.jbpm.bpmn2.flow.MultipleProcessInOneFile1Process;
import org.jbpm.bpmn2.flow.MultipleProcessInOneFile2Model;
import org.jbpm.bpmn2.flow.MultipleProcessInOneFile2Process;
import org.jbpm.test.utils.ProcessTestHelper;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.process.ProcessInstance;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceTest {

    @Test
    public void testResourceType() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<MinimalModel> process = MinimalProcess.newProcess(app);
        ProcessInstance<MinimalModel> processInstance = process.createInstance(process.createModel());
        processInstance.start();
        assertThat(processInstance).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);

    }

    @Test
    public void testMultipleProcessInOneFile() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<MultipleProcessInOneFile1Model> process1 = MultipleProcessInOneFile1Process.newProcess(app);
        org.kie.kogito.process.Process<MultipleProcessInOneFile2Model> process2 = MultipleProcessInOneFile2Process.newProcess(app);

        ProcessInstance<MultipleProcessInOneFile1Model> processInstance1 = process1.createInstance(process1.createModel());
        processInstance1.start();
        assertThat(processInstance1).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);

        ProcessInstance<MultipleProcessInOneFile2Model> processInstance2 = process2.createInstance(process2.createModel());
        processInstance2.start();
        assertThat(processInstance2).extracting(ProcessInstance::status).isEqualTo(ProcessInstance.STATE_COMPLETED);

    }

}
