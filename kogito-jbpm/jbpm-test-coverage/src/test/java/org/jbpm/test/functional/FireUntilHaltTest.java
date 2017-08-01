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

package org.jbpm.test.functional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.domain.Person;
import org.jbpm.test.listener.TrackingAgendaEventListener;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;

/**
 * Tests fireUntilHalt with jBPM process - this test makes sense only with singleton strategy
 */
public class FireUntilHaltTest extends JbpmTestCase {

    private static final String PROCESS = "org/jbpm/test/functional/FireUntilHalt.bpmn";
    private static final String PROCESS_ID = "org.jbpm.test.functional.FireUntilHalt";
    private static final String PROCESS_DRL = "org/jbpm/test/functional/FireUntilHalt.drl";

    public FireUntilHaltTest() {
        super(false);
    }

    
    @Test(timeout = 30000)
    public void testFireUntilHaltWithProcess() throws Exception {
        Map<String, ResourceType> res = new HashMap<String, ResourceType>();
        res.put(PROCESS, ResourceType.BPMN2);
        res.put(PROCESS_DRL, ResourceType.DRL);
        final KieSession ksession = createKSession(res);
        ksession.getEnvironment().set("org.jbpm.rule.task.waitstate", true);

        TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        ksession.addEventListener(listener);

        // thread for firing until halt
        ExecutorService thread = Executors.newSingleThreadExecutor();
        thread.submit(new Runnable() {
            @Override
            public void run() {
                ksession.fireUntilHalt();
            }
        });

        int wantedPersonsNum = 3;
        int unwantedPersonsNum = 2;

        Person p;
        // insert 3 wanted persons
        for (int i = 0; i < wantedPersonsNum; i++) {
            p = new Person("wanted person");
            p.setId(i);
            ksession.insert(p);
        }
        // insert 2 unwanted persons
        for (int i = 0; i < unwantedPersonsNum; i++) {
            p = new Person("unwanted person");
            p.setId(i);
            ksession.insert(p);
        }
        // wait for rule to fire
        Thread.sleep(100);
        // 8 persons should be acknowledged - person detector rule fired
        Assertions.assertThat(listener.ruleFiredCount("person detector"))
                .isEqualTo(wantedPersonsNum + unwantedPersonsNum);

        // we start defined process
        ksession.startProcess(PROCESS_ID);
        Thread.sleep(100);
        Assertions.assertThat(listener.ruleFiredCount("initial actions")).isEqualTo(1);
        Assertions.assertThat(listener.ruleFiredCount("wanted person detector")).isEqualTo(wantedPersonsNum);
        Assertions.assertThat(listener.ruleFiredCount("change unwanted person to wanted")).isEqualTo(unwantedPersonsNum);
        // 5 + 2 changed + 1 added
        Assertions.assertThat(listener.ruleFiredCount("person detector"))
                .isEqualTo(wantedPersonsNum * unwantedPersonsNum);
        Assertions.assertThat(listener.ruleFiredCount("closing actions")).isEqualTo(1);
        ksession.halt();
    }

}
