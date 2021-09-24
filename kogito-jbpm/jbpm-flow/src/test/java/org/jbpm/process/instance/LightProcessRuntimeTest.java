/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance;

import java.util.Collections;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LightProcessRuntimeTest {

    static class MyProcess {
        String result;
        RuleFlowProcess process = RuleFlowProcessFactory.createProcess("org.kie.api2.MyProcessUnit")
                // Header
                .name("HelloWorldProcess")
                .version("1.0")
                .packageName("org.jbpm")
                // Nodes
                .startNode(1).name("Start").done()
                .actionNode(2).name("Action")
                .action(ctx -> {
                    result = "Hello!";
                }).done()
                .endNode(3).name("End").done()
                // Connections
                .connection(1, 2)
                .connection(2, 3).validate().getProcess();
    }

    @Test
    void testInstantiation() {
        LightProcessRuntimeServiceProvider services =
                new LightProcessRuntimeServiceProvider();

        MyProcess myProcess = new MyProcess();
        LightProcessRuntimeContext rtc = new LightProcessRuntimeContext(Collections.singletonList(myProcess.process));

        LightProcessRuntime rt = new LightProcessRuntime(rtc, services);

        rt.startProcess(myProcess.process.getId());

        assertEquals("Hello!", myProcess.result);

    }

}
