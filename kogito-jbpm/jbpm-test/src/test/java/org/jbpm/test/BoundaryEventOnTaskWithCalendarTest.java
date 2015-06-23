/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.test;

import java.util.Date;
import java.util.HashMap;

import org.drools.core.time.TimeUtils;
import org.jbpm.process.core.timer.BusinessCalendarImpl;
import org.joda.time.DateTime;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * This is a sample file to test a process.
 */
public class BoundaryEventOnTaskWithCalendarTest extends JbpmJUnitBaseTestCase {

    public BoundaryEventOnTaskWithCalendarTest() {
        super(true, true);
    }

    @Test
    public void testProcess() throws Exception {
        createRuntimeManager("BPMN2-BoundaryEventWithCalendar.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        ksession.getEnvironment().set("jbpm.business.calendar", new BusinessCalendarImpl());
        
        HashMap<String, Object> params = new HashMap<String, Object>();
        DateTime now = new DateTime(System.currentTimeMillis());
        now.plus(2000);
        params.put("date", now.toString());


        ProcessInstance processInstance = ksession.startProcess("boundaryTimer", params);

        assertNodeTriggered(processInstance.getId(), "Start", "form1");

        Thread.sleep(3000);

        assertNodeTriggered(processInstance.getId(), "Koniec1");
        assertProcessInstanceCompleted(processInstance.getId());
    }

 
    @Test
    public void testProcessWithTimeCycleISO() throws Exception {
        createRuntimeManager("BPMN2-BoundaryEventWithCycleCalendar.bpmn2");
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

        Thread.sleep(3000);

        assertNodeTriggered(processInstance.getId(), "Koniec1");
        assertProcessInstanceCompleted(processInstance.getId());
    }
}
