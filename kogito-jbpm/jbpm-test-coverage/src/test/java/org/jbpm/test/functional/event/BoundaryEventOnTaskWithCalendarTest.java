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

package org.jbpm.test.functional.event;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;

import org.drools.core.time.TimeUtils;
import org.jbpm.process.core.timer.BusinessCalendarImpl;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

import static org.junit.Assert.*;

/**
 * This is a sample file to test a process.
 */
public class BoundaryEventOnTaskWithCalendarTest extends JbpmTestCase {

    public BoundaryEventOnTaskWithCalendarTest() {
        super(true, true);
    }

    @Test(timeout=10000)
    public void testProcess() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("deadline1", 1);
        addProcessEventListener(countDownListener);
        createRuntimeManager("org/jbpm/test/functional/event/BoundaryEventWithCalendar.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        ksession.getEnvironment().set("jbpm.business.calendar", new BusinessCalendarImpl());
        
        HashMap<String, Object> params = new HashMap<String, Object>();
        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);
        params.put("date", plusTwoSeconds.toString());


        ProcessInstance processInstance = ksession.startProcess("boundaryTimer", params);

        assertNodeTriggered(processInstance.getId(), "Start", "form1");

        countDownListener.waitTillCompleted();
        
        ProcessInstance pi = ksession.getProcessInstance(processInstance.getId());
        assertNull(pi);

        assertNodeTriggered(processInstance.getId(), "Koniec1");
        assertProcessInstanceCompleted(processInstance.getId());
    }

 
    @Test(timeout=10000)
    public void testProcessWithTimeCycleISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("deadline1", 1);
        addProcessEventListener(countDownListener);
        createRuntimeManager("org/jbpm/test/functional/event/BoundaryEventWithCycleCalendar.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        ksession.getEnvironment().set("jbpm.business.calendar", new BusinessCalendarImpl() {

			@Override
			public long calculateBusinessTimeAsDuration(String timeExpression) {
				timeExpression = adoptISOFormat(timeExpression);
		        return TimeUtils.parseTimeString(timeExpression);
		        
			}

			@Override
			public Date calculateBusinessTimeAsDate(String timeExpression) {
				timeExpression = adoptISOFormat(timeExpression);
	            return new Date(TimeUtils.parseTimeString(getCurrentTime() + timeExpression));
			}
        	
        });
        
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("date", "R3/PT2S");


        ProcessInstance processInstance = ksession.startProcess("boundaryTimer", params);

        assertNodeTriggered(processInstance.getId(), "Start", "form1");

        countDownListener.waitTillCompleted();
        
        ProcessInstance pi = ksession.getProcessInstance(processInstance.getId());
        assertNull(pi);

        assertNodeTriggered(processInstance.getId(), "Koniec1");
        assertProcessInstanceCompleted(processInstance.getId());
    }
}
