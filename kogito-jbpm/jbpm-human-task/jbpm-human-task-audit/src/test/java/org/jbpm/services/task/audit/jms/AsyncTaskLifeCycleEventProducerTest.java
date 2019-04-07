/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.services.task.audit.jms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.jbpm.services.task.events.TaskEventImpl;
import org.jbpm.services.task.utils.TaskFluent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.mockito.ArgumentCaptor;

public class AsyncTaskLifeCycleEventProducerTest {
    private ConnectionFactory connectionFactory;    
    private Queue queue;
    
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    
    private TextMessage message;
    
    private AsyncTaskLifeCycleEventProducer logProducer;
    
    private TaskEvent event;
    
    @Before
    public void configure() throws JMSException {
        
        this.connectionFactory = mock(ConnectionFactory.class);        
        this.queue = mock(Queue.class);
        
        connection = mock(Connection.class);
        session = mock(Session.class);
        producer = mock(MessageProducer.class);
        
        message = mock(TextMessage.class);
        
        when(connectionFactory.createConnection()).thenReturn(connection);        
        when(connection.createSession(true, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        
        when(session.createProducer(any())).thenReturn(producer);
        when(session.createTextMessage(any())).thenReturn(message);
        
        logProducer = new AsyncTaskLifeCycleEventProducer() {

            @Override
            protected AuditTaskImpl getAuditTask(TaskPersistenceContext persistenceContext, Task ti) {
                Task task = new TaskFluent().setName("Updated name")
                        .addPotentialGroup("Knights Templer")
                        .setAdminUser("Administrator")
                        .setProcessId("")
                        .setProcessInstanceId(1L)
                        .setCreatedOn(new Date())
                        .getTask();
                return createAuditTask(task, new Date());
            }
            
        };
        logProducer.setConnectionFactory(connectionFactory);
        logProducer.setQueue(queue);
        logProducer.setTransacted(true);
        
        TaskContext context = mock(TaskContext.class);
        when(context.getUserId()).thenReturn("john");
        Task task = new TaskFluent().setName("This is my task name")
                .addPotentialGroup("Knights Templer")
                .setAdminUser("Administrator")
                .setProcessId("")
                .setProcessInstanceId(1L)
                .setCreatedOn(new Date())
                .getTask();
        event = new TaskEventImpl(task, context);
    }
    
    @After
    public void assertClose() throws JMSException {
        verify(producer, times(1)).close();
        verify(session, times(1)).close();
        verify(connection, times(1)).close();
    }
    
    @Test
    public void testAfterTaskStarted() throws JMSException {     
        logProducer.afterTaskStartedEvent(event);
        
        assertMessage("<type>STARTED</type>", 5);
    }
    
    @Test
    public void testAfterTaskActivated() throws JMSException {     
        logProducer.afterTaskActivatedEvent(event);
        
        assertMessage("<type>ACTIVATED</type>", 8);
    }

    @Test
    public void testAfterTaskClaimed() throws JMSException {     
        logProducer.afterTaskClaimedEvent(event);
        
        assertMessage("<type>CLAIMED</type>", 8);
    }

    @Test
    public void testAfterTaskSkipped() throws JMSException {     
        logProducer.afterTaskSkippedEvent(event);
        
        assertMessage("<type>SKIPPED</type>", 2);
    }

    @Test
    public void testAfterTaskStopped() throws JMSException {     
        logProducer.afterTaskStoppedEvent(event);
        
        assertMessage("<type>STOPPED</type>", 4);
    }    

    @Test
    public void testAfterTaskCompleted() throws JMSException {     
        logProducer.afterTaskCompletedEvent(event);
        
        assertMessage("<type>COMPLETED</type>", 2);
    }

    @Test
    public void testAfterTaskFailed() throws JMSException {     
        logProducer.afterTaskFailedEvent(event);
        
        assertMessage("<type>FAILED</type>", 2);
    }

    @Test
    public void testAfterTaskAdded() throws JMSException {     
        logProducer.afterTaskAddedEvent(event);
        
        assertMessage("<type>ADDED</type>", 9);
    }

    @Test
    public void testAfterTaskExited() throws JMSException {     
        logProducer.afterTaskExitedEvent(event);
        
        assertMessage("<type>EXITED</type>", 2);
    }
    
    @Test
    public void testAfterTaskReleased() throws JMSException {     
        logProducer.afterTaskReleasedEvent(event);
        
        assertMessage(null, 8);
    }
    
    @Test
    public void testAfterTaskResumed() throws JMSException {     
        logProducer.afterTaskResumedEvent(event);
        
        assertMessage("<type>RESUMED</type>", 6);
    }    

    @Test
    public void testAfterTaskSuspended() throws JMSException {     
        logProducer.afterTaskSuspendedEvent(event);
        
        assertMessage("<type>SUSPENDED</type>", 6);
    }

    @Test
    public void testAfterTaskForwarded() throws JMSException {     
        logProducer.afterTaskForwardedEvent(event);
        
        assertMessage("<type>FORWARDED</type>", 4);
    }

    @Test
    public void testAfterTaskDelegated() throws JMSException {     
        logProducer.afterTaskDelegatedEvent(event);
        
        assertMessage("<type>DELEGATED</type>", 4);
    }

    @Test
    public void testAfterTaskNominated() throws JMSException {     
        logProducer.afterTaskNominatedEvent(event);
        
        assertMessage("<type>NOMINATED</type>", 4);
    }
    
    @Test
    public void testBeforeTaskReleasedEvent() throws JMSException {     
        logProducer.beforeTaskReleasedEvent(event);
                
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(session).createTextMessage(argument.capture());
        String messageContent = argument.getValue();
        assertThat(messageContent)
            .isNotNull()
            .doesNotContain("<auditTask>")
            .contains("<org.jbpm.services.task.audit.impl.model.TaskEventImpl>")
            .contains("<type>RELEASED</type>");
        verify(message, times(1)).setStringProperty(eq("LogType"), eq("Task"));        
        verify(producer, times(1)).setPriority(eq(7));
        verify(producer, times(1)).send(eq(message));
    }
    
    @Test
    public void testAfterTaskUpdated() throws JMSException {     
        logProducer.afterTaskUpdatedEvent(event);
        
        assertMessage("<type>UPDATED</type>", 4);
    }
    
    @Test
    public void testAfterTaskReassigned() throws JMSException {     
        logProducer.afterTaskReassignedEvent(event);
        
        assertMessage("<type>DELEGATED</type>", 4);
    }

    @Test
    public void testAfterTaskOutputVariableChanged() throws JMSException {     
        Map<String, Object> vars = new HashMap<>();
        vars.put("test", "value");
        logProducer.afterTaskOutputVariableChangedEvent(event, vars);
        
        assertMessage("<type>UPDATED</type>", 2);
    }

    @Test
    public void testAfterTaskInputVariableChanged() throws JMSException {     
        Map<String, Object> vars = new HashMap<>();
        vars.put("test", "value");
        logProducer.afterTaskInputVariableChangedEvent(event, vars);
                
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(session).createTextMessage(argument.capture());
        String messageContent = argument.getValue();
        assertThat(messageContent)
            .isNotNull()
            .doesNotContain("<auditTask>")
            .doesNotContain("<org.jbpm.services.task.audit.impl.model.TaskEventImpl>")
            .contains("<taskInputs>");
        verify(message, times(1)).setStringProperty(eq("LogType"), eq("Task"));        
        verify(producer, times(1)).setPriority(eq(2));
        verify(producer, times(1)).send(eq(message));
    }
    
    /*
     * Helper methods
     */
    
    private void assertMessage(String eventStatus, int messagePriority) throws JMSException {
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(session).createTextMessage(argument.capture());
        String messageContent = argument.getValue();
        assertThat(messageContent)
            .isNotNull()
            .contains("<auditTask>")
            .contains("<name>This is my task name</name>");
        
        if (eventStatus != null) {
            assertThat(messageContent)        
            .contains("<org.jbpm.services.task.audit.impl.model.TaskEventImpl>")
            .contains(eventStatus);
        } else {
            assertThat(messageContent).doesNotContain("<org.jbpm.services.task.audit.impl.model.TaskEventImpl>");
        }

        verify(message, times(1)).setStringProperty(eq("LogType"), eq("Task"));        
        verify(producer, times(1)).setPriority(eq(messagePriority));
        verify(producer, times(1)).send(eq(message));
    }
}
