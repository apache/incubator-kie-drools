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

package org.jbpm.test.regression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.TrackingAgendaEventListener;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.jbpm.test.listener.TrackingRuleRuntimeEventListener;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import qa.tools.ikeeper.annotation.BZ;

public class RuleTest extends JbpmTestCase {

    private static final String ON_ENTRY_EVENT = "org/jbpm/test/regression/Rule-onEntryEvent.bpmn2";
    private static final String ON_ENTRY_EVENT_ID = "org.jbpm.test.regression.BusinessCalendar-timer";
    private static final String ON_ENTRY_EVENT_DRL = "org/jbpm/test/regression/Rule-onEntryEvent.drl";

    @Test
    @BZ("852095")
    public void testNoOnEntryEvent() {
        Map<String, ResourceType> res = new HashMap<String, ResourceType>();
        res.put(ON_ENTRY_EVENT, ResourceType.BPMN2);
        res.put(ON_ENTRY_EVENT_DRL, ResourceType.DRL);
        KieSession ksession = createKSession(res);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        TrackingAgendaEventListener agendaEvents = new TrackingAgendaEventListener();
        TrackingProcessEventListener processEvents = new TrackingProcessEventListener();
        TrackingRuleRuntimeEventListener ruleEvents = new TrackingRuleRuntimeEventListener();

        ksession.addEventListener(agendaEvents);
        ksession.addEventListener(processEvents);
        ksession.addEventListener(ruleEvents);

        commands.add(getCommands().newStartProcess(ON_ENTRY_EVENT_ID));
        commands.add(getCommands().newFireAllRules());

        ksession.execute(getCommands().newBatchExecution(commands, null));

        Assertions.assertThat(processEvents.wasProcessStarted(ON_ENTRY_EVENT_ID)).isTrue();
        Assertions.assertThat(processEvents.wasNodeTriggered("Rule")).isTrue();
        Assertions.assertThat(ruleEvents.wasInserted("OnEntry")).isTrue();
        Assertions.assertThat(agendaEvents.isRuleFired("dummyRule")).isTrue();
        Assertions.assertThat(ruleEvents.wasInserted("OnExit")).isTrue();
        Assertions.assertThat(processEvents.wasNodeLeft("Rule")).isTrue();
        Assertions.assertThat(processEvents.wasProcessCompleted(ON_ENTRY_EVENT_ID)).isTrue();
    }

}
