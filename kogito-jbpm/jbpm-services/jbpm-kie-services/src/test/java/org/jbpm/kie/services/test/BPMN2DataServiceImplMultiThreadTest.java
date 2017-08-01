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

package org.jbpm.kie.services.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import org.drools.core.util.IoUtils;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.model.ProcessDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BPMN2DataServiceImplMultiThreadTest extends AbstractKieServicesBaseTest {

    @Before
    public void prepare() {
        configureServices();
    }

    @After
    public void cleanup() {

        cleanupSingletonSessionId();
        close();
    }

    @Test
    public void testBuildProcessDefinitionConcurrent() throws Exception {

        final List<ProcessDefinition> defs = new ArrayList<ProcessDefinition>();

        byte[] process1 = IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream(
                "/repo/processes/general/customtask.bpmn"));
        byte[] process2 = IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream(
                "/repo/processes/general/humanTask.bpmn"));

        final String process1Content = new String(process1, "UTF-8");
        final String process2Content = new String(process2, "UTF-8");

        final CyclicBarrier threadsFinishedBarrier = new CyclicBarrier(3);

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                ProcessDefinition def = bpmn2Service.buildProcessDefinition("test", process1Content, null, true);
                defs.add(def);

                waitForTheOtherThreads(threadsFinishedBarrier);
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                ProcessDefinition def = bpmn2Service.buildProcessDefinition("test", process2Content, null, true);
                defs.add(def);

                waitForTheOtherThreads(threadsFinishedBarrier);
            }
        });

        t1.start();
        t2.start();

        waitForTheOtherThreads(threadsFinishedBarrier);

        assertEquals(2, defs.size());
    }

}
