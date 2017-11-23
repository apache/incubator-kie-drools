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

package org.jbpm.test.regression.subprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

import qa.tools.ikeeper.annotation.BZ;

import static org.junit.Assert.*;

public class MultipleInstancesSubprocessTest extends JbpmTestCase {

    private static final String TIMER_EVENT_PARENT =
            "org/jbpm/test/regression/subprocess/MultipleInstancesSubprocess-timerEvent-parent.bpmn2";
    private static final String TIMER_EVENT_PARENT_ID =
            "org.jbpm.test.regression.subprocess.MultipleInstancesSubprocess-timerEvent-parent";
    private static final String TIMER_EVENT_SUBPROCESS1 =
            "org/jbpm/test/regression/subprocess/MultipleInstancesSubprocess-timerEvent-subprocess1.bpmn2";
    private static final String TIMER_EVENT_SUBPROCESS2 =
            "org/jbpm/test/regression/subprocess/MultipleInstancesSubprocess-timerEvent-subprocess2.bpmn2";

    private static final String ENTRY_AND_EXIT_SCRIPT_PARENT =
            "org/jbpm/test/regression/subprocess/MultipleInstancesSubprocess-entryAndExitScript-parent.bpmn2";
    private static final String ENTRY_AND_EXIT_SCRIPT_PARENT_ID =
            "org.jbpm.test.regression.subprocess.MultipleInstancesSubprocess-entryAndExitScript-parent";
    private static final String ENTRY_AND_EXIT_SCRIPT_SUBPROCESS =
            "org/jbpm/test/regression/subprocess/MultipleInstancesSubprocess-entryAndExitScript-subprocess.bpmn2";
    private static final String ENTRY_AND_EXIT_SCRIPT_SUBPROCESS_ID =
            "org.jbpm.test.regression.subprocess.MultipleInstancesSubprocess-entryAndExitScript-subprocess";

    @Test
    @BZ("958390")
    public void testTimerEvent() throws Exception {
        KieSession ksession = createKSession(TIMER_EVENT_PARENT, TIMER_EVENT_SUBPROCESS1, TIMER_EVENT_SUBPROCESS2);
        TrackingProcessEventListener processEvents = new TrackingProcessEventListener();
        ksession.addEventListener(processEvents);

        Map<String, Object> params = new HashMap<String, Object>();
        List<String> clients = new ArrayList<String>();
        clients.add("A");
        clients.add("B");
        params.put("clients", clients);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(getCommands().newStartProcess(TIMER_EVENT_PARENT_ID, params));
        ksession.execute(getCommands().newBatchExecution(commands, null));
       
        String lastNodeName = "main-end";
        assertTrue( "Node '" + lastNodeName + "' was not triggered on time!", 
                processEvents.waitForNodeTobeTriggered(lastNodeName, 4000));

        Assertions.assertThat(processEvents.wasNodeTriggered("main-script1")).isTrue();
        Assertions.assertThat(processEvents.wasNodeTriggered("main-multiinstance1")).isTrue();
        Assertions.assertThat(processEvents.wasNodeTriggered("main-script2")).isTrue();
        Assertions.assertThat(processEvents.wasNodeTriggered("main-multiinstance2")).isTrue();
        Assertions.assertThat(processEvents.wasNodeTriggered(lastNodeName)).isTrue();
    }

    @Test
    @BZ("1123789")
    public void testEntryAndExitScript() {
        createRuntimeManager(ENTRY_AND_EXIT_SCRIPT_PARENT, ENTRY_AND_EXIT_SCRIPT_SUBPROCESS);
        KieSession ksession = getRuntimeEngine().getKieSession();

        ProcessInstance pi = ksession.startProcess(ENTRY_AND_EXIT_SCRIPT_PARENT_ID);
        logger.debug("Process with id = " + pi.getId() + " has just been started.");

        List<? extends VariableInstanceLog> varList = getLogService()
                .findVariableInstancesByName("onEntryScriptTriggered", false);
        Assertions.assertThat(varList).hasSize(1);
        Assertions.assertThat(Boolean.valueOf(varList.get(0).getValue())).isTrue();

        varList = getLogService().findVariableInstancesByName("onExitScriptTriggered", false);
        Assertions.assertThat(varList).hasSize(1);
        Assertions.assertThat(Boolean.valueOf(varList.get(0).getValue())).isTrue();

        List<? extends ProcessInstanceLog> processList = getLogService()
                .findProcessInstances(ENTRY_AND_EXIT_SCRIPT_PARENT_ID);
        Assertions.assertThat(processList).hasSize(1);
        Assertions.assertThat(processList.get(0).getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        processList = getLogService().findProcessInstances(ENTRY_AND_EXIT_SCRIPT_SUBPROCESS_ID);
        Assertions.assertThat(processList).hasSize(2);
        for (ProcessInstanceLog pil : processList) {
            Assertions.assertThat(pil.getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        }
    }

}
