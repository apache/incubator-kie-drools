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

package org.jbpm.services.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.test.listener.task.CountDownTaskEventListener;
import org.junit.Test;
import org.kie.internal.task.api.model.InternalTask;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public abstract class TaskReminderBaseTest extends HumanTaskServicesBaseTest {

    protected Wiser wiser;

    @Test(timeout=10000)
    public void testTaskReminderWithoutNotification() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.ReminderWithoutNotification));
        InternalTask task = (InternalTask) TaskFactory.evalTask(reader, vars);
        System.out.println("testTaskReminderWithoutNotification " + task.getTaskData().getStatus());

        assertNull(task.getDeadlines());

        long taskId = taskService.addTask(task, new HashMap<String, Object>());

        taskService.executeReminderForTask(taskId, "Luke Cage");
        countDownListener.waitTillCompleted();
        assertEquals(1, wiser.getMessages().size());

        String receiver = wiser.getMessages().get(0).getEnvelopeReceiver();
        assertEquals("tony@domain.com", receiver);
        MimeMessage msg = ((WiserMessage) wiser.getMessages().get(0)).getMimeMessage();
        assertEquals("You have a task ( Simple Test Task ) of process ( taskReminder )",
                msg.getSubject());

    }

    @Test(timeout=10000)
    public void testTaskReminderWithNotificationByTaskNostarted() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath
                .ReminderWithNotificationReserved));
        InternalTask task = (InternalTask) TaskFactory.evalTask(reader, vars);
        System.out.println("testTaskReminderWithNotificationByTaskNostarted " + task.getTaskData().getStatus());

        assertEquals(1, task.getDeadlines().getEndDeadlines().size());
        assertEquals(1, task.getDeadlines().getStartDeadlines().size());

        long taskId = taskService.addTask(task, new HashMap<String, Object>());
        taskService.executeReminderForTask(taskId, "Luke Cage");

        countDownListener.waitTillCompleted();
        assertEquals(2, wiser.getMessages().size());

        final List<String> list = new ArrayList<String>(2);
        list.add(wiser.getMessages().get(0).getEnvelopeReceiver());
        list.add(wiser.getMessages().get(1).getEnvelopeReceiver());

        assertTrue(list.contains("tony@domain.com"));
        assertTrue(list.contains("darth@domain.com"));

        MimeMessage msg = ((WiserMessage) wiser.getMessages().get(0)).getMimeMessage();
        assertEquals("ReminderWithNotificationReserved:you have new task to be started", msg.getSubject());
        assertEquals("task is not started", msg.getContent());

        msg = ((WiserMessage) wiser.getMessages().get(1)).getMimeMessage();
        assertEquals("ReminderWithNotificationReserved:you have new task to be started", msg.getSubject());
        assertEquals("task is not started", msg.getContent());
    }

    @Test(timeout=10000)
    public void testTaskReminderWithNotificationByTaskNoCompleted() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath
                .ReminderWithNotificationInProgress));
        InternalTask task = (InternalTask) TaskFactory.evalTask(reader, vars);
        System.out.println("testTaskReminderWithNotificationByTaskNoCompleted " + task.getTaskData().getStatus());

        assertEquals(1, task.getDeadlines().getEndDeadlines().size());
        assertEquals(1, task.getDeadlines().getStartDeadlines().size());
        long taskId = taskService.addTask(task, new HashMap<String, Object>());
        taskService.executeReminderForTask(taskId, "Luke Cage");

        countDownListener.waitTillCompleted();
        assertEquals(2, wiser.getMessages().size());

        List<String> list = new ArrayList<String>(2);
        list.add(wiser.getMessages().get(0).getEnvelopeReceiver());
        list.add(wiser.getMessages().get(1).getEnvelopeReceiver());

        assertTrue(list.contains("tony@domain.com"));
        assertTrue(list.contains("darth@domain.com"));

        MimeMessage msg = ((WiserMessage) wiser.getMessages().get(0)).getMimeMessage();
        assertEquals("ReminderWithNotificationInProgress:you have new task to be completed",
                msg.getSubject());
        assertEquals("task is not completed", msg.getContent());

        msg = ((WiserMessage) wiser.getMessages().get(1)).getMimeMessage();
        assertEquals("ReminderWithNotificationInProgress:you have new task to be completed",
                msg.getSubject());
        assertEquals("task is not completed", msg.getContent());
    }
}
