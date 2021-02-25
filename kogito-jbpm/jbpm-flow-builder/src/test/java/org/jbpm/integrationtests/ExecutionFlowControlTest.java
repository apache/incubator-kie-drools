/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExecutionFlowControlTest extends AbstractBaseTest {

    @Test
    public void testRuleFlowUpgrade() throws Exception {
        // Set the system property so that automatic conversion can happen
        System.setProperty("drools.ruleflow.port", "true");

        builder.add(ResourceFactory.newClassPathResource("ruleflow.drl", ExecutionFlowControlTest.class), ResourceType.DRL);
        builder.add(ResourceFactory.newClassPathResource("ruleflow40.rfm", ExecutionFlowControlTest.class), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        final List list = new ArrayList();
        kruntime.getKieSession().setGlobal("list", list);
        kruntime.getKieSession().fireAllRules();
        assertEquals(0, list.size());
        final KogitoProcessInstance processInstance = kruntime.startProcess("0");
        assertEquals(KogitoProcessInstance.STATE_COMPLETED,
                processInstance.getState());

        assertEquals(4,
                list.size());
        assertEquals("Rule1",
                list.get(0));
        list.subList(1, 2).contains("Rule2");
        list.subList(1, 2).contains("Rule3");
        assertEquals("Rule4",
                list.get(3));
        assertEquals(KogitoProcessInstance.STATE_COMPLETED,
                processInstance.getState());
        // Reset the system property so that automatic conversion should not happen
        System.setProperty("drools.ruleflow.port", "false");
    }

}
