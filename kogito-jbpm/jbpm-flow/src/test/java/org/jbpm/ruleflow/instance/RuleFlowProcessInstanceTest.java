/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.ruleflow.instance;

import org.drools.kiesession.session.ProcessRuntimeFactory;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.*;

public class RuleFlowProcessInstanceTest extends AbstractBaseTest {

    private static String PROCESS_ID = "process.test";
    static {
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
    }

    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testStartProcessThrowException() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(PROCESS_ID);
        process.setName("test");
        process.setPackageName("org.mycomp.myprocess");

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime(process);
        assertThatThrownBy(() -> kruntime.startProcess(PROCESS_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testStartProcessDynamic() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId(PROCESS_ID);
        process.setName("test");
        process.setPackageName("org.mycomp.myprocess");
        process.setDynamic(true);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime(process);
        KogitoProcessInstance instance = kruntime.startProcess(PROCESS_ID);
        assertThat(instance).isNotNull();
    }

}
