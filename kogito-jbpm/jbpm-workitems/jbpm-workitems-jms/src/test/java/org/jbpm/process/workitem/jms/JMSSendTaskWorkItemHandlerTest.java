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

package org.jbpm.process.workitem.jms;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JMSSendTaskWorkItemHandlerTest {

    @Mock
    ConnectionFactory connectionFactory;

    @Mock
    Destination destination;

    @Mock
    Connection connection;

    @Mock
    Session session;

    @Mock
    MessageProducer producer;

    @Mock
    BytesMessage message;

    @Test
    public void testSendMessage() throws Exception {
        ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);

        when(connectionFactory.createConnection()).thenReturn(connection);
        when(connection.createSession(anyBoolean(),
                                      anyInt())).thenReturn(session);
        when(session.createProducer(any(Destination.class))).thenReturn(producer);
        when(session.createBytesMessage()).thenReturn(message);

        doNothing().when(producer).close();
        doNothing().when(session).close();
        doNothing().when(connection).close();
        doNothing().when(producer).send(any(Message.class));

        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter("Signal",
                              "mysignal");
        workItem.setParameter("SignalProcessInstanceId",
                              "abcde");
        workItem.setParameter("SignalWorkItemId",
                              "12345");
        workItem.setParameter("SignalDeploymentId",
                              "deployment-123");
        workItem.setProcessInstanceId(123L);
        workItem.setDeploymentId("deploy-123");
        workItem.setParameter("Data",
                              "hello world");

        JMSSendTaskWorkItemHandler handler = new JMSSendTaskWorkItemHandler(connectionFactory,
                                                                            destination,
                                                                            false,
                                                                            false);

        handler.executeWorkItem(workItem,
                                manager);
        assertNotNull(manager.getResults());
        assertEquals(1,
                     manager.getResults().size());
        assertTrue(manager.getResults().containsKey(workItem.getId()));

        verify(message).writeBytes(bytesCaptor.capture());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesCaptor.getValue());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        String messageVal = (String) objectInputStream.readObject();
        assertTrue("hello world".equals(messageVal));
    }
}
