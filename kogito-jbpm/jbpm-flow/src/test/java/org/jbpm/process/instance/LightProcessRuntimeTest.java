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
package org.jbpm.process.instance;

import java.util.Collections;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.kogito.Application;
import org.kie.kogito.Config;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.AbstractProcessConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LightProcessRuntimeTest {

    private static WorkflowElementIdentifier one = WorkflowElementIdentifierFactory.fromExternalFormat("one");
    private static WorkflowElementIdentifier two = WorkflowElementIdentifierFactory.fromExternalFormat("two");
    private static WorkflowElementIdentifier three = WorkflowElementIdentifierFactory.fromExternalFormat("three");

    static class MyProcess {
        String result;
        RuleFlowProcess process = RuleFlowProcessFactory.createProcess("org.kie.api2.MyProcessUnit")
                // Header
                .name("HelloWorldProcess")
                .version("1.0")
                .packageName("org.jbpm")
                // Nodes
                .startNode(one).name("Start").done()
                .actionNode(two).name("Action")
                .action(ctx -> {
                    result = "Hello!";
                }).done()
                .endNode(three).name("End").done()
                // Connections
                .connection(one, two)
                .connection(two, three).validate().getProcess();
    }

    @Test
    void testInstantiation() {
        LightProcessRuntimeServiceProvider services =
                new LightProcessRuntimeServiceProvider();

        MyProcess myProcess = new MyProcess();
        LightProcessRuntimeContext rtc = new LightProcessRuntimeContext(Collections.singletonList(myProcess.process));

        Application application = mock(Application.class);
        Config config = mock(Config.class);
        when(application.config()).thenReturn(config);
        when(config.get(any())).thenReturn(mock(AbstractProcessConfig.class));
        when(application.get(Processes.class)).thenReturn(mock(Processes.class));
        LightProcessRuntime rt = new LightProcessRuntime(rtc, services, application);

        rt.startProcess(myProcess.process.getId());

        assertThat(myProcess.result).isEqualTo("Hello!");

    }

}
