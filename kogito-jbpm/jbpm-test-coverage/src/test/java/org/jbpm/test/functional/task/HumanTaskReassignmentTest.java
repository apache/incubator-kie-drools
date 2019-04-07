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

package org.jbpm.test.functional.task;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jbpm.process.core.timer.BusinessCalendar;
import org.jbpm.process.core.timer.BusinessCalendarImpl;
import org.jbpm.test.JbpmTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;

import static org.junit.Assert.*;

public class HumanTaskReassignmentTest extends JbpmTestCase {

    private static final String PROCESS_FILE = "org/jbpm/test/functional/task/TaskReassignmentTimeout.bpmn2";
    private static final String PROCESS_ID = "com.bpms.functional.bpmn2.task.TaskReassignmentTimeout";

    private static final String JOHN = "john";
    private static final String MARY = "mary";

    private KieSession ksession;
    private TaskService taskService;
    private RuntimeManager runtimeManager;
    private RuntimeEngine engine;

    public HumanTaskReassignmentTest() {
        super(true, true);
    }

    @Before
    public void init() {
        runtimeManager = createRuntimeManager(PROCESS_FILE);
        engine = getRuntimeEngine();
        ksession = engine.getKieSession();
        taskService = engine.getTaskService();
    }
    
    @After
    public void cleanup() {
    	runtimeManager.disposeRuntimeEngine(engine);
    	runtimeManager.close();
    }

    private void testTimeout(boolean businessHour) throws InterruptedException {
        long pid = ksession.startProcess(PROCESS_ID).getId();
        long taskId = taskService.getTasksByProcessInstanceId(pid).get(0);
        String potOwner = getTaskPotentialOwner(taskId);
        assertEquals(JOHN, potOwner);
        Thread.sleep(2000);
        potOwner = getTaskPotentialOwner(taskId);
        assertEquals(businessHour ? MARY : JOHN, potOwner);
        
        ksession.abortProcessInstance(pid);
    }

    @Test
    public void testTimeout() throws InterruptedException {
        testTimeout(true);
    }

    @Test
    public void testTimeoutBusinessHour() throws InterruptedException {
        configureBusinessCalendar(true);
        testTimeout(true);
    }

    @Test
    public void testTimeoutNonBusinessHour() throws InterruptedException {
        configureBusinessCalendar(false);
        testTimeout(false);
    }

    private String getTaskPotentialOwner(long taskId) {
        Task task = taskService.getTaskById(taskId);
        assertNotNull(task);

        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        assertFalse(potentialOwners.isEmpty());
        return potentialOwners.get(0).getId();
    }

    private void configureBusinessCalendar(boolean businessHour) {
        Properties configuration = new Properties();

        if (businessHour) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            configuration.setProperty(BusinessCalendarImpl.START_HOUR, "0");
            configuration.setProperty(BusinessCalendarImpl.END_HOUR, "24");
            configuration.setProperty(BusinessCalendarImpl.HOURS_PER_DAY, "24");
            configuration.setProperty(BusinessCalendarImpl.DAYS_PER_WEEK, "7");
            configuration.setProperty(BusinessCalendarImpl.WEEKEND_DAYS, Integer.toString(dayOfWeek));
        } else {
            Date today = new Date();

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            Date tomorrow = c.getTime();

            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            configuration.setProperty(BusinessCalendarImpl.HOLIDAYS, sdf.format(today) + "," + sdf.format(tomorrow));
            configuration.setProperty(BusinessCalendarImpl.HOLIDAY_DATE_FORMAT, dateFormat);
        }

        BusinessCalendar businessCalendar = new BusinessCalendarImpl(configuration);
        ksession.getEnvironment().set("jbpm.business.calendar", businessCalendar);
    }

}
