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

package org.jbpm.casemgmt.impl.jms;

import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jbpm.casemgmt.api.event.CaseDataEvent;
import org.jbpm.casemgmt.api.event.CaseReopenEvent;
import org.jbpm.casemgmt.api.event.CaseRoleAssignmentEvent;
import org.jbpm.casemgmt.api.event.CaseStartEvent;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.model.OrganizationalEntity;

public class AsyncCaseInstanceAuditEventProducerTest {

    private ConnectionFactory connectionFactory;    
    private Queue queue;
    
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    
    private TextMessage message;
    
    private AsyncCaseInstanceAuditEventProducer logProducer;
    
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
        
        logProducer = new AsyncCaseInstanceAuditEventProducer();
        logProducer.setConnectionFactory(connectionFactory);
        logProducer.setQueue(queue);
        logProducer.setTransacted(true);
    }
    
    @After
    public void assertClose() throws JMSException {
        verify(producer, times(1)).close();
        verify(session, times(1)).close();
        verify(connection, times(1)).close();
    }
    
    @Test
    public void testCaseStarted() throws JMSException {
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl("TEST-01", "case");
        CaseStartEvent event = new CaseStartEvent("user", "TEST-01", "test", "case", caseFile, 1L);
        
        logProducer.afterCaseStarted(event);
        
        verify(message, times(1)).setStringProperty(eq("LogType"), eq("Case"));
        verify(message, times(1)).setIntProperty(eq("EventType"), eq(AFTER_CASE_STARTED_EVENT_TYPE));
        verify(producer, times(1)).setPriority(eq(8));
        verify(producer, times(1)).send(eq(message));
    }
    
    @Test
    public void testCaseReopen() throws JMSException {
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl("TEST-01", "case");
        CaseReopenEvent event = new CaseReopenEvent("user", "TEST-01", caseFile, "test", "case", new HashMap<>());
        
        logProducer.afterCaseReopen(event);
        
        verify(message, times(1)).setStringProperty(eq("LogType"), eq("Case"));
        verify(message, times(1)).setIntProperty(eq("EventType"), eq(AFTER_CASE_REOPEN_EVENT_TYPE));
        verify(producer, times(1)).setPriority(eq(4));
        verify(producer, times(1)).send(eq(message));
    }
    
    @Test
    public void testCaseRoleAssignmentAdded() throws JMSException {
        OrganizationalEntity entity = mock(OrganizationalEntity.class);
        when(entity.getId()).thenReturn("john");
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl("TEST-01", "case");
        CaseRoleAssignmentEvent event = new CaseRoleAssignmentEvent("user", "TEST-01", caseFile, "owner", entity);
        
        logProducer.afterCaseRoleAssignmentAdded(event);
        
        verify(message, times(1)).setStringProperty(eq("LogType"), eq("Case"));
        verify(message, times(1)).setIntProperty(eq("EventType"), eq(AFTER_CASE_ROLE_ASSIGNMENT_ADDED_EVENT_TYPE));
        verify(producer, times(1)).setPriority(eq(4));
        verify(producer, times(1)).send(eq(message));
    }
    
    @Test
    public void testCaseRoleAssignmentRemoved() throws JMSException {
        OrganizationalEntity entity = mock(OrganizationalEntity.class);
        when(entity.getId()).thenReturn("john");
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl("TEST-01", "case");
        CaseRoleAssignmentEvent event = new CaseRoleAssignmentEvent("user", "TEST-01", caseFile, "owner", entity);
        
        logProducer.afterCaseRoleAssignmentRemoved(event);
        
        verify(message, times(1)).setStringProperty(eq("LogType"), eq("Case"));
        verify(message, times(1)).setIntProperty(eq("EventType"), eq(AFTER_CASE_ROLE_ASSIGNMENT_REMOVED_EVENT_TYPE));
        verify(producer, times(1)).setPriority(eq(4));
        verify(producer, times(1)).send(eq(message));
    }
    
    @Test
    public void testCaseDataAdded() throws JMSException {
        Map<String, Object> data = new HashMap<>();
        data.put("test", "value");
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl("TEST-01", "case");
        CaseDataEvent event = new CaseDataEvent("user", "TEST-01", caseFile, "case", data);
        
        logProducer.afterCaseDataAdded(event);
        
        verify(message, times(1)).setStringProperty(eq("LogType"), eq("Case"));
        verify(message, times(1)).setIntProperty(eq("EventType"), eq(AFTER_CASE_DATA_ADDED_EVENT_TYPE));
        verify(producer, times(1)).setPriority(eq(4));
        verify(producer, times(1)).send(eq(message));
    }
    
    @Test
    public void testCaseDataRemoved() throws JMSException {
        Map<String, Object> data = new HashMap<>();
        data.put("test", "value");
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl("TEST-01", "case");
        CaseDataEvent event = new CaseDataEvent("user", "TEST-01", caseFile, "case", data);
        
        logProducer.afterCaseDataRemoved(event);
        
        verify(message, times(1)).setStringProperty(eq("LogType"), eq("Case"));
        verify(message, times(1)).setIntProperty(eq("EventType"), eq(AFTER_CASE_DATA_REMOVED_EVENT_TYPE));
        verify(producer, times(1)).setPriority(eq(4));
        verify(producer, times(1)).send(eq(message));
    }
}
